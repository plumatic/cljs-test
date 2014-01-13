(ns cljs-test.core-test
  (:use-macros
   [cljs-test.macros :only [deftest is= is is-thrown? are]])
  (:require
   [cljs-test.core :as test]))

(deftest test
  (is= {:pass 0 :fail 0 :total 0 :error 0} (test/test-stats))
  (is 1)
  (is= {:pass 2 :fail 0 :total 2 :error 0} (test/test-stats))
  (test/log :debug "Some debug logging with " 3 " args")
  (is nil "Expected fail")
  (is= 5 (+ 2 2) "Expected fail - show actual & expected")
  (is= {:pass 3 :fail 2 :total 5 :error 0} (test/test-stats))
  (is (.makeUpFn (js-obj)) "Expected exception")
  (is= {:pass 4 :fail 2 :total 7 :error 1} (test/test-stats))
  (is-thrown? (+ 2 3) "Expected fail")
  (is= {:pass 5 :fail 3 :total 9 :error 1} (test/test-stats))
  (is-thrown? (throw (js/Error. "pwned")))
  (is= {:pass 7 :fail 3 :total 11 :error 1} (test/test-stats))
  (println "Some captured println logging")
  (are [x y] (zero? (mod x y))
       4 2
       9 3)
  (is= {:pass 10 :fail 3 :total 14 :error 1} (test/test-stats))
  (if (= {:pass 11 :fail 3 :total 15 :error 1} (test/test-stats))
    (swap! test/tests assoc-in [@test/+current-test+ :stats]
           {:pass 11 :fail 0 :total 11 :error 0})))
