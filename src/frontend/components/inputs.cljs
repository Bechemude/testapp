(ns frontend.components.inputs
  (:require
    [reagent.core :as r]))


(defn input
  [label type value handler is-valid? err-msg]
  (let [dirty (r/atom false)]
    (fn [label type value handler is-valid? err-msg]
      [:div.flex.flex-col
       [:label label]
       [:input.p-3 {:class
                    (when (and @dirty (not is-valid?))
                      "border-2 border-red-500 ")
                    :type type
                    :placeholder label
                    :value value
                    :on-blur #(reset! dirty true)
                    :on-change handler}]

       (when (and @dirty (not is-valid?))
         [:p.text-red-500 err-msg])])))


(defn select
  [label options value handler is-valid? err-msg]
  (let [dirty (r/atom false)]
    (fn [label options value handler is-valid? err-msg]
      [:div.flex.flex-col
       [:label label]
       [:select.p-3 {:class
                     (when (and @dirty (not is-valid?))
                       "border-2 border-red-500 ")
                     :placeholder label
                     :value (or value "Select option")
                     :on-blur #(reset! dirty true)
                     :on-change handler}
        [:option {:disabled true :defaultValue true} "Select option"]
        (for [{:keys [id index name]} options]
          ^{:key id}
          [:option {:value id} name])]
       (when (and @dirty (not is-valid?))
         [:p.text-red-500 err-msg])])))
