(defproject cloj "0.1.0-SNAPSHOT"
  :description "A space for trying out functional programming"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.11.1"]
                 ]
  :main cloj.pw-tool
  :repl-options {:init-ns cloj.core}
  :profiles {:kaocha {:dependencies [[lambdaisland/kaocha "1.87.1366"]]}}
  :aliases {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]})
