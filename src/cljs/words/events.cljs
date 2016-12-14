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
 :test/reply
 (fn [db msg]
   (assoc db :message msg)))

(re-frame/reg-event-db
 :test/send
 (fn [db [_ msg]]
   (chsk-send! [:test/send msg])
   db))

(re-frame/reg-event-db
 :example/toggle-broadcast
 (fn [db _]
   (chsk-send! [:example/toggle-broadcast])
   db))

(re-frame/reg-event-db
 :example/test-rapid-push
 (fn [db _]
   (chsk-send! [:example/test-rapid-push])
   db))

(re-frame/reg-event-db
 :name
 (fn [db [_ v]]
   (assoc db :name v)))

(re-frame/reg-event-db
 :new-hint
 (fn [db [_ v]]
   (assoc db :new-hint v)))

(re-frame/reg-event-db
 :count
 (fn [db [_ v]]
   (assoc db :count v)))

(re-frame/reg-event-db
 :selected-word
 (fn [db [_ v]]
   (assoc db :selected-word v)))
