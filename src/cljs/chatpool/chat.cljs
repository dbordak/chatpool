(ns chatpool.chat
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [reagent.core :as reagent]))

(defn render-msg [username]
  (fn [msg]
    (let [[key map] msg]
      (case key
        :cust-page
        [:span {:style {:color "#999"
                        :font-style "italic"
                        :text-align "center"}}
         (str "Customer is now on " (name (:page map)))]

        :chat-msg
        [:span {:style {:text-align (if (= (:name map) username)
                                      "right" "left")}}

         ;; Timestamp
         [:span {:style {:color "#999"
                         :font-style "italic"
                         :margin-right "0.25em"}}
          (:time map)]

         ;; Username
         [:span {:style {:font-weight "700"}}
          (:name map)]
         [:span {:style {:margin-right "0.25em"}} ":"]

         ;; Message
         (:body map)]))))

(defn container []
  "Container for making the chat separately scrollable"
  (let [msg-list (re-frame/subscribe [:chat/msg-list])
        user (re-frame/subscribe [:user])]
    (fn []
      (re-frame/dispatch [:scroll-chatbox])
      [re-com/scroller
       :height "100%"
       :attr {:id "chat-scrollbox"}
       :margin "10px 0"
       :child [re-com/v-box
               :children
               (for [msg @msg-list]
                 [(render-msg (:name @user)) msg])]])))

(defn msg-input []
  "Message input form"
  (let [msg-input (re-frame/subscribe [:chat/msg-input])]
    (fn []
      [:form {:on-submit #(do (re-frame/dispatch [:chat/send-msg])
                              (.preventDefault %))}
       [re-com/input-text
        :placeholder "Type a message!"
        :width "100%"
        :model @msg-input
        :change-on-blur? false
        :on-change #(re-frame/dispatch-sync [:chat/msg-input %])]])))

(defn end-button []
  "Ends the chat. If a customer, closes the chat panel. If a rep, just
  ends chat."
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
                    :label "Cancel"
                    :on-click #(do (re-frame/dispatch [:chat/enabled? false])
                                   (.preventDefault %))]
                   [re-com/button
                    :label "Start Chat"]]]])))

(defn panel []
  (let [rep-id (re-frame/subscribe [:rep-id])
        cust-page (re-frame/subscribe [:cust-page])]
    (fn []
      [re-com/v-box
       :width "100%"
       :margin "10px"
       :style {:flex "1 1 auto"}
       :children
       (if (and @rep-id (not @cust-page))
         [[re-com/label
           :label "No customer connected"]]
         [[end-button] [container] [msg-input]])])))
