**UPDATE 3/18/2015: this library is no longer under active development -- we'd recommend checking out [clojurescript.test](https://github.com/cemerick/clojurescript.test) or ClojureScript's new built-in [cljs.test](https://groups.google.com/forum/#!topic/clojure/gnCl0CySSk8)**

# cljs-test [![Build Status](https://secure.travis-ci.org/Prismatic/cljs-test.png)](http://travis-ci.org/Prismatic/cljs-test)


Simple testing library for ClojureScript, mirroring `clojure.test` as much as possible. Each `deftest` runs after declaration and prints test statistics to console. Intended usage is with [phantomJS](http://phantomjs.org/) and `lein cljsbuild test` to get a readable test summary. In the future, we'll add some HTML scaffolding to support visual test results in a browser.

 <b>Note:</b> This is an alpha release and much left to do, will add things like support tests with asynchronous elements (`defasynctest`) which take completion callbacks. Also visual HTML representation.

## Usage

```clojure
(ns mytest-ns
  (:require cljs-test.core)
  (:use-macros [cljs-test.macros :only [deftest is= is]]))

(deftest simple-case
  (is= 1 (+ 0 1))
  (is true)
  (is nil))
```

Add a section in your ```project.clj``` as follows:

```clojure
  {:builds
   {:test {:source-paths ["src" "test"]
           :compiler {:output-to "target/unit-test.js"
                      :optimizations :whitespace
                      :pretty-print true}}}
   :test-commands {"unit" ["phantomjs" "target/unit-test.js"]}}
```
### Testing in the browser

To generate the test runner,

    $ lein clean
    $ lein cljsbuild once test

Add an HTML file (recommended ```resources/test.html```) with the following content:

```html
<html>
  <body>
    <script src="../target/unit-test.js"></script>
  </body>
</html>
```
Open the HTML file in a browser to execute the tests.

### Testing using PhantomJS

Run

    $ lein clean
    $ lein cljsbuild test

## License

Copyright (C) 2013 Prismatic.  Distributed under the Eclipse Public License, the same as Clojure.
