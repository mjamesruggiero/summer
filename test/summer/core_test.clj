(ns summer.core-test
  (:require [clojure.test :refer :all]
            [summer.core :refer [parse-github-date]]
            [clj-time.core :as t]))

(deftest test-formatter
  (testing "when passed Github datestring, returns datetime"
    (is (= (parse-github-date "2015-10-12T17:42:17Z") (t/date-time 2015 10 12 17 42 17)))))
