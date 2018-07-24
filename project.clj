(defproject result-clj "0.1.0-SNAPSHOT"
  :description "Result for clojure"
  :url "https://github.com/0x636363/result-clj"
  :license {:name "MIT License"}
  :dependencies [[org.clojure/clojure "1.9.0"]]

  :profiles {:dev {:dependencies [[eftest "0.5.2"]]
                   :plugins [[lein-eftest "0.5.2"]]}}
  )
