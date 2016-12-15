(ns words.db)

(def default-db
  {:name ""
   :messages []
   :hint nil
   :new-hint {:text ""
              :count ""}
   :word-list ["apple" "banana" "truck"]
   :selected-word "banana"
   :chat? true})
