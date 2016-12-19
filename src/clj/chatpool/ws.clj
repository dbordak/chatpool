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
  (let [uids (:any @connected-uids)
        from (first (db/get-user-name {:uid uid}))
        rep (first (db/get-rep-by-uid {:uid uid}))
        conv (if rep
               (first (db/get-rep-conv {:id (:id rep)}))
               (first (db/get-cust-conv {:uid uid})))
        partner (if rep
                  (:cust_uid conv)
                  (:uid (first (db/get-rep {:id (:rep_id conv)}))))
        msg [:chat/msg
             {:what-is-this "A chat message"
              :how-often "Whenever one is received"
              :from (if from
                      (:first_name from)
                      "?")
              :msg ?data}]]
    (db/create-msg<! {:sender (if rep "rep" "cust")
                      :body ?data
                      :id (:id conv)})
    (chsk-send! partner msg)
    (chsk-send! uid msg)))

(defmethod -event-msg-handler :chat/user
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (let [name (:name ?data)
        email (:email ?data)]
    (debugf "new user: %s" (:name ?data))
    (db/create-cust<! {:? [name ""] :email email :uid uid})
    (let [rep (rand-nth (db/list-idle-reps))]
      (debugf "pairing with %s" (:first_name rep))
      (db/create-conv<! {:cust_uid uid :rep_id (:id rep)})))
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
  (db/rep-online! {:id ?data :uid uid})
  (debugf "rep logged in: %s" ?data)
  (broadcast-idle-list!)
  (when ?reply-fn
    (?reply-fn true)))

(defmethod -event-msg-handler :rep/logout
  [{:as ev-msg :keys [event id uid ?data ring-req ?reply-fn send-fn]}]
  (db/rep-offline! {:id ?data})
  (debugf "rep logged out: %s" ?data)
  (broadcast-idle-list!)
  (when ?reply-fn
    (?reply-fn true)))

(defonce router_ (atom nil))
(defn stop-router! [] (when-let [stop-fn @router_] (stop-fn)))
(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router!
           ch-chsk event-msg-handler)))
