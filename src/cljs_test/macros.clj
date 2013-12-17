(ns cljs-test.macros
  "Simple Test library for ClojureScript. Runs each deftest after declaration and prints test statistics to console

  TODO: Handle async tests (defasynctest) which take completion callback
  TODO: Handle detecting tests have finished in phantom case and exit with return value
  TODO: Provide HTML scaffold to pretty display test results in browser"
  (:require [clojure.java.io :as io]
            [clojure.template :as template]))

(defmacro read-json
  "read json from classpath, useful for test resources"
  [f]
  `(js/JSON.parse ~(slurp f)))

(defmacro read-clj
  "read clojure literal which is valid cljs"
  [f]
  ~(read (slurp f)))

(defmacro deftest
  [nm & body]
  `(cljs-test.core/add-test! ~(name nm) (fn [] ~@body)))

(defmacro safe-eval [expr]
  (let [ex-sym (gensym "exception")]
    `(try
       [:no-error ~expr]
       (catch js/Error ~ex-sym
         (cljs-test.core/log :error (.-stack ~ex-sym))
         [:error ~ex-sym]))))

(defmacro is
  ([expr]
     `(is ~expr ~(str expr)))
  ([expr msg]
     `(let [as# (cljs-test.core/assertion-state (safe-eval ~expr))]
        (cljs-test.core/update-test-stats! as#)
        (cljs-test.core/log-state as# ~msg))))

(defmacro is-thrown?
  ([expr]
     `(is-thrown? ~expr ~(str expr)))
  ([expr msg]
     `(is
       (try (do ~expr false) (catch js/Error _# true))
       ~msg)))

(defmacro is=
  ([a b]
     (let [msg (str "(= " `~a " " `~b ")")]
       `(is= ~a ~b ~msg)))
  ([a b msg]
     `(let [lhs# (safe-eval ~a)
            rhs# (safe-eval ~b)
            as# (case [(first lhs#) (first rhs#)]
                  [:no-error :no-error] (cljs-test.core/assertion-state
                                         (safe-eval (= (second lhs#) (second rhs#))))
                  :error)
            msg# (if (= as# :fail)
                   (str ~msg " (not= " (second lhs#) " " (second rhs#) ")")
                   ~msg)]
        (cljs-test.core/update-test-stats! as#)
        (cljs-test.core/log-state as# msg#))))

(defmacro are
  [argv expr & args]
  (if (or
       ;; (are [] true) is meaningless but ok
       (and (empty? argv) (empty? args))
       ;; Catch wrong number of args
       (and (pos? (count argv))
            (pos? (count args))
            (zero? (mod (count args) (count argv)))))
    `(template/do-template ~argv (is ~expr) ~@args)
    (throw (IllegalArgumentException. "The number of args doesn't match are's argv."))))
