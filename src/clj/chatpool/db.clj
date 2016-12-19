(ns chatpool.db
  (:require [yesql.core :refer [defqueries]]))

;; This file pretty much just exists to namespace the SQL functions.

(def db {:classname "org.postgresql.Driver"
         :subprotocol "postgresql"
         :subname (or (System/getenv "DATABASE_URL")
                      "//localhost:5432/chatpool")})

(defqueries "sql/reps.sql" {:connection db})
(defqueries "sql/chatpool.sql" {:connection db})
