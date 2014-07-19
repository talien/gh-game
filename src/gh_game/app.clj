(ns gh-game.app
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            [cheshire.core :as j]
            [gh-game.github.auth :as gh-auth]
            [gh-game.github.core :as github]
            [gh-game.views :as views]
            ))

(defroutes gh-game-app
  (GET "/" request views/index)
  (GET "/status" request views/status)
  (GET "/repos" request (gh-auth/github-authorized views/repos))
  (GET "/issues/:repo" request (gh-auth/github-authorized views/issues))
  (route/resources "/")

  (friend/logout (ANY "/logout" request (ring.util.response/redirect "/"))))

(def app-handler
  (handler/site
   (gh-auth/github-auth gh-game-app)
   ))
