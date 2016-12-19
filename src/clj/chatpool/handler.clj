(ns chatpool.handler
  (:require [compojure.core :refer [GET POST defroutes context]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response content-type]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response]]
            [chatpool.ws :refer [ring-ajax-get-or-ws-handshake ring-ajax-post]]
            [chatpool.db :as db]))

(defroutes api
  (GET "/rep/list" [] (db/list-reps))
  (context "/rep/:rep-id{[0-9]+}" [rep-id]
           (GET "/name" [] (db/get-rep-name {:id rep-id}))
           ;; TODO: POST name
           (GET "/conv" [] (db/get-rep-convs {:id rep-id})))
  (context "/conv/:conv-id{[0-9]+}" [conv-id]
           (GET "/" [] (db/get-conv {:id conv-id}))
           ;; TODO: DELETE conv
           ))

(defroutes routes
  (GET "/" [] (content-type
               (resource-response "index.html" {:root "public"})
               "text/html"))
  (GET "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post req))
  (context "/api/v1" [format]
           (if (= format "json")
             (wrap-json-response api)
             api))
  (resources "/"))

(def handler (wrap-defaults #'routes site-defaults))
(def dev-handler (wrap-reload handler))
