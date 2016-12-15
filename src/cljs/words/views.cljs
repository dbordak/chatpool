(ns words.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [reagent.core :as reagent]))


;; home

(defn home-title []
  [re-com/title
   :label "Words"
   :level :level1])

(defn name-field []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/input-text
       :placeholder "Name"
       :model @name
       :on-change #(re-frame/dispatch [:name %])])))

(defn new-game-button []
  [re-com/hyperlink-href
   :label "New Game"
   :class "btn btn-default"
   :href "#/game"])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title]
              [re-com/h-box
               :gap "0.5em"
               :children [[name-field] [new-game-button]]]]])

;; game

(defn settings-modal []
  [re-com/md-icon-button
   :md-icon-name "zmdi-settings"])

(defn hint-display []
  (let [hint (re-frame/subscribe [:hint])]
    (fn []
      [re-com/label
       :label (if @hint
                @hint
                "No hint to display.")
       :class "btn btn-primary disabled"])))

(defn help-modal []
  [re-com/md-icon-button
   :md-icon-name "zmdi-help"])

(defn top-bar []
  [re-com/h-box
   :gap "0.5em"
   :children [[settings-modal] [hint-display] [help-modal]]])

(defn word-button [word]
  (let [selected-word (re-frame/subscribe [:selected-word])]
    [re-com/radio-button
     :label word
     :value word
     :model selected-word
     :style {:visibility "hidden"}
     :label-class (str "btn btn-default"
                       (if (= word @selected-word)
                         " active" ""))
     :on-change #(re-frame/dispatch [:selected-word word])]))

(defn game-board []
  (let [word-list (re-frame/subscribe [:word-list])]
    (fn []
      [re-com/h-box
       :gap "0.2mm"
       :children (doall (map word-button @word-list))])))

(defn hint-input []
  (let [new-hint (re-frame/subscribe [:new-hint])]
    (fn []
      [re-com/h-box
       :gap "0.5em"
       :children [[re-com/input-text
                   :placeholder "Hint"
                   :model (:text @new-hint)
                   :on-change #(re-frame/dispatch [:new-hint {:text %}])]
                  [re-com/input-text
                   :placeholder "Count"
                   :model (:count @new-hint)
                   :width "5em" ; number input, only need it to be as
                                ; big as the placeholder text.
                   :on-change #(re-frame/dispatch [:new-hint {:count %}])]
                  [re-com/button
                   :label "Submit"
                   :on-click #(do (re-frame/dispatch [:new-hint {:text "" :count ""}])
                                  ;(re-frame/dispatch [:example/test-rapid-push])
                                  (re-frame/dispatch [:toggle-chat])
                                  (re-frame/dispatch [:new-message "1"]))]]])))

(defn game-panel []
  [re-com/v-box
   :gap "1em"
   :children [[top-bar] [game-board] [hint-input]]])

;; Chat stuff

(defn chat-message [text]
  [re-com/label
   :label text])

(defn chat-box []
  (let [messages (re-frame/subscribe [:messages])]
    (fn []
      [re-com/scroller
       :height "100%"
       :child [re-com/v-box
               :children (doall (map chat-message @messages))]])))

(defn chat-input []
  (let [new-message ""]
    (fn []
      [re-com/h-box
       :children [[re-com/label
                   :label "input goes here"]]])))

(defn chat-panel []
  [re-com/v-box
   :width "100%"
   :children [[chat-box] [chat-input]]])

(defn meta-chat-panel [other-panel]
  [re-com/h-split
   :margin "0"
   :height "100vh"
   :initial-split 70
   :panel-1 [re-com/scroller
             :height "100%"
             :child [other-panel]]
   :panel-2 [chat-panel]])

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :game-panel [] [game-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        chat? (re-frame/subscribe [:chat?])]
    (fn []
      (if @chat?
        [re-com/h-split
         :margin "0"
         :height "100vh"
         :initial-split 70
         :panel-1 [re-com/scroller
                   :height "100%"
                   :child [panels @active-panel]]
         :panel-2 [chat-panel]]
        [re-com/v-box
         :height "100%"
         :children [[panels @active-panel]]]))))
