(ns chatpool.ws
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require
   [cljs.core.async :as async :refer (put! chan)]
   [taoensso.sente  :as sente :refer (cb-success?)]
   [taoensso.timbre :as timbre]
   [cljs-time.core :refer [to-default-time-zone]]
   [cljs-time.format :refer [parse unparse formatter]]
   [re-frame.core :refer [dispatch]]))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {:type :auto})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

(defmulti push-msg-handler (fn [[id _]] id))

(defmethod push-msg-handler :default
  [[_ data]]
  (timbre/debug "Unhandled event: " _ data))

(defmethod push-msg-handler :chat/msg
  [[_ {:as data :keys [from msg time]}]]
  (dispatch [:chat/recv-msg from msg
             (unparse (formatter "HH:mm")
                      (to-default-time-zone (parse time)))]))

(defmethod push-msg-handler :idle-reps/update
  [[_ {:as data :keys [list]}]]
  (dispatch [:idle-rep-list list]))

(defmethod push-msg-handler :chat/cust-page
  [[_ {:as data :keys [page]}]]
  (dispatch [:cust-page page]))

(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id)

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler
  :default ; Default/fallback case (no other matching handler)
  [{:as ev-msg :keys [event]}]
  (timbre/debug "Unhandled event: " event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state-map new-state-map] ?data]
    (if (:first-open? new-state-map)
      (timbre/debug "Channel socket successfully established!: " new-state-map)
      (timbre/debug "Channel socket state change: "              new-state-map))))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (push-msg-handler ?data))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (timbre/debug "Handshake: " ?data)))

(defonce router_ (atom nil))
(defn stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-client-chsk-router!
           ch-chsk event-msg-handler)))

;; Sending

(defn user-login! [user page cb-fn]
  (chsk-send! [:chat/user {:user user :page page}] 5000
              cb-fn))
