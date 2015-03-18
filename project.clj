(defproject prismatic/cljs-test "0.0.7-SNAPSHOT"
  :clojurescript? true
  :description "Very simple cljs testing"
  :url "https://github.com/prismatic/cljs-test"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:url "git@github.com:prismatic/cljs-test.git"}
  :pom-addition [:developers [:developer
                              [:name "Prismatic"]
                              [:url "http://getprismatic.com"]
                              [:email "admin+oss@getprismatic.com"]
                              [:timezone "-8"]]]
  :cljsbuild
  {:builds
   {:test {:source-paths ["src" "test"]
           :compiler {:output-to "target/unit-test.js"
                      :optimizations :whitespace
                      :pretty-print true}}}
   :test-commands {"unit" ["phantomjs" "target/unit-test.js"]}}
  :dependencies [[org.clojure/clojurescript "0.0-2120"]]
  :plugins [[lein-cljsbuild "1.0.0"]])
