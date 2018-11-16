(ns scramblies.router
  (:require [scramblies.client :as client]
            [scramblies.routes :as routes]
            [scramblies.scramble :as scramble]
            [scramblies.state :as state]
            [#?(:clj clojure.edn :cljs cljs.reader) :as edn]
            [domkm.silk :as silk]
            [om.next :as om]
            [om.dom :as dom]
            #?@(:clj [[domkm.silk.serve :refer [ring-handler]]
                      [hiccup.core :as h]
                      [hiccup.page :as page]
                      [liberator.core :as liberator]])))

#?(:cljs (enable-console-print!))

(defmulti response identity)

#?(:clj
   (defmethod response :scramble
     [route]
     (liberator/resource
      :allowed-methods [:get]
      :malformed? (fn [{{{{:keys [query]} :domkm.silk/url} :params} :request}]
                    (not (and (boolean (get query "str1"))
                              (boolean (get query "str2")))))
      :handle-malformed (fn [_]
                          "Must provide 'str1' and 'str2' query parameters.")
      :available-media-types ["application/json"
                              "application/edn"
                              "text/html"]
      :handle-ok (fn [{{{{:keys [query]} :domkm.silk/url} :params} :request}]
                   {:scramble? (scramble/scramble? (get query "str1")
                                                   (get query "str2"))
                    :str1 (get query "str1")
                    :str2 (get query "str2")}))))

#?(:clj
   (defmethod response :graph
     [route]
     (fn [request]
       {:status 200
        :headers {"Content-Type" "application/edn"}
        :body (state/parser {:state state/app-state}
                            (edn/read-string (slurp (:body request))))})))

(defmethod response :index
  [route]
  (let [react-root (om/add-root! state/reconciler
                                 client/ScrambleUI
                                 #?(:clj nil
                                    :cljs (.getElementById js/document "app")))]
    #?(:clj
       (fn [request]
         {:status 200
          :headers {"Content-Type" "text/html"}
          :body (page/html5
                 [:head [:title "Scramblies"]]
                 [:body [:div#app (dom/render-to-str react-root)]
                  (page/include-js "/js/app.js")])})
       :cljs
       react-root)))

(defmethod response :default
  [route]
  (fn [request]
    {:status 404
     :headers {"Content-Type" "text/plain"}
     :body "Error 404"}))

(defn route->response
  [matched-route]
  (response matched-route))

#?(:clj
   (def route-handler
     (ring-handler routes/routes route->response)))

(defn path->name
  [url]
  (:domkm.silk/name (silk/arrive routes/routes url)))

(defn name->path
  [route]
  (silk/depart routes/routes route))

;; Mount the React DOM Root corresponding with the current path
#?(:cljs
   (-> (.. js/window -location -pathname)
       path->name
       route->response))
