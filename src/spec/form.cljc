(ns spec.form
  (:require
    #?(:clj [clj-time.core :as c])
    #?(:clj [clj-time.format :as f])
    [#?(:clj clojure.spec.alpha :cljs cljs.spec.alpha) :as s]))


(s/def ::title (s/and string? not-empty))

(s/def ::description (s/and string? not-empty))

(s/def ::applicant (s/and string? not-empty))

(s/def ::executor (s/and string? not-empty))


(s/def ::deadline
  (s/and
    string?
    not-empty
    #?(:cljs #(inst? (new js/Date %)))
    #?(:cljs #(> (.getTime (new js/Date %)) (.now js/Date)))
    #?(:clj #(inst? (f/parse %)))
    #?(:clj #(c/after? (f/parse %) (c/now)))))


(s/def ::form
  (s/keys :req-un
          [::title
           ::description
           ::applicant
           ::executor
           ::deadline]))


(def spec-errors
  {::title "Field can not be empty"
   ::description "Field can not be empty"
   ::applicant  "Field can not be empty"
   ::executor "Field can not be empty"
   ::deadline  "Select correct date"})


(def default-message
  "Исправьте ошибки в поле")


(defn get-message
  [problem]
  (or (-> problem
          ::s/problems
          peek
          :via
          peek
          spec-errors)
      default-message))
