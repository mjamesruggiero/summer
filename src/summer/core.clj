(ns summer.core
  (:require [tentacles.core :as core]
            [tentacles.repos :as repos]
            [tentacles.pulls :as pulls]
            [clj-time.format :as format])
   (:gen-class))

(defn -main
  [& args]
  (println "Hello, World!"))

(defn- commit
  [user repo sha auth]
  (repos/specific-commit user repo sha {:auth auth}))

(defn pull-request-metadata
  "get pull request number and date"
  [user repo state auth]
  (map #(select-keys % [:number :created_at]) (pulls/pulls user repo {:auth auth :state state})))

(defn- pull-requests
  [user repo state auth]
  (pulls/pulls user repo {:auth auth :state state}))

(defn pull-commits
  "returns seq of shas"
  [user repo number auth]
  (map #(:sha %) (pulls/commits user repo number {:auth auth})))

(defn commit-changes
  [commit-map]
  "returns number of changes for a commit"
  (:total (:stats commit-map)))

(def github-formatter
  (format/formatters :date-time-no-ms))

(defn parse-github-date [datestring]
  (format/parse github-formatter datestring))
