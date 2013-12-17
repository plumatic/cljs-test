(ns cljs-test.core
  (:use-macros
   [cljs-test.macros :only [is]]))

(def tests (atom {}))
(def +current-test+ (atom nil))

(defn add-test! [name f]
  (swap! tests assoc name
         {:fn f
          :stats
          {:total 0
           :pass 0
           :fail 0
           :error 0}}))

(defn update-test-stats! [state]
  (swap! tests update-in [@+current-test+ :stats :total] inc)
  (swap! tests update-in [@+current-test+ :stats state] inc))

(defn test-stats []
  (get-in @tests [@+current-test+ :stats]))

(defn assertion-state [[error pass?]]
  (if (= :no-error error)
    (if pass? :pass :fail)
    :error))

(defn style [el m]
  (doseq [[k v] m]
    (aset (.-style el) (name k) v)))

(def +state-style+
  {:pass {:color "#67f86f"}
   :notice {:color "#77d3ee"  :font-size "1.2em" :margin-top "1em" :font-weight "bold"}
   :fail {:color "#fff" :font-weight "bold"}
   :error {:color "#fff"}
   :debug {:color "#c5c5c5" :font-style "italic"}})

(def +ansi-style+
  {:pass "\u001b[32m\u001b[1m"   ; bright green
   :notice "\u001b[36m\u001b[1m" ; bright cyan
   :fail "\u001b[31m\u001b[1m"   ; bright red
   :error "\u001b[31m\u001b[1m"  ; bright red
   :debug "\u001b[37m\u001b[3m"  ; italic grey
   :default "\u001b[39m\u001b[0m"})

(defn test-state
  [{:keys [pass, fail, error]}]
  (if (and (zero? fail) (zero? error))
    :pass
    :fail))

(defn failed-tests [all-tests]
  (for [[test-name {:keys [stats]}] all-tests
        :when (= (test-state stats) :fail)]
    [test-name stats]))

(defn html-report []
  (let [el (.createElement js/document "div")
        all-tests @tests
        passed-tests (for [[test-name {:keys [stats]}] all-tests
                           :when (= (test-state stats) :pass)]
                       [test-name stats])
        failed-tests (failed-tests all-tests)
        overview-el (.createElement js/document "div")]
    (style el {:position "fixed"
               :top "10px"
               :right "10px"
               :padding "1em"
               :max-width "50%"
               :background "#111"})
    (style overview-el {:font-size "1.2em"
                        :font-weight "bold"
                        :color (if (empty? failed-tests) "#67f86f" "#d02f2f")})
    (set! (.-innerHTML overview-el)
          (if-not (empty? failed-tests)
            (str (count failed-tests) " / " (count all-tests) " tests failed")
            "All tests passed!"))
    (.appendChild el overview-el)
    (doseq [[test-name stats] failed-tests
            :let [test-el (.createElement js/document "a")]]
      (.setAttribute test-el "href" (str "#" test-name))
      (set! (.-innerHTML test-el) test-name)
      (style test-el {:color "#d02f2f"})
      (.appendChild el test-el))
    (.appendChild js/document.body el)))

(defn ansi-header [type msg]
  (when type
    (str
      (+ansi-style+ type)
      (condp = type
        :fail   "  FAIL  "
        :error  " ERROR  "
        :pass   "  PASS  "
        :debug  msg
        :notice msg)
      (+ansi-style+ :default)
      (when-not (#{:notice :debug} type) msg))))

(defn console-logger [type msg]
  (when js/window.phantom
    (.log
      js/console
      (.replace
        (ansi-header type msg)
        (js/RegExp. "\n" "g")
        "\n        "))))

(defn log [type & more]
  (let [msg (.trimRight (apply str more))]
    (when (pos? (count msg))
      (console-logger type msg)
      (let [el (.createElement js/document "div")]
        (style el (merge {:white-space "pre"}
                         (+state-style+ type)))
        (set! (.-innerHTML el) msg)
        (.appendChild js/document.body el)))))

(defn log-state [state msg]
  (console-logger state msg)
  (let [el (.createElement js/document "div")
        state-span (.createElement js/document "span")]
    (style el
           (merge {:white-space "pre"
                   :padding-bottom "0.2em"}
                  (when (#{:fail :error} state)
                    {:background-color "#d02f2f"})))
    (style state-span (merge {:display "inline-block"
                              :text-transform "uppercase"
                              :width "3.5em"
                              :text-align "right"
                              :margin-right "1em"}
                             (+state-style+ state)))
    (set! (.-innerText state-span) (name state))
    (.appendChild el state-span)
    (.appendChild el (.createTextNode js/document msg))
    (.appendChild js/document.body el)))

(set!
  *print-fn*
  (fn [s]
    (log :debug s)))

(defn run-tests! []
  (style js/document.body
         {:background "#252525"
          :color "#c7c7c7"
          :font "14px monospace"})
  (doseq [[name test] @tests
          :let [bookmark (.createElement js/document "a")]]
    (reset! +current-test+ name)
    (.setAttribute bookmark "name" name)
    (.appendChild js/document.body bookmark)
    (log :notice name)
    (is (do ((:fn test)) true)))
  (html-report)
  (when js/window.phantom
    (let [failed-tests (failed-tests @tests)]
      (js/console.log (count failed-tests) "tests failed")
      (.exit js/window.phantom (if (empty? failed-tests) 0 1)))))

(if js/window.phantom
  (.setTimeout js/window run-tests! 4000)
  (set! (.-onreadystatechange js/document)
        (fn []
          (when (identical? "complete" (.-readyState js/document))
            (run-tests!)))))