(ns gh-game.app
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            [clj-http.client :as client]
            [cheshire.core :as j]
            [gh-game.github.auth :as gh-auth]))

(declare render-status-page)
(declare render-repos-page)

(defroutes gh-game-app
  (GET "/" request "<a href=\"/repos\">My Github Repositories</a><br><a href=\"/status\">Status</a>")
  (GET "/status" request
       (render-status-page request))
  (GET "/repos" request (gh-auth/github-authorized render-repos-page))

  (friend/logout (ANY "/logout" request (ring.util.response/redirect "/"))))

(def app-handler
  (handler/site
   (gh-auth/github-auth gh-game-app)
   ))

(defn render-status-page [request]
  (let [count (:count (:session request) 0)
        session (assoc (:session request) :count (inc count))]
    (-> (ring.util.response/response
           (str "<p>We've hit the session page " (:count session)
                " times.</p><p>The current session: " session "</p>"))
         (assoc :session session))))

(defn get-github-repos
  "Github API call for the current authenticated users repository list."
  [access-token]
  (let [url (str "https://api.github.com/user/repos?access_token=" access-token)
        response (client/get url {:accept :json})
        repos (j/parse-string (:body response) true)]
    repos))

(defn render-repos-page
  [request]
  (str (vec (map :name (get-github-repos (gh-auth/get-access-token-from-request request))))))

