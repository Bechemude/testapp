(ns backend.core-test
  (:require
   [backend.core :refer [app]]
   [cheshire.core :as json]
   [clojure.string :refer [replace]]
   [clojure.test :refer [deftest is use-fixtures testing]]
   [datomic.api :as d]
   [environ.core :refer [env]]))

(def db-uri (env :db-uri))

(def persons
  "for comparing with test value"
  [{:index 2, :name "Aurora"}
   {:index 1, :name "Alex"}])

(def default-application
  "for comparing with test value,
  applicant and executor add by get person id"
  {:title "title",
   :description "desc",
   :deadline "2023-02-01"})

(def test-application
  "for adding to db,
  applicant and executor add by get person id"
  {:title "title",
   :description "desc",
   :deadline "2023-02-01"})

(def bad-params
  [[{:title nil} "empty title field"]
   [{:title 42} "wrong value in title field"]
   [{:description nil} "empty description field"]
   [{:description 42} "wrong value in description field"]
   [{:applicant nil} "empty applicant field"]
   [{:applicant 1} "wrong value in applicant field"]
   [{:executor nil} "empty executor field"]
   [{:executor 1} "wrong value in executor field"]
   [{:deadline nil} "empty deadline field"]
   [{:deadline 42} "wrong value in deadline field"]
   [{:deadline "1900-01-01"} "wrong value in deadline field"]])

(defn get-first-person-id
  []
  (-> {:request-method :get :uri "/persons"}
      (app)
      (:body)
      (json/decode true)
      (first)
      (:id)
      (str)))

(defn fix-db
  [t]
  ;; create db and get first person id
  (let [person-id (get-first-person-id)]
    ;;add application with first person id
    (app {:request-method :post
          :uri "/applications"
          :body-params (assoc test-application :applicant person-id :executor person-id)}))

  (t)
  (d/delete-database db-uri))

(use-fixtures :each fix-db)

(deftest test-persons
  (testing "get all persons"
    (let [request {:request-method :get
                   :uri "/persons"}
          body (:body (app request))]
      (is (= persons
             (map #(dissoc % :id) (json/decode body true)))))))

(defn prepare-request-result
  [body]
  (-> body
      (first)
      (dissoc :id :index)
      (update-in [:executor] str)
      (update-in [:applicant] str)
      (update-in [:deadline] #(replace % #"T.*" ""))))

(deftest test-applications
  (testing "get all applications"
    (let [request {:request-method :get
                   :uri "/applications"}
          body (json/decode (:body (app request)) true)
          request-result (prepare-request-result body)]
      (is (= (assoc default-application :applicant "Aurora" :executor "Aurora")
             request-result))))

  (testing "get application by index"
    (let [person-id (get-first-person-id)
          request {:request-method :get
                   :uri "/applications/1"}
          body (json/decode (:body (app request)) true)
          request-result (prepare-request-result body)]
      (is (= (assoc default-application :applicant person-id :executor person-id)
             request-result))))

  (testing "create application"
    (let [person-id (get-first-person-id)
          request {:request-method :post
                   :uri "/applications"
                   :body-params (assoc test-application
                                       :applicant person-id
                                       :executor person-id)}
          body (json/decode (:body (app request)) true)
          request-result (prepare-request-result body)]
      (is (= (assoc default-application :applicant person-id :executor person-id)
             request-result))))

  (testing "edit application by id"
    (let [application-id (-> {:request-method :get :uri "/applications/1"}
                             (app)
                             (:body)
                             (json/decode)
                             (first)
                             (get "id"))
          person-id (get-first-person-id)
          request {:request-method :put
                   :uri "/applications/1"
                   :body-params (assoc test-application
                                       :id application-id
                                       :applicant person-id
                                       :executor person-id)}
          body (json/decode (:body (app request)) true)
          request-result (prepare-request-result body)]
      (is (= (assoc default-application :applicant person-id :executor person-id)
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

