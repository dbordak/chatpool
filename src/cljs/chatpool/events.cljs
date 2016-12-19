(ns chatpool.events
    (:require [re-frame.core :as re-frame]
              [taoensso.timbre :as timbre]
              [chatpool.db :as db]
              [chatpool.ws :refer [chsk-send!]]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 :test/send
 (fn [db [_ msg]]
   (chsk-send! [:test/send msg])
   db))

(re-frame/reg-event-db
 :user
 (fn [db [_ v]]
   (assoc db :user
          (merge (:user db) v))))

(re-frame/reg-event-db
 :user/name
 (fn [db [_ v]]
   (assoc-in db [:user :name] v)))

(re-frame/reg-event-db
 :user/email
 (fn [db [_ v]]
   (assoc-in db [:user :email] v)))

(re-frame/reg-event-db
 :hint-input-changed
 (fn [db [_ v]]
   (assoc db :hint-input
          (merge (:hint-input db) v))))

(re-frame/reg-event-db
 :chat/msg-input
 (fn [db [_ v]]
   (assoc-in db [:chat :msg-input] v)))

(re-frame/reg-event-db
 :scroll-chatbox
 (fn [db _]
   (let [scroller (.getElementById js/document "chat-scrollbox")]
     (set! (.-scrollTop scroller) (.-scrollHeight scroller)))
   db))

(re-frame/reg-event-db
 :chat/recv-msg
 (fn [db [_ from msg]]
   (assoc-in db [:chat :msg-list]
          (conj (-> db :chat :msg-list)
                {:name from :body msg}))))

(re-frame/reg-event-db
 :chat/enabled?
 (fn [db [_ v]]
   (assoc-in db [:chat :enabled?] v)))

(re-frame/reg-event-db
 :chat/ready?
 (fn [db [_ v]]
   (assoc-in db [:chat :ready?] v)))

(re-frame/reg-event-db
 :chat/send-msg
 (fn [db _]
   (chsk-send! [:chat/msg (-> db :chat :msg-input)])
   (assoc-in db [:chat :msg-input] "")))

(re-frame/reg-event-db
 :chat/send-user-info
 (fn [db _]
   (chsk-send! [:chat/user (:user db)] 5000
               (fn [cb-reply]
                 (when cb-reply
                   (re-frame/dispatch [:chat/ready? true]))))
   ;; not going to clear the forms since we might want these values,
   ;; and these <input>s disappear as soon as they're set
   db))

(re-frame/reg-event-db
 :rep-list
 (fn [db [_ v]]
   (assoc db :rep-list v)))

(re-frame/reg-event-db
 :rep-login
 (fn [db [_ v]]
   (when (:rep-id db)
     (chsk-send! [:rep/logout (:rep-id db)]))
   (chsk-send! [:rep/login (:id v)] 5000
               (fn [cb-reply]
                 (when cb-reply
                   (re-frame/dispatch [:chat/ready? true])
                   (re-frame/dispatch [:chat/enabled? true]))))
   (-> db
       (assoc :rep-id (:id v))
       (assoc :user {:name (:first_name v) :email ""}))))

(re-frame/reg-event-db
 :rep-logout
 (fn [db [_ v]]
   (chsk-send! [:rep/logout (:rep-id db)])
   (re-frame/dispatch [:chat/ready? false])
   (re-frame/dispatch [:chat/enabled? false])
   (-> db
       (assoc :rep-id nil)
       (assoc-in [:chat :msg-list] (vector))
       (assoc :user {:name "" :email ""}))))

(re-frame/reg-event-db
 :idle-rep-list
 (fn [db [_ v]]
   (assoc db :idle-rep-list v)))
