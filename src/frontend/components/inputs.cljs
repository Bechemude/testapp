(ns frontend.components.inputs
  (:require
   [re-frame.core :as rf]
   [cljs.spec.alpha :as s]
   [reagent.core :as r]
   [spec.form :as f]))

(defn input [label input-type value kw]
  (let [value (r/atom value)
        dirty (r/atom false)
        validation-kw (->> kw
                           (name)
                           (str "spec.form/")
                           (keyword))
        err-msg (->> value
                     (s/explain-data validation-kw)
                     (f/get-message))]
    (fn []
      [:div.flex.flex-col
       [:label label]
       [:input.p-3 {:class
                    (when (and @dirty (not (s/valid? validation-kw @value)))
                      "border-2 border-red-500 ")
                    :type input-type
                    :placeholder label
                    :value @value
                    :on-blur #(do (rf/dispatch [:handle-change kw @value])
                                  (reset! dirty true))
                    :on-change #(reset! value (-> % .-target .-value))}]

       (when (and @dirty (not (s/valid? validation-kw @value)))
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
