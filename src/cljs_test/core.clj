(ns cljs-test.core
  "Simple Test library for ClojureScript. When running in an environment where
  phantom exists, runs each deftest after declaration and prints test statistics
  to terminal. When running in browser environment utilizes the Google Closure Unit Test
  runner to do a nice visual rundown of failed assertions.

  TODO: Handle async tests (defasynctest) which take completion callback
  TODO: Handle detecting tests have finished in phantom case and exit with return value"
  (:require [clojure.java.io :as io]))

(defmacro read-json
  "read json from classpath, useful for test resources"
  [f]
  `(js/JSON.parse ~(slurp f)))

(defmacro read-clj
  "read clojure literal which is valid cljs

   TODO: Async test cases (defasynctest test-name [finished-callback] 
   which take a finished callback and optional test timeout arg"
  [f]
  ~(read (slurp f)))

(def ^:dynamic *cljs-test-name* (atom nil))


(defmacro deftest
  [nm & body]
  (let [testname (str "test-cljs-" (name nm))]    
    (reset! *cljs-test-name* testname)
    `(let [test-stats# (atom {:total 0 :pass 0 :fail 0 :error 0})]
       (aset js/window ~(str @*cljs-test-name* "-stats") test-stats#)
       (aset goog.global ~testname (fn ~(symbol (name nm)) [] ~@body))             
       ;; when phantom defined, run testname
       (when (.-phantom js/window)
         (.log js/console "RUNNING test " ~(name nm))
         ((aget goog.global ~testname))
         (.log js/console 
             (if (= (:pass @test-stats#) (:total @test-stats#))
               (str "\033[92mTEST PASSED: " ~(name nm) " "
                    (:pass @test-stats# 0) "/" (:total @test-stats# 0) " assertions\033[0m")
               (str "\033[91mTEST FAILED: " ~(name nm) " "
                    (:fail @test-stats# 0) " failures " (:error @test-stats# 0) " errors\033[0m")))
         (.log js/console "\n")))))

(defmacro safe-eval [expr]
  (let [ex-sym (gensym "exception")]
    `(try
       [:no-error ~expr]
       (catch ~ex-sym 
         (when (.-phantom js/window)
           (.log js/console (str "\033[93m" (.-stack ~ex-sym) "\033[0m\n")))
         [:error ~ex-sym]))))

(defmacro assertion-state [expr]
  `(let [pair# (safe-eval ~expr)]
     (if (= :no-error (first pair#))
       (if (second pair#) :pass :fail)
       :error)))

(defmacro test-statistics []
  `(aget js/window ~(str @*cljs-test-name* "-stats")))

(defmacro update-test-stats! [assertion-state]
  `(let [test-stats# (test-statistics)]
     (swap! test-stats# update-in [:total] inc)      
     (swap! test-stats# update-in [~assertion-state] inc)))

(defmacro is
  ([expr] `(is ~expr ~(str `~expr)))
  ([expr msg]
     `(if (.-phantom js/window)
        (let [assertion-state# (assertion-state ~expr)]
          (update-test-stats! assertion-state#)
          (case assertion-state#
              :fail (.log js/console "\033[91mFAIL: " ~msg)
              :error (.log js/console "\033[93mERROR: " ~msg)
              :pass nil))
        (window/assertTrue ~msg ~expr))))

(defmacro is=
  ([a b]
  (let [msg (str "(= " `~a " " `~b ")")]
       `(is= ~a ~b ~msg)))
  ([a b msg]
     (let [form (cons '= (list `~a `~b))]
       `(if (.-phantom js/window)
          (let [lhs# (safe-eval ~a)
                rhs# (safe-eval ~b)
                as# (case [(first lhs#) (first rhs#)]
                      [:no-error :no-error] (assertion-state (= (second lhs#) (second rhs#)))
                      :error)]
            (update-test-stats! as#)
            (case as#
                :fail (.log js/console (str "\033[91mFAIL "  ~msg
                                            " (not= " (second lhs#) " " (second rhs#) ")"))
                :error (.log js/console (str "\033[91mERROR " ~msg))
                :pass nil))
          (window/assertEquals ~msg ~a ~b)))))

