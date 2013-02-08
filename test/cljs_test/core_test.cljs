(ns cljs-test.core-test
  (:use-macros [cljs-test.core :only [deftest is= is test-statistics are]]))

(deftest simple-is
  (is= {:pass 0 :fail 0 :total 0 :error 0} @(test-statistics))
  (is 1)
  (is= {:pass 2 :fail 0 :total 2 :error 0} @(test-statistics))
  (is nil "Expected fail")
  (is= {:pass 3 :fail 1 :total 4 :error 0} @(test-statistics))
  (let [o (js-obj)]
    (is (.makeUpFn o) "Expected exception"))
  (is= {:pass 4 :fail 1 :total 6 :error 1} @(test-statistics))
  (if (= {:pass 5 :fail 1 :total 7 :error 1} @(test-statistics))
    (reset! (test-statistics) {:pass 5 :fail 0 :total 5 :error 0})))
