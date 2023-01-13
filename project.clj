(defproject testapp "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [nrepl "1.1.0-alpha1"]
                 [ring "1.9.6"]
                 [ring/ring-json "0.5.1"]
                 [ring-cors "0.1.13"]
                 [bananaoomarang/ring-debug-logging "1.1.0"]
                 [compojure "1.7.0"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [environ "1.2.0"]
                 [cheshire "5.11.0"]
                 [clj-time "0.15.2"]]
  :plugins [[lein-ring "0.12.6"]
            [lein-cloverage "1.2.4"]
            [lein-environ "1.2.0"]]
  :source-paths ["src"]
  :ring {:handler backend.core/app
         :port 8080
         :nrepl {:start? true}}
  :profiles {:dev {:env {:db-uri "datomic:mem://dev"}}
             :test {:env {:db-uri "datomic:mem://test"}}}

  :uberjar {:aot :all}
  :uberjar-name "testapp.jar"
  :repl-options {:init-ns backend.core})

