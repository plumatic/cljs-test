# cljs-test: Sane ClojureScript Testing

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


## License

Copyright (C) 2013 Prismatic.  Distributed under the Eclipse Public License, the same as Clojure.
