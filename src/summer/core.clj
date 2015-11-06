(ns summer.core
  (:require [tentacles.core :as core]
            [tentacles.repos :as repos]
            [tentacles.pulls :as pulls]
            [clj-time.format :as format]
            [clj-time.core :as time-core])
   (:gen-class))

(defn -main
  [& args]
  (println "Hello, World!"))

;; data accessors; wrappers around tentacles

(defn- commit
  [user repo sha auth]
  (repos/specific-commit user repo sha {:auth auth}))

(defn- pull-requests
  [user repo state auth]
  (pulls/pulls user repo {:auth auth :state state}))

(defn- commits
  [user repo number auth]
  (pulls/commits user repo number {:auth auth}))

(defn pull-request-metadata
  "get pull request number and date"
  [pr-seq]
  (map #(select-keys % [:number :created_at]) pr-seq))

(defn pull-commits
  "returns seq of shas"
  [commit-seq]
  (map #(:sha %) commit-seq))

(defn commit-changes
  [commit-map]
  "returns number of changes for a commit"
  (:total (:stats commit-map)))

(def github-formatter
  (format/formatters :date-time-no-ms))

(defn parse-github-date [datestring]
  (format/parse github-formatter datestring))

(defn to-month
  "converts github datestring to first of the month"
  [datestring]
  (let [date-val (parse-github-date datestring)]
    (time-core/date-time
      (time-core/year date-val)
      (time-core/month date-val))))
