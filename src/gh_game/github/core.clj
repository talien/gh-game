(ns gh-game.github.core
   (:require [clj-http.client :as client]
             [cheshire.core :as j]))

(defn get-repos-for-current-user
  "Github API call for the current authenticated users repository list."
  [access-token]
  (let [url (str "https://api.github.com/user/repos?access_token=" access-token)
     response (client/get url {:accept :json})
     repos (j/parse-string (:body response) true)]
  repos))


