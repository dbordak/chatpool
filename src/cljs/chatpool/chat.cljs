(ns chatpool.chat
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [reagent.core :as reagent]))

(defn render-msg [msg]
  [:span [:span (:name msg)] (str ": " (:body msg))])

(defn container []
  "Container for making the chat separately scrollable"
  (let [msg-list (re-frame/subscribe [:chat/msg-list])]
    (fn []
      [re-com/scroller
       :height "100%"
       :child [re-com/v-box
               :children (doall (map render-msg @msg-list))]])))

(defn msg-input []
  "Message input form"
  (let [msg-input (re-frame/subscribe [:chat/msg-input])]
    (fn []
      [:form {:on-submit #(do (re-frame/dispatch [:chat/send-msg])
                              (.preventDefault %))
              :style {:margin "10px"}}
       [re-com/input-text
        :placeholder "Type a message!"
        :width "100%"
        :model @msg-input
        :change-on-blur? false
        :on-change #(re-frame/dispatch-sync [:chat/msg-input %])]])))

(defn end-button []
  "Closes the chat panel"
  [re-com/md-icon-button
   :md-icon-name "zmdi-close"
   :on-click #(re-frame/dispatch [:chat/enabled? false])])

(defn name-form []
  "Form for submitting name+email"
  (let [user (re-frame/subscribe [:user])]
    (fn []
      ;; Using forms is a bit messy with re-com, sorry for the inline css mess
      [:form {:on-submit #(do (when (not= "" (:name @user))
                                (re-frame/dispatch [:chat/send-user-info]))
                              (.preventDefault %))
              :class "rc-v-box display-flex form-group"
              :style {:margin "0 auto"
                      :flex-flow "column nowrap"
                      :justify-content "center"}}
       [re-com/input-text
        :placeholder "Name"
        :model (:name @user)
        :change-on-blur? false
        :on-change #(re-frame/dispatch-sync [:user/name %])]
       [re-com/input-text
        :placeholder "Email (Optional)"
        :model (:email @user)
        :change-on-blur? false
        :class "form-group"
        :style {:margin "10px 0"}
        :on-change #(re-frame/dispatch-sync [:user/email %])]
       [re-com/h-box
        :style {:justify-content "space-between"}
        :children [[re-com/button
                    :label "Start Chat"]
                   [re-com/button
                    :label "Cancel"
                    :on-click #(do (re-frame/dispatch [:chat/enabled? false])
                                   (.preventDefault %))]]]])))

(defn panel []
  [re-com/v-box
   :width "100%"
   :children [[end-button] [container] [msg-input]]])
