(ns summer.core
  (:require [tentacles.core :as core]
            [tentacles.repos :as repos]
            [tentacles.pulls :as pulls]
            [clj-format :as format])
   (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn pull-request-metadata
  "get pull request number and date"
  [user repo state auth]
  (map #(select-keys % [:number :created_at]) (pulls/pulls user repo {:auth auth :state state})))

(defn pull-commits
  "returns seq of shas"
  [user repo number auth]
  (map #(:sha %) (pulls/commits user repo number {:auth auth})))

(defn commit-changes
  "returns number of changes for a commit"
  [user repo sha auth]
  (:total
    (:stats
      (repos/specific-commit user repo sha {:auth auth}))))

(def github-formatter
  "converts github dates into Joda datetimes"
  (format/formatters :date-time-no-ms))
