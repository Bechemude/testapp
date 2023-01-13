(ns backend.core-test
  (:require
   [backend.core :refer [app]]
   [cheshire.core :as json]
   [clojure.string :refer [replace]]
   [clojure.test :refer [deftest is use-fixtures testing]]
   [datomic.api :as d]
   [environ.core :refer [env]]))

(def db-uri (env :db-uri))

(def bad-params
  [[{:title nil} "empty title field"]
   [{:title 42} "wrong value in title field"]
   [{:description nil} "empty description field"]
   [{:description 42} "wrong value in description field"]
   [{:applicant nil} "empty applicant field"]
   [{:executor nil} "empty executor field"]
   [{:deadline nil} "empty deadline field"]
   [{:deadline 42} "wrong value in deadline field"]
   [{:deadline "1900-01-01"} "wrong value in deadline field"]])

(defn fix-db
  [t]
  ;; create db and add application
  (app {:request-method :post
        :uri "/applications"
        :body-params {:title "title"
                      :description "desc"
                      :applicant "17592186045420"
                      :executor "17592186045420"
                      :deadline "2023-02-01"}})
  (t)
  (d/delete-database db-uri))

(use-fixtures :each fix-db)

(def persons
  "for compare with test value"
  [{"index" 2, "name" "Aurora"}
   {"index" 1, "name" "Alex"}])

(def default-application
  "for compare with test value"
  {:title "title",
   :description "desc",
   :applicant "17592186045420"
   :executor "17592186045420"
   :deadline "2023-02-01"})

(def test-application
  "for adding to db"
  {:title "title",
   :description "desc",
   :applicant "17592186045420"
   :executor "17592186045420"
   :deadline "2023-02-01"})

(deftest test-persons
  (testing "get all persons"
    (let [request {:request-method :get
                   :uri "/persons"}
          body (:body (app request))]
      (is (= persons
             (map #(dissoc % "id") (json/decode body)))))))

(deftest test-applications
  (testing "get all applications"
    (let [request {:request-method :get
                   :uri "/applications"}
          prepared-validation-data (-> default-application
                                       (assoc :applicant "Aurora" :executor "Aurora")
                                       (json/generate-string)
                                       (json/parse-string))
          body (json/decode (:body (app request)))
          request-result (as-> body $
                           (first $)
                           (dissoc $ "id" "index")
                           (assoc $
                                  "executor" (str (get $ "executor"))
                                  "applicant" (str (get $ "applicant"))
                                  "deadline" (replace (get $ "deadline") #"T.*" "")))]
      (is (= prepared-validation-data
             request-result))))

  (testing "get application by index"
    (let [request {:request-method :get
                   :uri "/applications/1"}
          prepared-validation-data (-> default-application
                                       (json/generate-string)
                                       (json/parse-string))
          body (json/decode (:body (app request)))
          request-result (as-> body $
                           (first $)
                           (dissoc $ "id" "index")
                           (assoc $
                                  "executor" (str (get $ "executor"))
                                  "applicant" (str (get $ "applicant"))
                                  "deadline" (replace (get $ "deadline") #"T.*" "")))]

      (is (= prepared-validation-data
             request-result))))

  (testing "create application"
    (let [request {:request-method :post
                   :uri "/applications"
                   :body-params test-application}
          prepared-validation-data (-> default-application
                                       (json/generate-string)
                                       (json/parse-string))
          body (json/decode (:body (app request)))
          request-result (as-> body $
                           (first $)
                           (dissoc $ "id" "index")
                           (assoc $
                                  "executor" (str (get $ "executor"))
                                  "applicant" (str (get $ "applicant"))
                                  "deadline" (replace (get $ "deadline") #"T.*" "")))]

      (is (= prepared-validation-data
             request-result))))

  (testing "edit application by id"
    (let [application-id (-> {:request-method :get :uri "/applications/1"}
                             (app)
                             (:body)
                             (json/decode)
                             (first)
                             (get "id"))
          prepared-validation-data (-> default-application
                                       (json/generate-string)
                                       (json/parse-string))
          request {:request-method :put
                   :uri "/applications/1"
                   :body-params (assoc test-application :id application-id)}
          body (json/decode (:body (app request)))
          request-result (as-> body $
                           (first $)
                           (dissoc $ "id" "index")
                           (assoc $
                                  "executor" (str (get $ "executor"))
                                  "applicant" (str (get $ "applicant"))
                                  "deadline" (replace (get $ "deadline") #"T.*" "")))]
      (is (= prepared-validation-data
             request-result)))))

(deftest test-app-page-not-found
  (testing "get request to nonexistent url"
    (let [request {:request-method :get
                   :uri "/not-found"}
          status (:status (app request))]
      (is (= 404 status)))))

;; bad params
(deftest test-create-application-bad-params
  (testing "sending bad params:"
    (doseq [[params desription] bad-params]
      (testing desription
        (let [bad-param (merge test-application params)
              request {:request-method :post
                       :uri "/applications"
                       :body-params bad-param}
              status (:status (app request))]
          (is (= status 400)))))))

