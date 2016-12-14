(ns words.server
  (:require [words.handler :refer [handler start-router! start-example-broadcaster!]]
            [config.core :refer [env]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

(defn start-server! []
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-server handler {:port port :join? false})))

(defn -main [& args]
  (start-server!)
  (start-router!)
  (start-example-broadcaster!))
