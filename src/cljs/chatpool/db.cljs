(ns chatpool.db)

(def default-db
  {:hint nil
   :hint-input {:text ""
                :count ""}
   :user {:name ""
          :email ""}
   :chat {:enabled? true
          :ready? false
          :msg-input ""
          :msg-list (vector)}})
