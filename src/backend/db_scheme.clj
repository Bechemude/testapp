(ns backend.db-scheme
  (:require
    [datomic.api :as d]))


(def person-schema
  [{:db/ident :person/index
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db/index true
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The human-readable auto-incremental id of the person"}

   {:db/ident :person/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The name of the person"}])


(def application-schema
  [{:db/ident :application/index
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db/index true
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The human-readable auto-incremental id of the application"}

   {:db/ident :application/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The title of the application"}

   {:db/ident :application/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The description of the application"}

   {:db/ident :application/applicant
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The applicant of the application"}

   {:db/ident :application/executor
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The executor of the application"}

   {:db/ident :application/deadline
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/id (d/tempid :db.part/db)
    :db.install/_attribute :db.part/db
    :db/doc "The deadline of the application"}])
