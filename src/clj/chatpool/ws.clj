(ns chatpool.ws
  (:require [clojure.core.async :as async :refer (<! <!! >! >!! put! chan go go-loop)]
            [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]
            [chatpool.db :as db]))

;; Sente boilerplate
(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! (get-sch-adapter)
                                  {:user-id-fn (fn [ring-req]
                                                 (:client-id ring-req))})]

  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv)
  (def chsk-send!                    send-fn)
  (def connected-uids                connected-uids))

;; Debugging, prints every time someone connects/disconnects
(add-watch connected-uids :connected-uids
  (fn [_ _ old new]
    (when (not= old new)
      (infof "Connected uids change: %s" new))))

(defmulti -event-msg-handler :id)

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (future (-event-msg-handler ev-msg))) ; thread pool

(defmethod -event-msg-handler
  :default ; Default/fallback case (no other matching handler)
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    (debugf "Unhandled event: %s" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))

(defmethod -event-msg-handler :chat/msg
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (debugf "Chat message from %s" uid)
  (let [from (db/get-user-name uid)
        rep (db/get-rep uid)
        conv (if rep
               (db/get-rep-conv (:id rep))
               (db/get-cust-conv uid))
        partner (if rep
                  (:cust_uid conv)
                  (:uid (db/get-rep (:rep_id conv))))
        db-entry (db/create-msg<! (:id conv)
                                  (if rep "rep" "cust")
                                  ?data)
        msg [:chat/msg
             {:what-is-this "A chat message"
              :how-often "Whenever one is received"
              :from (if from
                      (:first_name from)
                      "?")
              :msg ?data
              ;; Lazy way of changing between those two very similar
              ;; timestamp formats. It feels like every time I work
              ;; with time I have to do this.
              :time (clojure.string/replace (str (:time db-entry)) " " "T")}]]
    (chsk-send! partner msg)
    (chsk-send! uid msg)))

(defmethod -event-msg-handler :chat/cust-page
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (debugf "customer switched page: %s %s" uid ?data)
  (let [conv (db/get-cust-conv uid)
        rep (db/get-rep (:rep_id conv))]
    (db/set-cust-page! uid (name ?data))
    (chsk-send! (:uid rep)
                [:chat/cust-page
                 {:what-is-this "The connected customer's current page."
                  :to-whom (:uid rep)
                  :page ?data}])))

(defmethod -event-msg-handler :chat/user
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (let [user (:user ?data)
        page (:page ?data)]
    (debugf "new user: %s" user)
    (db/create-cust<! uid
                      [(:name user) ""]
                      (:email user)
                      (name page))
    (let [rep (rand-nth (db/list-idle-reps))]
      (debugf "pairing with %s" (:first_name rep))
      (db/create-conv<! uid (:id rep))
      (chsk-send! (:uid rep)
                  [:chat/cust-page
                   {:what-is-this "The connected customer's current page."
                    :to-whom (:uid rep)
                    :page page}])))
  (when ?reply-fn
    (?reply-fn true)))

(defn broadcast-idle-list! []
  (let [uids (:any @connected-uids)]
    (doseq [to-uid uids]
      (chsk-send! to-uid
                  [:idle-reps/update
                   {:what-is-this "The current list of idle reps"
                    :how-often "Whenever a rep's online/busy status changes"
                    :to-whom to-uid
                    :list (db/list-idle-reps)}]))))

(defmethod -event-msg-handler :rep/login
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (db/rep-online! ?data uid)
  (debugf "rep logged in: %s" ?data)
  (broadcast-idle-list!)
  (when ?reply-fn
    (?reply-fn true)))

(defmethod -event-msg-handler :rep/logout
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (let [rep (db/get-rep uid)]
    (debugf "rep logged out: %s" (:id rep))
    (db/rep-offline! (:id rep))
    (db/end-conv-for-rep! (:id rep)))
  (broadcast-idle-list!)
  (when ?reply-fn
    (?reply-fn true)))

(defmethod -event-msg-handler :chat/end
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (let [rep (db/get-rep uid)]
    (if rep
      (db/end-conv-for-rep! (:id rep))
      (db/end-conv-for-cust! uid))
    (broadcast-idle-list!)))

;; This is called automatically when the tab is closed/refreshed.
(defmethod -event-msg-handler :chsk/uidport-close
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  ;; Note: automatically called with uid as sole parameter, so ?data == uid
  (debugf "client closed tab: %s" uid)
  (let [rep (db/get-rep uid)]
    (if rep
      ;; Asymmetric behavior. Basically, if a rep disconnects without
      ;; logging out, presumably it was a mistake and they can
      ;; reconnect. If a customer disconnects, there's no recourse so
      ;; we might as well end the conversation to free up the rep.
      (db/rep-offline! (:id rep))
      (db/end-conv-for-cust! uid))
    (broadcast-idle-list!)))

(defonce router_ (atom nil))
(defn stop-router! [] (when-let [stop-fn @router_] (stop-fn)))
(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router!
           ch-chsk event-msg-handler)))
