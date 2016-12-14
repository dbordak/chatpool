(ns words.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [words.events]
              [words.subs]
              [words.routes :as routes]
              [words.views :as views]
              [words.config :as config]
              [words.ws :as ws]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root)
  (ws/start-router!))
