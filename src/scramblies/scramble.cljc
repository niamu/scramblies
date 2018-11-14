(ns scramblies.scramble)

(defn scramble?
  "Returns true if a portion of str1 characters can be rearranged to match str2"
  [str1 str2]
  (and (>= (count str1) (count str2))
       (let [freq1 (frequencies str1)
             freq2 (frequencies str2)]
         (every? (fn [[char freq]]
                   (>= (get freq1 char 0) freq))
                 freq2))))

(defn -main
  [& [str1 str2]]
  (if (and str1 str2)
    (prn {:scramble? (scramble? str1 str2)
          :str1 str1
          :str2 str2})
    (prn "Must provide 'str1' and 'str2' query parameters.")))
