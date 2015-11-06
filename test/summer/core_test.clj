(ns summer.core-test
  (:require [clojure.test :refer :all]
            [summer.core :refer [parse-github-date
                                 commit-changes
                                 pull-request-metadata
                                 pull-commits
                                 to-month]]
            [clj-time.core :as t]))

(def test-github-datestring
  "2015-10-12T17:42:17Z")

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

(def fake-prs
  [
   {:html_url "https://github.com/fake-user/fake-repo/pull/1020",
    :merge_commit_sha "9f6739f3b411954114014c419a538208e3d398a2",
    :patch_url "https://github.com/fake-user/fake-repo/pull/1020.patch",
    :closed_at nil,
    :review_comment_url "https://api.github.com/repos/fake-user/fake-repo/pulls/comments{/number}",
    :number 1020,
    :milestone nil,
    :merged_at nil,
    :statuses_url "https://api.github.com/repos/fake-user/fake-repo/statuses/9a45c4cb1ce0ba0b7745fff93a9e223cf2c2b39e",
    :state "open",
    :issue_url "https://api.github.com/repos/fake-user/fake-repo/issues/1020",
    :title "Add loc credit card form for self serve buyers",
    :commits_url "https://api.github.com/repos/fake-user/fake-repo/pulls/1020/commits",
    :updated_at "2015-11-02T22:52:09Z",
    :head
    {:label "fake-user:cc/loc-payment-form" },
    :diff_url "https://github.com/fake-user/fake-repo/pull/1020.diff",
    :comments_url "https://api.github.com/repos/fake-user/fake-repo/issues/1020/comments",
    :locked false,
    :id 49442433,
    :url "https://api.github.com/repos/fake-user/fake-repo/pulls/1020",
    :base {:label "fake-user:master"},
    :_links {},
    :body "Fake body",
    :user {:html_url "https://github.com/bob" },
    :review_comments_url "https://api.github.com/repos/fake-user/fake-repo/pulls/1020/comments",
    :assignee nil,
    :created_at "2015-11-02T15:11:32Z"
    }
   {
    :html_url "https://github.com/fake-user/fake-repo/pull/1018",
    :merge_commit_sha "233d9e584e6c22380b7d73c9b1f0a5ada95f603e",
    :patch_url "https://github.com/fake-user/fake-repo/pull/1018.patch",
    :closed_at nil,
    :review_comment_url "https://api.github.com/repos/fake-user/fake-repo/pulls/comments{/number}",
    :number 1018,
    :milestone nil,
    :merged_at nil,
    :statuses_url "https://api.github.com/repos/fake-user/fake-repo/statuses/ae2702e5ee062d70f89c358301a44a9904d192e4",
    :state "open",
    :issue_url "https://api.github.com/repos/fake-user/fake-repo/issues/1018",
    :title "Rewrite private marketplace to support new angular routing",
    :commits_url "https://api.github.com/repos/fake-user/fake-repo/pulls/1018/commits",
    :updated_at "2015-11-02T23:43:12Z",
    :head {:label "fake-user:ab/private-marketplace-angular-rewrite" },
    :diff_url "https://github.com/fake-user/fake-repo/pull/1018.diff",
    :comments_url "https://api.github.com/repos/fake-user/fake-repo/issues/1018/comments",
    :locked false,
    :id 49201972,
    :url "https://api.github.com/repos/fake-user/fake-repo/pulls/1018",
    :base {:label "fake-user:master" },
    :_links {:self {:href "https://api.github.com/repos/fake-user/fake-repo/pulls/1018"} },
    :body "another fake body",
    :user {:html_url "https://github.com/carol" },
    :review_comments_url "https://api.github.com/repos/fake-user/fake-repo/pulls/1018/comments",
    :assignee nil,
    :created_at "2015-10-29T21:23:03Z"
    }])


(def fake-commit-seq
 [
  {
    :sha "87bbebf01a403a31408223d85dda6077a0492d8b",
    :commit {:author {:name "Alice Smith and Bob Smith",
                      :email "fake-user@fake-user.com",
                      :date "2015-11-02T18:59:17Z"},
             :committer {:name "Alice Smith and Bob Smith",
                         :email "fake-user@fake-user.com",
                         :date "2015-11-02T18:59:17Z"},
             :message "This message is a fake",
             :tree {:sha "d78154197703722550fa1c2e25f56c775adc4ea2",
                    :url "https://api.github.com/repos/fake-user/fake-repo/git/trees/d78154197703722550fa1c2e25f56c775adc4ea2"},
             :url "https://api.github.com/repos/fake-user/fake-repo/git/commits/87bbebf01a403a31408223d85dda6077a0492d8b",
             :comment_count 0},
    :url "https://api.github.com/repos/fake-user/fake-repo/commits/87bbebf01a403a31408223d85dda6077a0492d8b",
    :html_url "https://github.com/fake-user/fake-repo/commit/87bbebf01a403a31408223d85dda6077a0492d8b",
    :comments_url "https://api.github.com/repos/fake-user/fake-repo/commits/87bbebf01a403a31408223d85dda6077a0492d8b/comments",
    :author nil,
    :committer nil,
    :parents [{:sha "a6045177b5e7bac103e347913a35bf5d29da2173",
               :url "https://api.github.com/repos/fake-user/fake-repo/commits/a6045177b5e7bac103e347913a35bf5d29da2173",
               :html_url "https://github.com/fake-user/fake-repo/commit/a6045177b5e7bac103e347913a35bf5d29da2173"}]
    }
    {
    :sha "0d453af295fdf0769c11576a5545fe5f44ec8079",
    :commit {:author {:name "Alice Smith and Bob Smith",
                      :email "fake-user@fake-user.com",
                      :date "2015-11-02T22:18:57Z"},
             :committer {:name "Alice Smith and Bob Smith",
                         :email "fake-user@fake-user.com",
                         :date "2015-11-02T22:18:57Z"},
             :message "This message is not a real message",
             :tree {:sha "90a060a366edffe116488da3d121700dd6fe722f",
                    :url "https://api.github.com/repos/fake-user/fake-repo/git/trees/90a060a366edffe116488da3d121700dd6fe722f"},
             :url "https://api.github.com/repos/fake-user/fake-repo/git/commits/0d453af295fdf0769c11576a5545fe5f44ec8079",
             :comment_count 0},
    :url "https://api.github.com/repos/fake-user/fake-repo/commits/0d453af295fdf0769c11576a5545fe5f44ec8079",
    :html_url "https://github.com/fake-user/fake-repo/commit/0d453af295fdf0769c11576a5545fe5f44ec8079",
    :comments_url "https://api.github.com/repos/fake-user/fake-repo/commits/0d453af295fdf0769c11576a5545fe5f44ec8079/comments",
    :author nil,
    :committer nil,
    :parents [{:sha "87bbebf01a403a31408223d85dda6077a0492d8b",
               :url "https://api.github.com/repos/fake-user/fake-repo/commits/87bbebf01a403a31408223d85dda6077a0492d8b",
               :html_url "https://github.com/fake-user/fake-repo/commit/87bbebf01a403a31408223d85dda6077a0492d8b"}]
    }
    {
    :sha "fee996e7a6ed6290b6b06eb2ac87a3f7998f2688",
    :commit {:author {:name "Alice Smith and Bob Smith",
                      :email "fake-user@fake-user.com",
                      :date "2015-11-03T00:01:19Z"},
             :committer {:name "Alice Smith and Bob Smith",
                         :email "fake-user@fake-user.com",
                         :date "2015-11-03T00:01:19Z"},
             :message "Another fake message",
             :tree {:sha "e6df24477414404df90027e68790f339b700b9d2",
                    :url "https://api.github.com/repos/fake-user/fake-repo/git/trees/e6df24477414404df90027e68790f339b700b9d2"},
             :url "https://api.github.com/repos/fake-user/fake-repo/git/commits/fee996e7a6ed6290b6b06eb2ac87a3f7998f2688",
             :comment_count 0},
    :url "https://api.github.com/repos/fake-user/fake-repo/commits/fee996e7a6ed6290b6b06eb2ac87a3f7998f2688",
    :html_url "https://github.com/fake-user/fake-repo/commit/fee996e7a6ed6290b6b06eb2ac87a3f7998f2688",
    :comments_url "https://api.github.com/repos/fake-user/fake-repo/commits/fee996e7a6ed6290b6b06eb2ac87a3f7998f2688/comments",
    :author nil,
    :committer nil,
    :parents [{:sha "0d453af295fdf0769c11576a5545fe5f44ec8079",
               :url "https://api.github.com/repos/fake-user/fake-repo/commits/0d453af295fdf0769c11576a5545fe5f44ec8079",
               :html_url "https://github.com/fake-user/fake-repo/commit/0d453af295fdf0769c11576a5545fe5f44ec8079"}]
    }
    {
    :sha "91fe7446aabeabc9c0baf96b4c7493f62147dae8",
    :commit {:author {:name "Alice Smith and Bob Smith",
                      :email "fake-user@fake-user.com",
                      :date "2015-11-03T00:01:58Z"},
             :committer {:name "Alice Smith and Bob Smith",
                         :email "fake-user@fake-user.com",
                         :date "2015-11-03T00:01:58Z"},
             :message "Fake commit message",
             :tree {:sha "c659536e37803a6c94f1ad79e03b0f8992f25be5",
                    :url "https://api.github.com/repos/fake-user/fake-repo/git/trees/c659536e37803a6c94f1ad79e03b0f8992f25be5"},
             :url "https://api.github.com/repos/fake-user/fake-repo/git/commits/91fe7446aabeabc9c0baf96b4c7493f62147dae8",
             :comment_count 0},
    :url "https://api.github.com/repos/fake-user/fake-repo/commits/91fe7446aabeabc9c0baf96b4c7493f62147dae8",
    :html_url "https://github.com/fake-user/fake-repo/commit/91fe7446aabeabc9c0baf96b4c7493f62147dae8",
    :comments_url "https://api.github.com/repos/fake-user/fake-repo/commits/91fe7446aabeabc9c0baf96b4c7493f62147dae8/comments",
    :author nil,
    :committer nil,
    :parents [{:sha "fee996e7a6ed6290b6b06eb2ac87a3f7998f2688",
               :url "https://api.github.com/repos/fake-user/fake-repo/commits/fee996e7a6ed6290b6b06eb2ac87a3f7998f2688",
               :html_url "https://github.com/fake-user/fake-repo/commit/fee996e7a6ed6290b6b06eb2ac87a3f7998f2688"}]
    }
  ]
)

(def expected-shas
 '("87bbebf01a403a31408223d85dda6077a0492d8b"
    "0d453af295fdf0769c11576a5545fe5f44ec8079"
    "fee996e7a6ed6290b6b06eb2ac87a3f7998f2688"
    "91fe7446aabeabc9c0baf96b4c7493f62147dae8"))

(deftest test-commit-changes
  (testing "when passed a commit, grabs the number of changes"
    (is (= (commit-changes fake-commit) 18))))

(deftest test-pr-metadata
  (testing "given a seq of PRs, grab date and the number"
    (is (= (pull-request-metadata fake-prs) '({:created_at "2015-11-02T15:11:32Z", :number 1020}
                                              {:created_at "2015-10-29T21:23:03Z", :number 1018})))))
(deftest test-pull-commits
  (testing "given a seq of commits, get just the shas"
    (is (= (pull-commits fake-commit-seq) expected-shas))))

(deftest test-formatter
  (testing "when passed Github datestring, returns datetime"
    (is (= (parse-github-date test-github-datestring) (t/date-time 2015 10 12 17 42 17)))))

(deftest test-convert-to-mont
  (testing "given a datestring, give the first day of that month"
    (is (= (t/date-time 2015 10) (to-month test-github-datestring)))))
