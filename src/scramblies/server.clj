(ns scramblies.server
  (:require [org.httpkit.server :as server]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [scramblies.router :as router]))

(defonce ^:private server (atom nil))

(def app
  (-> #'router/route-handler
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      wrap-not-modified))

(defn stop
  []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil))
  (println "Stopped server."))

(defn start
  []
  (let [port 8080]
    (when (nil? @server)
      (reset! server (server/run-server #'app {:port port})))
    (println (str "Started server on port " port "."))))

(defn restart [] (stop) (start))

(defn -main [] (start))
