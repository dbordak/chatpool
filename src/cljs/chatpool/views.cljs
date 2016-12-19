(ns chatpool.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [taoensso.encore :as encore]
              [chatpool.chat :as chat]))


(defn start-chat-button []
  [re-com/button
   :label "Start Chat"
   :on-click #(re-frame/dispatch [:chat/enabled? true])])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :margin "1em"
   :children [[:p "Hello World"] [start-chat-button]]])

(defn about-title []
  [re-com/title
   :label "About Us"
   :level :level1])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :margin "1em"
   :children [[about-title] [:p "We do the thing with the stuff."]]])

(defn rep-login-button [rep]
  [re-com/button
   :label (str (:first_name rep) " " (:last_name rep))
   :on-click #(re-frame/dispatch [:rep-login rep])])

(defn login-title []
  (re-com/title
   :label "Log in as Representative"
   :level :level1))

(defn login-panel []
  (let [rep-list (re-frame/subscribe [:rep-list])]
    (fn []
      [re-com/v-box
       :gap "1em"
       :margin "1em"
       :children (cons [login-title]
                       (doall (map rep-login-button @rep-list)))])))

;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    :debug-panel [home-panel]
    :login-panel (do (encore/ajax-lite
                      "/api/v1/rep/list"
                      {:method :get :resp-type :edn}
                      (fn [resp]
                        (when (:?content resp)
                          (re-frame/dispatch [:rep-list (:?content resp)]))))
                     [login-panel])
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
                          ;; {:id :debug-panel
                          ;;  :label "Debug"}
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
         :style {:flex "1 1 auto"}
         :height "100%"
         :initial-split 70
         :panel-1 [re-com/scroller
                   :height "100%"
                   :child [panels @active-panel]]
         :panel-2 [(if @chat-ready? chat/panel chat/name-form)]]
        [re-com/v-box
         :style {:flex "1 1 auto"}
         :children [[panels @active-panel]
                    ;; TODO: chat availability
                    ]]))))

(defn bottom-banner []
  (let [user (re-frame/subscribe [:user])
        rep-id (re-frame/subscribe [:rep-id])
        bar-height "30px"]
    (fn []
      (if @rep-id
        [re-com/h-box
         :gap "1em"
         :style {:justify-content "center"
                 :background "#f7f7f9"
                 :border-top "1px solid #e1e1e8"
                 :padding "10px"}
         :children [[re-com/label
                     :label (str "Welcome, " (:name @user))
                     :style {:font-size "14pt"
                             :line-height bar-height
                             :height bar-height}]
                    [re-com/hyperlink
                     :label "Logout"
                     :style {:line-height bar-height}
                     :on-click #(re-frame/dispatch [:rep-logout])]]]
        [:div]))))

(defn main-panel []
  [re-com/v-box
   :height "100vh"
   :children [[nav-bar] [meta-panel] [bottom-banner]]])
