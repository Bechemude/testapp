(ns frontend.components.modal
  (:require
   [cljs.spec.alpha :as s]
   [frontend.components.inputs :refer [input select]]
   [frontend.events]
   [frontend.subs]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [spec.form :as f]))

(defn handle-change
  [key]
  #(rf/dispatch [:handle-change key (-> % .-target .-value)]))

(defn handle-close
  []
  (rf/dispatch [:modal nil])
  (rf/dispatch [:update-application nil]))

(defn edit-modal
  [index]
  (r/create-class
   {:component-did-mount
    (fn []
      (when index (rf/dispatch [:get-application index]))
      (rf/dispatch [:get-persons]))

    :reagent-render
    (fn []
      (let [application @(rf/subscribe [:application])
            application-data (:data application)
            persons @(rf/subscribe [:persons])]
        [:<>
         (if index
           [:h2.text-xl.pt-4.text-center "Edit application"]
           [:h2.text-xl.pt-4.text-center "Create application"])

         (if (or (:is-persons-loading? persons)
                 (when index (:is-application-loading? application)))
           [:h2.text-xl.pt-4.text-center "Loading..."]
           [:<>

            (let [kw :title]
              [input "Title" "text" (kw application-data) kw])

            (let [kw :description]
              [input "Descripton" "text" (kw application-data) kw])

            (let [key :applicant]
              [select "Applicant"
               (:data persons)
               (key (:data application))
               (handle-change key)
               (s/valid? ::f/applicant (key (:data application)))
               (f/get-message (s/explain-data ::f/applicant (key (:data application))))])

            (let [key :executor]
              [select "Executor"
               (:data persons)
               (key (:data application))
               (handle-change key)
               (s/valid? ::f/executor (key (:data application)))
               (f/get-message (s/explain-data ::f/executor (key (:data application))))])

            (let [kw :deadline]
              [input "Deadline" "date" (kw application-data) kw])

            [:button {:class "p-1 bg-gray-200 hover:bg-gray-300"
                      :on-click
                      #(if (contains? (:data application) :index)
                         (rf/dispatch
                          [:put-application (:data application) (:index (:data application))])
                         (rf/dispatch
                          [:post-application (:data application)]))}
             "Save!"]])]))}))

(defn modal
  []
  (let [ref (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn [_] (.focus @ref))
      :reagent-render
      (fn []
        (let [modal @(rf/subscribe [:modal])
              error @(rf/subscribe [:error])]
          [:div {:on-key-down (fn [e] (when (= (.-key e) "Escape") (handle-close)))
                 :tab-index 0
                 :ref #(reset! ref %)
                 :on-click handle-close
                 :class "fixed left-0 top-0 w-screen h-screen
                    bg-gray-400 bg-opacity-50 overflow-auto"}
           [:div {:class "grid w-3/6 m-auto my-20 p-10 gap-10 bg-gray-100"
                  :on-click (fn [e] (.stopPropagation e))}
            [:button {:class "absolute justify-self-end text-3xl -m-5 transform rotate-45"
                      :on-click handle-close} "+"]
            (if error
              [:h2 {:class "text-xl pt-4 text-center"} "Error: "
               (:status-text error)]
              (case (:type modal)
                "edit" [edit-modal (:application-id modal)]))]]))})))
