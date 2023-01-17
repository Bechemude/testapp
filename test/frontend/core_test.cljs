(ns frontend.core-test
  (:require
   [re-frame.core :as rf]
   [day8.re-frame.test :as rf-test]
   [cljs.test :refer [deftest is run-tests testing]]))

(deftest init
  (rf-test/run-test-sync

    ;; Init db values 
   (rf/dispatch [:initialize])

    ;; Define subscriptions to the app state
   (let [modal (rf/subscribe [:modal])
         application (rf/subscribe [:application])
         error (rf/subscribe [:error])]

     (testing "init db values"
       (is (nil? @modal))
       (is (nil? @application))
       (is (nil? @error)))

     (testing "open modal"
       (rf/dispatch [:modal {:is-opened? true :type "edit"}])
       (is (= {:is-opened? true :type "edit"} @modal)))

     (testing "fill application form"
       (rf/dispatch [:handle-change :title "title"])
       (is (= {:data {:title "title"}} @application))

       (rf/dispatch [:handle-change :desctiption "desc"])
       (is (= {:data {:title "title" :desctiption "desc"}} @application))

       (rf/dispatch [:handle-change :applicant "17592186045420"])
       (is (= {:data {:title "title"
                      :desctiption "desc"
                      :applicant "17592186045420"}}
              @application))

       (rf/dispatch [:handle-change :executor "17592186045420"])
       (is (= {:data {:title "title"
                      :desctiption "desc"
                      :applicant "17592186045420"
                      :executor "17592186045420"}}
              @application))

       (rf/dispatch [:handle-change :deadline "2222-11-11"])
       (is (=
            {:data {:title "title"
                    :desctiption "desc"
                    :applicant "17592186045420"
                    :executor "17592186045420"
                    :deadline "2222-11-11"}}
            @application)))

     (testing "set error"
       (rf/dispatch [:error "error"])
       (is (= "error" @error))))))

(comment
  (run-tests))
