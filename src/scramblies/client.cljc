(ns scramblies.client
  (:require [scramblies.state :as state]
            [om.next :as om]
            [sablono.core :as sab]))

(om/defui ScrambleUI
  static om/IQueryParams
  (params [_]
    {:str1 nil :str2 nil})
  static om/IQuery
  (query [_]
    '[:ui/str1 :ui/str2 (:scramble/scramble {:str1 ?str1 :str2 ?str2})])
  Object
  (render [this]
    (let [{:keys [ui/str1 ui/str2 scramble/scramble]} (om/props this)]
      (sab/html
       [:div
        [:h1 "Scramblies"]
        [:form {:method :get
                :action "/api/scramble"}
         [:input {:type "text"
                  :name "str1"
                  :placeholder "str1"
                  :autoComplete "off"
                  :value (or str1 "")
                  :onChange (fn [e]
                              (let [n (.. e -target -name)
                                    v (.. e -target -value)]
                                (om/transact! this
                                              `[(ui/input {:name ~n
                                                           :value ~v})])))}]
         [:input {:type "text"
                  :name "str2"
                  :placeholder "str2"
                  :autoComplete "off"
                  :value (or str2 "")
                  :onChange (fn [e]
                              (let [n (.. e -target -name)
                                    v (.. e -target -value)]
                                (om/transact! this
                                              `[(ui/input {:name ~n
                                                           :value ~v})])))}]
         [:input {:type "submit"
                  :value "Scramble"
                  :onClick #?(:clj nil
                              :cljs (fn [e]
                                      (.preventDefault e)
                                      (om/set-query!
                                       this
                                       {:params {:str1 str1
                                                 :str2 str2}})))}]]
        [:div
         (when (every? (comp not nil?) [(:str1 scramble) (:str2 scramble)])
           [:span (str "Result: "
                       (if (:scramble? scramble) "True." "False.")
                       " \"" (:str1 scramble) "\""
                       (if (:scramble? scramble) "can" "cannot")
                       " be rearranged to match \""
                       (:str2 scramble) "\".")])]]))))

(def scramble-ui (om/factory ScrambleUI))
