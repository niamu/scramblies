(ns scramblies.router
  (:require [scramblies.scramble :as scramble]
            [domkm.silk :as silk]
            #?@(:clj [[domkm.silk.serve :refer [ring-handler]]
                      [liberator.core :as liberator]])))

(def routes
  (silk/routes {:index    [[]]
                :scramble [["api" "scramble"]]}))

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
                              "application/edn"]
      :handle-ok (fn [{{{{:keys [query]} :domkm.silk/url} :params} :request}]
                   {:scramble? (scramble/scramble? (get query "str1")
                                                   (get query "str2"))
                    :str1 (get query "str1")
                    :str2 (get query "str2")}))))

(defmethod response :index
  [route]
  (fn [request]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body "Hello World"}))

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
     (ring-handler routes route->response)))

(defn path->name
  [url]
  (:domkm.silk/name (silk/arrive routes url)))

(defn name->path
  [route]
  (silk/depart routes route))
