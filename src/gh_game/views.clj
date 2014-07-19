(ns gh-game.views
  (:require [gh-game.github.auth :as gh-auth]
            [gh-game.github.core :as github]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.element :refer :all])) 

(defn main-template [& body-content]
    (html5 [:head
               [:title "Github Bounties"]
               [:link {:href "//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" :rel "stylesheet"}]
           ]
         [:body body-content]
    ))

(defn status [request]
  (let [count (:count (:session request) 0)
        session (assoc (:session request) :count (inc count))]
    (-> (ring.util.response/response
           (str "<p>We've hit the session page " (:count session)
                " times.</p><p>The current session: " session "</p>"))
         (assoc :session session))))

(defn repos [request]
  (main-template [:ul.list-group
   (for [repo (github/get-repos-for-current-user (gh-auth/get-access-token-from-request request))]
    [:li.list-group-item
      [:a.btn {:href (:html_url repo)} (:name repo)]
    ]
   )]))

(defn index [request]
   (main-template [:a.btn {:href "/repos" } "My Github Repositories"]
                  [:br]
                  [:a.btn {:href "/status"} "Status"]
                  [:br]
                  [:a.btn {:href "/logout"} "Logout"]
   ) )
