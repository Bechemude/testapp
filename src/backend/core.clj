(ns backend.core
  (:require
   [backend.db :refer [wrap-create-db  wrap-datomic]]
   [backend.handlers :as h]
   [compojure.core :refer [GET POST PUT defroutes context]]
   [compojure.route :as route]
   [environ.core :refer [env]]
   [ring-debug-logging.core :refer [wrap-with-logger]]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.util.response :as r]))

(defroutes app-routes
  (GET "/" [] (r/redirect "/testapp"))
  (GET "/testapp" [] (r/content-type
                      (r/resource-response "index.html" {:root "public"}) "text/html"))
  (GET "/persons" req (h/get-persons req))
  (context "/applications" []
    (GET "/" req (h/get-applications req))
    (POST "/" req (h/create-application req))
    (context "/" []
      (GET "/:index" req (h/get-application-by-index req))
      (PUT "/:id" req (h/edit-application-by-id req))))
  (route/not-found "<h1>Page not found</h1>"))

(def db-uri (or (env :db-uri) "datomic:mem://prod"))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post])
      (wrap-resource "public")
      (wrap-datomic db-uri)
      (wrap-create-db db-uri)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-with-logger)))

