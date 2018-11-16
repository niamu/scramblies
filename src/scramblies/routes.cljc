(ns scramblies.routes
  (:require [domkm.silk :as silk]))

(def routes
  (silk/routes {:index    [[]]
                :scramble [["api" "scramble"]]
                :graph    [["api" "graph"]]}))
