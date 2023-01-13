(ns frontend.events
  (:require
   [ajax.core :as ajax]
   [clojure.string :as str]
   [day8.re-frame.http-fx]
   [re-frame.core :as rf]))

(def base-url "http://localhost:8080")

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:modal nil
    :error nil
    :application nil
    :applications nil
    :persons nil}))

(rf/reg-event-db
 :modal
 (fn [db [_ modal]]
   (assoc db :modal modal :error nil)))

(rf/reg-event-db
 :error
 (fn [db [_ error]]
   (assoc db :error error)))

(rf/reg-event-db
 :handle-change
 (fn [db [_ key value]]
   (update db :application assoc-in [:data key] value)))

(rf/reg-event-fx
 :get-persons
 (fn [{:keys [db]} _]
   {:http-xhrio {:method :get
                 :uri (str base-url "/persons")
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success  [:success-get-persons]
                 :on-failure     [:failure-http-result]}
    :db (assoc db :persons {:is-persons-loading? true})}))

(rf/reg-event-fx
 :get-applications
 (fn [{:keys [db]} _]
   {:http-xhrio {:method :get
                 :uri (str base-url "/applications")
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success  [:success-get-applications]
                 :on-failure     [:failure-http-result]}
    :db (assoc db :applications {:is-applications-loading? true})}))

(rf/reg-event-fx
 :get-application
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method :get
                 :uri (str base-url "/applications/" id)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success  [:success-get-application]
                 :on-failure  [:failure-http-result]}
    :db (assoc db :application {:is-application-loading? true})}))

(rf/reg-event-fx
 :post-application
 (fn [{:keys [db]} [_ application]]
   {:http-xhrio {:method :post
                 :uri (str base-url "/applications")
                 :params application
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success  [:update-application]
                 :on-failure     [:failure-http-result]}
    :db (assoc db :application {:is-application-loading? true})}))

(rf/reg-event-fx
 :put-application
 (fn [{:keys [db]} [_ application id]]
   {:http-xhrio {:method :put
                 :uri (str base-url "/applications/" id)
                 :params application
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success  [:update-application]
                 :on-failure     [:failure-http-result]}
    :db (assoc db :application {:is-application-loading? true})}))

(rf/reg-event-db
 :success-get-persons
 (fn [db [_ persons]]
   (assoc db :persons {:data persons :is-persons-loading? false})))

(rf/reg-event-db
 :success-get-applications
 (fn [db [_ applications]]
   (assoc db :applications {:data applications :is-applications-loading? false})))

(rf/reg-event-db
 :success-get-application
 (fn [db [_ [{:keys [id index title description applicant executor deadline]}]]]
   (assoc db :application {:data {:id id
                                  :index index
                                  :title title
                                  :description description
                                  :applicant (str applicant)
                                  :executor (str executor)
                                  :deadline (str/replace deadline #"T.*" "")}
                           :is-application-loading? false})))

(rf/reg-event-fx
 :update-application
 (fn [cofx [_ application]]
   {:db (assoc (:db cofx)
               :application nil
               :modal nil)
    :fx [[:dispatch [:get-applications]]]}))

(rf/reg-event-db
 :failure-http-result
 (fn [db [_ error]]
   (assoc db :error error
          :persons (assoc (:persons db) :is-persons-loading? false)
          :application (assoc (:application db) :is-application-loading? false)
          :applications (assoc (:applications db) :is-applications-loading? false))))

