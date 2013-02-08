# cljs-test: Sane ClojureScript Testing

Simple testing library for ClojureScript, mirroring `clojure.test` as much as possible. When running in an environment where [phantomJS](http://phantomjs.org/) exists, each `deftest` runs after declaration and prints test statistics to terminal. When running in an actual browser, including test JavaScript in an HTML file will run them using the Google Closure Unit Test runner to do a nice visual rundown of failed assertions.

 <b>Note:</b> This is an alpha release and much left to do, will add things like support tests with asynchronous elements (`defasynctest`) which take completion callbacks. 

## Usage

```clojure
(ns mytest-ns
  (:use-macros [cljs-test.core :only [deftest is= is]]))
  
(deftest simple-case
  (is= 1 (+ 0 1))
  (is true)
  (is nil)
  (are [x y] (= x y)
   2 (+ 1 1)
   4 (+ 2 2)))  
```


## License

Copyright (C) 2013 Prismatic.  Distributed under the Eclipse Public License, the same as Clojure.
