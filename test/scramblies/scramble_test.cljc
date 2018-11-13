(ns scramblies.scramble-test
  (:require [scramblies.scramble :as scramble]
            #?(:clj [clojure.test :as t]
               :cljs [cljs.test :as t :include-macros true])))

(t/deftest scramble?
  (t/testing "portion of str1 can be arranged to match str2"
    (t/are [str1 str2] (scramble/scramble? str1 str2)
      "rekqodlw" "world"
      "cedewaraaossoqqyt" "codewars"))
  (t/testing "portion of str1 cannot be arranged to match str2"
    (t/are [str1 str2] (not (scramble/scramble? str1 str2))
      "world" "rekqodlw"
      "katas" "steak"))
  (t/testing "str1 and str2 as sets or vects"
    (t/are [str1 str2] (scramble/scramble? str1 str2)
      [1 2 3 4] [1 2 3]
      [5 4 3 2 1] [1 2 3]
      #{1 2 3 4} #{3 2 1}
      [1 2 3 4] #{1 2 3}))
  (t/testing "empty and nil str1 and str2"
    (t/are [str1 str2] (scramble/scramble? str1 str2)
      "abc" nil
      "abc" ""
      "" ""
      "" nil
      nil ""
      nil nil)))
