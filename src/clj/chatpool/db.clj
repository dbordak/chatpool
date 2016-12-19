(ns chatpool.db
  (:require [yesql.core :refer [defqueries]]))

;; This file pretty much just exists to namespace the SQL functions.

(def db (or (System/getenv "DATABASE_URL")
            {:classname "org.postgresql.Driver"
             :subprotocol "postgresql"
             :subname "//localhost:5432/chatpool"}))

(defqueries "sql/reps.sql" {:connection db})
(defqueries "sql/chatpool.sql" {:connection db})
