(ns frontend.views
  (:require
    [frontend.components.modal :refer [modal]]
    [frontend.components.table :refer [table]]
    [frontend.events]
    [frontend.subs]
    [re-frame.core :as rf]))


(defn add-application-btn
  []
  [:button.p-2.mb-4.bg-gray-100.hover:bg-gray-200
   {:on-click #(rf/dispatch [:modal {:is-opened? true :type "edit"}])}
   "Add new application"])


(defn app
  []
  (let [applications @(rf/subscribe [:applications])
        error @(rf/subscribe [:error])
        modal-state @(rf/subscribe [:modal])]
    [:div.flex.flex-col.px-10
     [:h1.text-4xl.py-4.text-center "Applications"]
     (when (and error (not (:is-opened? modal-state)))
       [:h2.text-4xl.py-4.text-center "Error: " (:status-text error)])
     [add-application-btn]
     ;; TABLE
     (if (not (:is-applications-loading? applications))
       [table (:data applications)]
       [:h2.text-xl.pt-4.text-center "Loading..."])
     ;; MODAL
     (when (:is-opened? modal-state)
       [modal])]))
