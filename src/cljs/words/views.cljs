(ns words.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [reagent.core :as reagent]))


(def name "")
(def hint nil)
(def new-hint "")
(def count "")
(def word-list ["apple" "banana" "truck"])
(def selected-word (reagent/atom "banana"))

;; home

(defn home-title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label "Words"
       :level :level1])))

(defn name-field []
  [re-com/input-text
   :placeholder "Name"
   :model name
   :on-change #(reset! name %)])

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
  [re-com/label
   :label (if hint
            hint
            "No hint to display.")
   :class "btn btn-primary disabled"])

(defn help-modal []
  [re-com/md-icon-button
   :md-icon-name "zmdi-help"])

(defn top-bar []
  [re-com/h-box
   :gap "0.5em"
   :children [[settings-modal] [hint-display] [help-modal]]])

(defn word-button [word]
  [re-com/radio-button
   :label word
   :value word
   :model selected-word
   :style {:visibility "hidden"}
   :label-class (if (= word @selected-word)
                  "btn btn-default active"
                  "btn btn-default")
   :on-change #(reset! selected-word word)])

(defn game-board []
  [re-com/h-box
   :gap "0.2mm"
   :children (doall (map word-button word-list))])

(defn- radio-clicked
  [selections item-id required?]
  (if (and required? (selections item-id))
    selections  ;; prevent unselect of radio
    (if (selections item-id) #{} #{item-id})))

(defn word-button-renderer
  [item id-fn selections on-change disabled? label-fn required? as-exclusions?]
  (let [item-id (id-fn item)]
    [re-com/box
     :attr {:on-click (re-com/handler-fn (when-not disabled?
                                    (on-change (radio-clicked selections item-id required?))))}
     :child [re-com/radio-button
             :model (first selections)
             :value item-id
             :on-change #()                                 ;; handled by enclosing box
             :disabled? disabled?
             :style {:display "none"}
             :label-class (if (selections item-id)
                            "btn btn-default active"
                            "btn btn-default")
             :label item-id]]))

;; (defn game-board []
;;   [re-com/selection-list
;;    :choices (map #(hash-map :id %) word-list)
;;    :model selected-word
;;    :multi-select? false
;;    :required? true
;;    :item-renderer word-button-renderer
;;    :on-change #(reset! selected-word %)])

(defn hint-input []
  [re-com/h-box
   :gap "0.5em"
   :children [[re-com/input-text
               :placeholder "Hint"
               :model new-hint
               :on-change #(reset! new-hint %)]
              [re-com/input-text
               :placeholder "Count"
               :model count
               :on-change #(reset! count %)]
              [re-com/button
               :label "Submit"]]])

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
