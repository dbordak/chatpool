(ns words.events
    (:require [re-frame.core :as re-frame]
              [words.db :as db]
              [words.ws :refer [chsk-send!]]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [db _]
   (merge db db/default-db)))

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

(re-frame/reg-event-db

(re-frame/reg-event-db
 :name
 (fn [db [_ v]]
   (assoc db :name v)))

(re-frame/reg-event-db
 :new-hint
 (fn [db [_ v]]
   (assoc db :new-hint
          (merge (:new-hint db) v))))

(re-frame/reg-event-db
 :selected-word
 (fn [db [_ v]]
   (assoc db :selected-word v)))

;;Add the new message to the front so we don't need to reverse the
;;list for display.
(re-frame/reg-event-db
 :new-message
 (fn [db [_ v]]
   (assoc db :messages
          (cons v (:messages db)))))

(re-frame/reg-event-db
 :enable-chat
 (fn [db]
   (assoc db :chat? true)))

(re-frame/reg-event-db
 :disable-chat
 (fn [db]
   (assoc db :chat? false)))

(re-frame/reg-event-db
 :toggle-chat
 (fn [db]
   (assoc db :chat? (not (:chat? db)))))
