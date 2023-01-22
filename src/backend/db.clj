(ns backend.db
  (:require
   [backend.db-scheme :as s]
   [clj-time.coerce :as c]
   [clj-time.format :as fmt]
   [datomic.api :as d]))

(def persons-list
  "Static data for example"
  [{:db/id (d/tempid :db.part/user)
    :person/name "Alex"
    :person/index 1}
   {:db/id (d/tempid :db.part/user)
    :person/name "Aurora"
    :person/index 2}])

(defn wrap-create-db
  "A Ring middleware that create db, add schemes, add init static data
  if not created"
  [handler uri]
  (fn [request]
    (let [succ (d/create-database uri)]
      (when succ
        (let [conn (d/connect uri)]
          (d/transact conn s/person-schema)
          (d/transact conn s/application-schema)
          (d/transact conn persons-list)))
      (handler request))))

(defn wrap-datomic
  "A Ring middleware that provides a request-consistent database connection and
  value for the life of a request."
  [handler uri]
  (fn [request]
    (let [conn (d/connect uri)]
      (handler (assoc request
                      :conn conn
                      :db   (d/db conn))))))

(def all-persons-q
  '[:find ?id ?index ?name
    :where
    [?id :person/name ?name]
    [?id :person/index ?index]])

(defn get-persons
  [db]
  (mapv (partial zipmap [:id :index :name]) (d/q all-persons-q db)))

;; APPLICATION
(def application-rule
  '[[(application-rule ?id ?index ?title ?description ?applicant ?executor ?deadline)
     [?id :application/index ?index]
     [?id :application/title ?title]
     [?id :application/description ?description]
     [?id :application/applicant ?applicant]
     [?id :application/executor ?executor]
     [?id :application/deadline ?deadline]]])

(def applications-rule
  '[[(applications-rule ?id ?index  ?title ?description ?applicant ?executor ?deadline)
     [?id :application/index ?index]
     [?id :application/title ?title]
     [?id :application/description ?description]
     [?aid :person/name ?applicant]
     [?id :application/applicant ?aid]
     [?eid :person/name ?executor]
     [?id :application/executor ?eid]
     [?id :application/deadline ?deadline]]])

;; APPLICATION QUERIES
(def all-application-q
  '[:find ?id ?index ?title ?description ?applicant ?executor ?deadline
    :in $ %
    :where
    (applications-rule ?id ?index ?title ?description
                       ?applicant ?executor ?deadline)])

(def application-by-id-q
  '[:find ?id ?index ?title ?description ?applicant ?executor ?deadline
    :in $ % ?id
    :where (application-rule ?id ?index ?title ?description
                             ?applicant ?executor ?deadline)])

(def application-by-index-q
  '[:find ?id ?index ?title ?description ?applicant ?executor ?deadline
    :in $ % ?index
    :where (application-rule ?id ?index ?title ?description
                             ?applicant ?executor ?deadline)])

;; APPLICATION HANDLERS
(def application-keys
  [:id :index :title :description :applicant :executor :deadline])

(defn get-applications
  [db]
  (mapv (partial zipmap application-keys)
        (d/q all-application-q db applications-rule)))

(defn get-application-by-index
  [index db]
  (map (partial zipmap application-keys)
       (d/q application-by-index-q db application-rule (Integer/parseInt index))))

(defn get-application-by-id
  [id db]
  (map (partial zipmap application-keys)
       (d/q application-by-id-q db application-rule id)))

(defn get-applications-max-index
  [db]
  (d/q '[:find (max ?index)
         :where
         [?id :application/index ?index]]
       db))

(defn create-application
  [{:keys [title description applicant executor deadline]} conn db]
  (let [id (d/tempid :db.part/user)
        max-index (get-applications-max-index db)
        index (if-not (empty? max-index) (inc (ffirst max-index)) 1)]
    @(d/transact conn [{:db/id id
                        :application/index index
                        :application/title title
                        :application/description description
                        :application/applicant (bigint applicant)
                        :application/executor (bigint executor)
                        :application/deadline (c/to-date (fmt/parse deadline))}])))

(defn edit-application
  [id {:keys [title description applicant executor deadline]} conn]
  @(d/transact conn [{:db/id (bigint id)
                      :application/title title
                      :application/description description
                      :application/applicant (bigint applicant)
                      :application/executor (bigint executor)
                      :application/deadline (c/to-date (fmt/parse deadline))}]))

