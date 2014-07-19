(ns gh-game.app
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            [clj-http.client :as client]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri get-access-token-from-params]]
            [cheshire.core :as j]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(declare render-status-page)
(declare render-repos-page)
(declare get-github-repos)

(def client-config
  {:client-id (System/getenv "GITHUB_CLIENTID")
   :client-secret (System/getenv "GITHUB_SECRET")
   :callback {:domain "http://localhost:3000" :path "/github"}})

(def uri-config
  {:authentication-uri {:url "https://github.com/login/oauth/authorize"
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (format-config-uri client-config)
                                :scope "user"
                                }}

   :access-token-uri {:url "https://github.com/login/oauth/access_token"
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (format-config-uri client-config)}}})

(defn authed-repos [request]
  (friend/authorize #{::user} (render-repos-page request)))

(defroutes gh-game-app
  (GET "/" request "<a href=\"/repos\">My Github Repositories</a><br><a href=\"/status\">Status</a>")
  (GET "/status" request
       (render-status-page request))
  (GET "/repos" request authed-repos)

  (friend/logout (ANY "/logout" request (ring.util.response/redirect "/"))))

(def app-handler
  (handler/site
   (friend/authenticate
    gh-game-app
    {:allow-anon? false
     :workflows [(oauth2/workflow
                  {:client-config client-config
                   :uri-config uri-config
                   :auth-error-fn (fn [error]
                                   (ring.util.response/response error))
                   :access-token-parsefn get-access-token-from-params
                   :credential-fn (fn [token]
                                   {:identity token
                                    :roles #{::user}})
                  })]})))

(defn render-status-page [request]
  (let [count (:count (:session request) 0)
        session (assoc (:session request) :count (inc count))]
    (-> (ring.util.response/response
           (str "<p>We've hit the session page " (:count session)
                " times.</p><p>The current session: " session "</p>"))
         (assoc :session session))))

(defn get-access-token-from-request [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (:access-token (first (first authentications)))]
        access-token))

(defn render-repos-page
  [request]
  (str (vec (map :name (get-github-repos (get-access-token-from-request request))))))

(defn get-github-repos
  "Github API call for the current authenticated users repository list."
  [access-token]
  (let [url (str "https://api.github.com/user/repos?access_token=" access-token)
        response (client/get url {:accept :json})
        repos (j/parse-string (:body response) true)]
    repos))
