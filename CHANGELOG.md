# Changelog

## 0.0.7

* Use latest lein-cljsbuild and clojurescript dependencies
* Added some usage notes in README.md
* Added Travis CI config
* Added console logging when running via PhantomJS, to emulate the HTML report as far as possible
* Fixed bug where (=is a b) didn't show the actual and expected values
* Capture print output and redirect to the logger with a :debug category

## 0.0.6

* Forked from Prismatic