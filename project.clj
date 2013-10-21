(defproject hn-subscribe "0.0.1"
  :java-source-paths ["src"]
  :test-paths ["test"]
  :aot :all
  :dependencies [
                   [commons-collections/commons-collections "3.2.1"]
                 ]

  :profiles {:dev
              {:dependencies [[storm "0.8.2"]
                              [org.clojure/clojure "1.4.0"]
                              [org.testng/testng "6.8.5"]
                              [org.easytesting/fest-assert-core "2.0M8"]
                              [org.mockito/mockito-all "1.9.0"]
                              [org.jmock/jmock "2.6.0"]]}}
  :min-lein-version "2.0.0"
  )
