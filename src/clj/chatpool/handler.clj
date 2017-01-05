(ns chatpool.handler
  (:require [compojure.core :refer [GET POST DELETE defroutes context]]
            [compojure.route :refer [resources]]
            [compojure.coercions :refer [as-int]]
            [ring.util.response :refer [resource-response content-type]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.format-response :refer [wrap-json-response wrap-clojure-response]]
            [chatpool.ws :refer [ring-ajax-get-or-ws-handshake ring-ajax-post]]
            [chatpool.db :as db]))

(defroutes site
  (GET "/" [] (content-type
               (resource-response "index.html" {:root "public"})
               "text/html"))
  (GET "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post req)))

(defroutes api
  (context "/rep/list" []
           (GET "/" [] (db/list-reps))
           (GET "/idle" [] (db/list-idle-reps)))
  (context "/rep/:rep-id" [rep-id :<< as-int]
           (GET "/name" [] (list (db/get-rep-name rep-id)))
           (POST "/name" [first-name last-name]
                 (db/update-rep-name! rep-id [first-name last-name])
                 "")
           (GET "/conv" [] (db/get-rep-convs rep-id)))
  (context "/conv/:conv-id" [conv-id :<< as-int]
           (GET "/" [] (db/get-conv conv-id))
           (DELETE "/" []
                   (let [msg-count (db/delete-conv! conv-id)]
                     (str "Deleted " msg-count " messages.")))
           (GET "/msgs" [] (db/get-conv-msgs conv-id))))

(defroutes handler
  (wrap-defaults
   (context "/api/v1" [format]
            (if (= format "json")
              (wrap-json-response api)
              (wrap-clojure-response api)))
   api-defaults)
  (wrap-defaults site site-defaults))

(def dev-handler (wrap-reload handler))
