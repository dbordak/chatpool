(ns chatpool.db
  (:require [yesql.core :refer [defqueries]]))

;; This file pretty much just exists to namespace the SQL functions.

(def db {:dbtype "sqlite"
         :dbname "chatpool.db"})

(defqueries "sql/reps.sql" {:connection db})
(defqueries "sql/chatpool.sql" {:connection db})
