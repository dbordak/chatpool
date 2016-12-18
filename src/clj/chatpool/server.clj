(ns chatpool.server
  (:require [chatpool.handler :refer [handler]]
            [chatpool.ws :refer [start-router!]]
            [config.core :refer [env]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

(defn start-server! []
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-server handler {:port port :join? false})))

(defn -main [& args]
  (start-server!)
  (start-router!))
