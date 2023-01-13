(ns frontend.core
  (:require
    [frontend.views :refer [app]]
    [re-frame.core :as rf]
    [reagent.dom :as rdom]))


(defn run
  []
  (rf/dispatch-sync [:initialize])
  (rf/dispatch [:get-applications])
  (app))


(run)


(defn ^:export main!
  []
  (rdom/render [app] (js/document.getElementById "app")))

