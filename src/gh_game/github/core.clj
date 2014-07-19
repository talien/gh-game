(ns gh-game.github.core
   (:require [clj-http.client :as client]
             [cheshire.core :as j]))

(defn query-github
  [query-path access-token]
  (let [url (str "https://api.github.com/" query-path "?access_token=" access-token)
     response (client/get url {:accept :json})
     result (j/parse-string (:body response) true)]
  result))

(defn get-repos-for-current-user
  "Github API call for the current authenticated users repository list."
  [access-token]
  (query-github "user/repos" access-token))

(defn get-current-user
  [access-token]
  (query-github "user" access-token))

(defn get-issues-for-repo
  [access-token owner repo]
  (query-github (str "repos/" owner "/" repo "/issues") access-token))
