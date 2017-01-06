(ns chatpool.db
  (:require [yesql.core :refer [defqueries]]))

;; This file pretty much just exists to namespace the SQL functions.

(def db {:classname "org.h2.Driver"
         :subprotocol "h2:file"
         :subname "./chatpool"
         :user "sa"
         :password ""
         :create true})

(defqueries "sql/chatpool.sql" {:connection db})

;; Representatives

(defn create-rep<! [name]
  (-create-rep<! {:? name}))

(defn get-rep [id]
  (first (cond
           (string? id) (-get-rep-by-uid {:uid id})
           (number? id) (-get-rep-by-id {:id id})
           ;; TODO: Some kind of error
           :else nil)))

(defn get-rep-name [id]
  (first (-get-rep-name {:id id})))

(defn update-rep-name! [id name]
  (-update-rep-name! {:id id :? name}))

(defn rep-online! [id uid]
  (-rep-online! {:id id :uid uid}))

(defn rep-offline! [id]
  (-rep-offline! {:id id}))

;; Conversations

(defn create-conv<! [cust-uid rep-id]
  (-create-conv<! {:cust_uid cust-uid :rep_id rep-id}))

(defn get-conv [id]
  (first (-get-conv {:id id})))

(defn delete-conv! [id]
  (-delete-conv! {:id id})
  (-delete-msgs! {:id id}))

(defn get-rep-convs [id]
  (-get-rep-convs {:id id}))

(defn get-rep-conv [id]
  (first (-get-rep-conv {:id id})))

(defn get-cust-conv [uid]
  (first (-get-cust-conv {:uid uid})))

(defn get-cust-rep [uid]
  (first (-get-cust-rep {:uid uid})))

(defn end-conv! [id]
  (-end-conv! {:id id}))

;; Customers

(defn create-cust<! [uid name email page]
  (-create-cust<! {:uid uid :? name :email email :page page}))

(defn set-cust-page! [uid page]
  (-set-cust-page! {:uid uid :page page}))

(defn create-msg<! [id sender body]
  (-create-msg<! {:id id :sender sender :body body}))

(defn get-conv-msgs [id]
  (-get-conv-msgs {:id id}))

(defn get-user-name [uid]
  (first (-get-user-name {:uid uid})))
