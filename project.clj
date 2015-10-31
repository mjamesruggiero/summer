(defproject summer "0.1.0-SNAPSHOT"
  :description "Making sense of github"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [tentacles "0.3.0"]
                 [clj-time "0.8.0"]]
  :main ^:skip-aot summer.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
