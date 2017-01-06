(ns chatpool.db)

(def default-db
  {:active-panel :home-panel
   :hint nil
   :hint-input {:text ""
                :count ""}
   :user {:name ""
          :email ""}
   :chat {:enabled? false ; Whether the chat panel appears. Always
                          ; enabled for reps.
          :ready? false   ; Whether the chat has sufficient
                          ; information to connect (i.e. name)
          :msg-input ""
          :msg-list (vector)}
   :modal {:end-chat? false}
   :cust-page nil
   :rep-list []
   :idle-rep-list []
   :rep-id nil})
