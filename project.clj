(defproject gh-game "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.11"] [lein-coffee "0.4.1"]]
  :ring {:handler gh-game.app/app-handler}
  :lein-coffee
  {:compile-hook true ;; Invoke coffee at `lein compile`
   :jar-hook true ;; Invoke coffee at `lein jar`
   :coffee-version ">=1.6"
   :coffee
      {:sources ["src/coffee/test.coffee"]
       :output "resources/public/js/"
       :bare true
      }
  }
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.0"]
                 [ring/ring-jetty-adapter "1.3.0"]
                 [com.cemerick/friend "0.2.0"]
                 [friend-oauth2 "0.1.1"]
                 [hiccup "1.0.5"]
                 [korma "0.3.0"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [compojure "1.1.8"]
                 [com.novemberain/monger "2.0.0"]
                ])
