(ns frontend.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :persons
 (fn [db _]
   (:persons db)))

(rf/reg-sub
 :applications
 (fn [db _]
   (:applications db)))

(rf/reg-sub
 :application
 (fn [db _]
   (:application db)))

(rf/reg-sub
 :error
 (fn [db _]
   (:error db)))

(rf/reg-sub
 :modal
 (fn [db _]
   (:modal db)))
