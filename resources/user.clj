(ns user
  (:require [chatpool.db :refer :all])
  (:require [yesql.core :refer [defqueries]]))

(defqueries "sql/migrations.sql" {:connection db})

(defn migrate []
  (create-rep-table!)

  ;; Add some example reps since I'm not going to add an API for new ones yet.
  (let [last-name "Cook"]
    (doseq [first-name ["Alice" "Bob" "Carlos" "Dan" "Erin"]]
      (create-rep<! [first-name last-name])))

  (create-conv-table!)
  (create-cust-table!)
  (create-msg-table!))

(defn drop-all []
  (drop-rep-table!)
  (drop-conv-table!)
  (drop-cust-table!)
  (drop-msg-table!))
