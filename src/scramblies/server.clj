(ns scramblies.server
  (:require [org.httpkit.server :as server]
            [scramblies.router :as router]))

(defonce ^:private server (atom nil))

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
      (reset! server (server/run-server #'router/route-handler {:port port})))
    (println (str "Started server on port " port "."))))

(defn restart [] (stop) (start))

(defn -main [] (start))
