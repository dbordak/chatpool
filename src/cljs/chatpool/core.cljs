(ns chatpool.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [re-frisk.core :refer [enable-re-frisk!]]
              [taoensso.encore :as encore]
              [chatpool.events]
              [chatpool.subs]
              [chatpool.routes :as routes]
              [chatpool.views :as views]
              [chatpool.config :as config]
              [chatpool.ws :as ws]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (encore/ajax-lite
   "/api/v1/rep/list/idle"
   {:method :get :resp-type :edn}
   (fn [resp]
     (when (:?content resp)
       (re-frame/dispatch [:idle-rep-list (:?content resp)]))))
  (dev-setup)
  (mount-root)
  (ws/start-router!))
