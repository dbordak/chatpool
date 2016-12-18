(ns chatpool.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [chatpool.chat :as chat]))


(defn start-chat-button []
  [re-com/button
   :label "Start Chat"
   :on-click #(re-frame/dispatch [:chat/enabled? true])])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[:p "Hello World"] [start-chat-button]]])

(defn about-title []
  [re-com/title
   :label "About Us"
   :level :level1])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [:p "We do the thing with the stuff."]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    :debug-panel [home-panel]
    :login-panel [home-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn title-box []
  [re-com/title
   :label "Companyname.website"
   :level :level1])

(defn nav-bar []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/h-box
       :style {:justify-content "space-between"
               :background "#f7f7f9"
               :border-bottom "1px solid #e1e1e8"
               :padding "10px"}
       :children [[re-com/label
                   :label "Companyname.website"
                   :style {:font-style "italic"
                           :font-size "14pt"
                           :line-height "40px"
                           :height "40px"}]
                  [re-com/horizontal-pill-tabs
                   :model @active-panel
                   :on-change #(re-frame/dispatch [:set-active-panel %])
                   :tabs [{:id :home-panel
                           :label "Home"}
                          {:id :about-panel
                           :label "About"}
                          {:id :debug-panel
                           :label "Debug"}
                          {:id :login-panel
                           :label "Login"}]]]])))

(defn meta-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        chat? (re-frame/subscribe [:chat/enabled?])
        chat-ready? (re-frame/subscribe [:chat/ready?])]
    (fn []
      (if @chat?
        [re-com/h-split
         :margin "0"
         :height "100%"
         :initial-split 70
         :panel-1 [re-com/scroller
                   :height "100%"
                   :child [panels @active-panel]]
         :panel-2 [(if @chat-ready? chat/panel chat/name-form)]]
        [re-com/v-box
         :children [[panels @active-panel]]]))))

(defn main-panel []
  [re-com/v-box
   :height "100vh"
   :children [[nav-bar] [meta-panel]]])
