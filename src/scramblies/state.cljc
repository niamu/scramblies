(ns scramblies.state
  (:refer-clojure :exclude [read])
  (:require [scramblies.scramble :as scramble]
            [#?(:clj clojure.edn :cljs cljs.reader) :as edn]
            [om.next :as om])
  #?(:cljs (:import [goog.net XhrIo])))

(def app-state (atom {}))

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value nil})))

(defmethod read :scramble/scramble
  [_ _ {:keys [str1 str2]}]
  #?(:cljs (if-let [result (get-in @app-state [:results [str1 str2]])]
             {:value result}
             {:remote true})
     :clj {:value {:scramble? (scramble/scramble? str1 str2)
                   :str1 str1
                   :str2 str2}}))

(defmulti mutate om/dispatch)

(defmethod mutate 'ui/input
  [_ _ params]
  {:action (fn []
             (swap! app-state assoc
                    (keyword "ui" (:name params))
                    (:value params)))})

#?(:cljs
   (defn remote-post
     [url]
     (fn [{:keys [remote] :as env} _]
       (.send XhrIo url
              (fn [e]
                (this-as this
                  (let [[_ {:keys [scramble? str1 str2] :as result}]
                        (edn/read-string (.getResponseText this))]
                    (swap! app-state assoc-in [:results [str1 str2]] result)
                    (prn @app-state))))
              "POST" remote
              #js {"Content-Type" "application/edn"}))))

(def parser
  (om/parser {:read read
              :mutate mutate}))

(def reconciler
  (om/reconciler (merge {:state app-state
                         :parser parser}
                        #?(:cljs {:send (remote-post "/api/graph")}))))
