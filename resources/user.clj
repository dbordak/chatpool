(ns user
  (:require [yesql.core :refer [defqueries]]))

(def db {:dbtype "sqlite"
         :dbname "chatpool.db"
         :create true})

(defqueries "sql/chatpool.sql" {:connection db})
(defqueries "sql/reps.sql" {:connection db})

(defn migrate []
  (create-rep-table!)

  ;; Add some example reps since I'm not going to add an API for new ones yet.
  (let [last-name "Cook"]
    (doseq [first-name ["Alice" "Bob" "Carlos" "Dan" "Erin"]]
      (create-rep<! {:? [first-name last-name]})))

  (create-conv-table!)
  (create-cust-table!)
  (create-msg-table!))
