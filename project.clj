(defproject radio105 "0.0.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]
                 [http-kit "2.3.0-beta2"]]

  :min-lein-version "2.5.3"
  :source-paths ["src"]
  :clean-targets ^{:protect false} ["target"]
  :main radio105.core
  :profiles {:uberjar {:aot :all}})
