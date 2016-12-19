(ns chatpool.db)

(def default-db
  {:active-panel :home-panel
   :hint nil
   :hint-input {:text ""
                :count ""}
   :user {:name ""
          :email ""}
   :chat {:enabled? false
          :ready? false
          :msg-input ""
          :msg-list (vector)}
   :rep-list []
   :rep-id nil})
