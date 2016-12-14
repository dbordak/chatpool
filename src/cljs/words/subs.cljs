(ns words.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 :new-hint
 (fn [db]
   (:new-hint db)))

(re-frame/reg-sub
 :count
 (fn [db]
   (:count db)))

(re-frame/reg-sub
 :hint
 (fn [db]
   (:hint db)))

(re-frame/reg-sub
 :selected-word
 (fn [db]
   (:selected-word db)))

(re-frame/reg-sub
 :word-list
 (fn [db]
   (:word-list db)))
