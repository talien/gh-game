(ns gh-game.github.auth
  (:require [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri
                                        get-access-token-from-params]]
            [gh-game.github.core :as github]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(def client-config
  {:client-id (System/getenv "GITHUB_CLIENTID")
   :client-secret (System/getenv "GITHUB_SECRET")
   :callback {:domain (System/getenv "APP_DOMAIN") :path "/github"}})

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

(defn get-access-token-from-request [request]
  (let [authentications (get-in request [:session :cemerick.friend/identity :authentications])
        access-token (:access-token (first (first authentications)))]
        access-token))

(defn store-github-login-in-session [request response]
  (if (contains? (:session request) :github-login)
    {
      :status 200
      :headers {"Content-Type" "text/html"}
      :body response
      :session (:session request)
    }
    {
      :status 200
      :headers {"Content-Type" "text/html"}
      :body response
      :session (assoc (:session request) :github-login (:login (github/get-current-user (get-access-token-from-request request))))
    }))

(defn github-authorized [controller]
  (fn [request]
    (store-github-login-in-session request (friend/authorize #{::user} (controller request)))))

(defn github-auth [router]
  (friend/authenticate
    router
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
                   })]}))
