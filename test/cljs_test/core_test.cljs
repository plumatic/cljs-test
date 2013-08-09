(ns cljs-test.core-test
  (:use-macros
   [cljs-test.macros :only [deftest is= is is-thrown?]])
  (:require
   [cljs-test.core :as test]))

(deftest test
  (is= {:pass 0 :fail 0 :total 0 :error 0} (test/test-stats))
  (is 1)
  (is= {:pass 2 :fail 0 :total 2 :error 0} (test/test-stats))
  (is nil "Expected fail")
  (is= {:pass 3 :fail 1 :total 4 :error 0} (test/test-stats))
  (is (.makeUpFn (js-obj)) "Expected exception")
  (is= {:pass 4 :fail 1 :total 6 :error 1} (test/test-stats))
  (is-thrown? (+ 2 3) "Expected fail")
  (is= {:pass 5 :fail 2 :total 8 :error 1} (test/test-stats))
  (is-thrown? (throw (js/Error. "pwned")))
  (is= {:pass 7 :fail 2 :total 10 :error 1} (test/test-stats))
  (if (= {:pass 7 :fail 2 :total 11 :error 1} (test/test-stats))
    (swap! test/tests assoc-in [@test/+current-test+ :stats]
           {:pass 7 :fail 0 :total 7 :error 0})))
