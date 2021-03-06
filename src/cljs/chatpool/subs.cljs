(ns chatpool.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 :hint-input
 (fn [db]
   (:hint-input db)))

(re-frame/reg-sub
 :count
 (fn [db]
   (:count db)))

(re-frame/reg-sub
 :hint
 (fn [db]
   (:hint db)))

(re-frame/reg-sub
 :user
 (fn [db]
   (:user db)))

(re-frame/reg-sub
 :chat
 (fn [db]
   (:chat db)))

(re-frame/reg-sub
 :chat/msg-input
 (fn [db]
   (-> db :chat :msg-input)))

(re-frame/reg-sub
 :chat/msg-list
 (fn [db]
   (-> db :chat :msg-list)))

(re-frame/reg-sub
 :chat/enabled?
 (fn [db]
   (-> db :chat :enabled?)))

(re-frame/reg-sub
 :chat/ready?
 (fn [db]
   (-> db :chat :ready?)))

(re-frame/reg-sub
 :rep-list
 (fn [db]
   (:rep-list db)))

(re-frame/reg-sub
 :rep-id
 (fn [db]
   (:rep-id db)))

(re-frame/reg-sub
 :idle-rep-list
 (fn [db]
   (:idle-rep-list db)))

(re-frame/reg-sub
 :cust-page
 (fn [db]
   (:cust-page db)))

(re-frame/reg-sub
 :modal/end-chat?
 (fn [db]
   (-> db :modal :end-chat?)))
