(defproject rm-hull/cljs-test "0.0.7"
  :clojurescript? true
  :description "Very simple cljs testing"
  :url "https://github.com/rm-hull/cljs-test"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:url "git@github.com:rm-hull/cljs-test.git"}
  :clojurescript? true
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
  :dependencies [[org.clojure/clojurescript "0.0-2134"]]
  :plugins [[lein-cljsbuild "1.0.1"]])
