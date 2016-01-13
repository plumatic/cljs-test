(defproject prismatic/cljs-test "0.0.7-SNAPSHOT"
  :clojurescript? true
  :description "Very simple cljs testing"
  :url "https://github.com/plumatic/cljs-test"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :cljsbuild
  {:builds
   {:test {:source-paths ["src" "test"]
           :compiler {:output-to "target/unit-test.js"
                      :optimizations :whitespace
                      :pretty-print true}}}
   :test-commands {"unit" ["phantomjs" "target/unit-test.js"]}}
  :dependencies [[org.clojure/clojurescript "0.0-2120"]]
  :plugins [[lein-cljsbuild "1.0.0"]])
