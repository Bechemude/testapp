(ns backend.handlers
  (:require
   [backend.db :as db]
   [clojure.spec.alpha :as s]
   [ring.util.response :as r]
   [spec.form :as f]))

(defn get-persons
  "get all persons"
  [request]
  (let [db (:db request)
        body (db/get-persons db)]
    (r/response body)))

(defn get-applications
  "get all applications"
  [request]
  (let [db (:db request)
        body (db/get-applications db)]
    (r/response body)))

(defn get-application-by-index
  "get application by human-readable index"
  [request]
  (let [db (:db request)
        index (:index (:params request))
        body (db/get-application-by-index index db)]
    (r/response body)))

(defn create-application
  "create new application if request data is valid"
  [request]
  (let [application (or (:body request) (:body-params request))
        conn (:conn request)
        db (:db request)]
    (if (s/valid? ::f/form application)
      (let [tx-response (db/create-application application conn db)
            id (first (vals (:tempids tx-response)))
            body (db/get-application-by-id id (:db-after tx-response))
            index (:index (first body))
            url (str "/applications/" index)]
        (r/created url body))
      (r/bad-request "Invalid request"))))

(defn edit-application-by-id
  "update application by datomic entity id"
  [request]
  (let [application (or (:body request) (:body-params request))
        id (:id application)
        conn (:conn request)]
    (if (s/valid? ::f/form application)
      (let [tx-response (db/edit-application id application conn)
            body (db/get-application-by-id id (:db-after tx-response))]
        (r/response body))
      (r/bad-request "Invalid request"))))

