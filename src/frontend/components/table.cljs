(ns frontend.components.table
  (:require
    [clojure.string :as str]
    [re-frame.core :as rf]))


(defn table
  [applications]
  (if (not= (count applications) 0)
    [:table.table-auto
     [:thead
      [:tr.text-left.border-solid
       [:th "ID"]
       [:th "TITLE"]
       [:th "DESCRIPTION"]
       [:th "APPLICANT"]
       [:th "EXECUTOR"]
       [:th "DEADLINE"]]]
     [:tbody
      (for [{:keys [id
                    index
                    title
                    description
                    applicant
                    executor
                    deadline]}
            applications]
        ^{:key id}
        [:tr.border-b-2
         [:td index]
         [:td title]
         [:td description]
         [:td applicant]
         [:td executor]
         [:td (str/replace deadline #"T.*" "")]
         [:td
          [:button.p-1.bg-gray-100.hover:bg-gray-200
           {:on-click #(rf/dispatch [:modal
                                     {:is-opened? true
                                      :type "edit"
                                      :application-id index}])}
           "Edit"]]])]]
    [:h2.text-xl.text-center.pt-20 "List is empty"]))

