(ns chatpool.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [chatpool.chat :as chat]))


;; home

(defn home-title []
  (let [name "world"]
    (fn []
      [re-com/title
       :label (str "Hello from " name ". This is the Home Page.")
       :level :level1])))

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title] [link-to-about-page]]])


;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        chat? (re-frame/subscribe [:chat/enabled?])
        chat-ready? (re-frame/subscribe [:chat/ready?])]
    (fn []
      (if @chat?
        [re-com/h-split
         :margin "0"
         :height "100vh"
         :initial-split 70
         :panel-1 [re-com/scroller
                   :height "100%"
                   :child [panels @active-panel]]
         :panel-2 [(if @chat-ready? chat/panel chat/name-form)]]
        [re-com/v-box
         :height "100%"
         :children [[panels @active-panel]]]))))
