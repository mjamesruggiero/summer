(ns summer.core-test
  (:require [clojure.test :refer :all]
            [summer.core :refer [parse-github-date commit-changes]]
            [clj-time.core :as t]))

(deftest test-formatter
  (testing "when passed Github datestring, returns datetime"
    (is (= (parse-github-date "2015-10-12T17:42:17Z") (t/date-time 2015 10 12 17 42 17)))))

(def fake-commit
  {:html_url "https://github.com/fake-user/fake-repo/commit/1234567aab",
   :committer nil,
   :author nil,
   :comments_url "https://api.github.com/repos/fake-user/fake-repo/commits/1234567aab/comments",
   :commit {:author {:name "John Doe",
                     :email "fake-user.com",
                     :date "2015-11-02T19:02:26Z"},
            :committer {:name "John Doe",
                        :email "johndoe@fake-user.com",
                        :date "2015-11-02T19:02:26Z"},
            :message "Fake commit message",
            :tree {:sha "9a7a49bde393dd6df414e0fadf7a54c5c0df831f",
                   :url "https://api.github.com/repos/fake-user/fake-repo/git/trees/9a7a49bde393dd6df414e0fadf7a54c5c0df831f"},
            :url "https://api.github.com/repos/fake-user/fake-repo/git/commits/1234567aab",
            :comment_count 0},
   :parents [{:sha "9876543baa",
              :url "https://api.github.com/repos/fake-user/fake-repo/commits/9876543baa",
              :html_url "https://github.com/fake-user/fake-repo/commit/9876543baa"}],
   :url "https://api.github.com/repos/fake-user/fake-repo/commits/1234567aab",
   :files [{:additions 12,
            :raw_url "https://github.com/fake-user/fake-repo/raw/1234567aab/spec/presenters/api/direct_sell_metrics/by_creative_spec.rb",
            :contents_url "https://api.github.com/repos/fake-user/fake-repo/contents/spec/presenters/api/direct_sell_metrics/by_creative_spec.rb?ref=1234567aab",
            :patch "fake patch",
            :blob_url "https://github.com/fake-user/fake-repo/blob/1234567aab/spec/presenters/api/direct_sell_metrics/by_creative_spec.rb",
            :filename "spec/presenters/api/direct_sell_metrics/by_creative_spec.rb",
            :status "modified",
            :deletions 6,
            :changes 18,
            :sha "22fa0f8db949144b0bec99ba5be4e6d4510e30e4"}],
   :stats {:total 18,
           :additions 12,
           :deletions 6},
   :sha "1234567aab"})

(deftest test-commit-changes
  (testing "when passed a commit, grabs the number of changes"
    (is (= (commit-changes fake-commit) 18))))
