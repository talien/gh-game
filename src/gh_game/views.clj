(ns gh-game.views
  (:require [gh-game.github.auth :as gh-auth]
            [gh-game.github.core :as github]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.element :refer :all]))

; In github.auth, we store the user name in the session when authorization happens, but the storage
; happens only after the code is executed, so we need a function which can get authenticated user from
; github directly, and only when the session variable :github-login is not set.
(defn get-user-login
   [request]
   (if (contains? (:session request) :github-login)
       (:github-login (:session request))
       (:login (github/get-current-user (gh-auth/get-access-token-from-request request)))))


(defn main-template [& body-content]
    (html5 [:head
               [:title "Github Bounties"]
               [:link {:href "//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" :rel "stylesheet"}]
               [:script {:src "/js/test.js"}]
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
      [:a.btn {:href (str "/issues/" (:name repo))} (:name repo)]
    ]
   )]))

(defn issues [request]
 (let [access-token (gh-auth/get-access-token-from-request request)
       repo (:repo (:params request))
       user (get-user-login request)]
      (main-template [:ul.list-group
        (for [issue (github/get-issues-for-repo access-token user repo)]
         [:li.list-group-item (:title issue)]
        )])))

(defn index [request]
   (main-template [:a.btn {:href "/repos" } "My Github Repositories"]
                  [:br]
                  [:a.btn {:href "/status"} "Status"]
                  [:br]
                  [:a.btn {:href "/logout"} "Logout"]
   ) )
