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
  (let [count (re-frame/subscribe [:count])
        new-hint (re-frame/subscribe [:new-hint])]
    (fn []
      [re-com/h-box
       :gap "0.5em"
       :children [[re-com/input-text
                   :placeholder "Hint"
                   :model @new-hint
                   :on-change #(re-frame/dispatch [:new-hint %])]
                  [re-com/input-text
                   :placeholder "Count"
                   :model @count
                   :on-change #(re-frame/dispatch [:count %])]
                  [re-com/button
                   :label "Submit"
                   :on-click #(do (re-frame/dispatch [:new-hint ""])
                                  (re-frame/dispatch [:count ""])
                                  (re-frame/dispatch [:example/test-rapid-push]))]]])))

(defn game-panel []
  [re-com/v-box
   :gap "1em"
   :children [[top-bar] [game-board] [hint-input]]])

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :game-panel [] [game-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [[panels @active-panel]]])))
