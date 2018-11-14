(ns scramblies.router-test
  (:require [scramblies.router :as router]
            #?@(:clj [[clojure.test :as t]
                      [ring.mock.request :as mock]
                      [ring.util.codec :as codec]]
                :cljs [[cljs.test :as t :include-macros true]])))

#?(:clj
   (t/deftest scramble
     (t/testing "Scramble resource API responses"
       (let [request (mock/request :get
                                   (str (router/name->path :scramble) "?"
                                        (codec/form-encode {:str1 "aabc"
                                                            :str2 "abc"})))]
         (t/is (= (router/route-handler (-> request
                                            (assoc-in [:headers "accept"]
                                                      "application/edn")))
                  {:status 200
                   :headers {"Content-Type" "application/edn;charset=UTF-8"
                             "Vary" "Accept"}
                   :body "{:scramble? true, :str1 \"aabc\", :str2 \"abc\"}"}))
         (t/is (= (router/route-handler request)
                  {:status 200
                   :headers {"Content-Type" "application/json;charset=UTF-8"
                             "Vary" "Accept"}
                   :body "{\"scramble?\":true,\"str1\":\"aabc\",\"str2\":\"abc\"}"}))
         (t/is (= (router/route-handler
                   (mock/request :get
                                 (str (router/name->path :scramble) "?"
                                      (codec/form-encode {:str1 "abc"
                                                          :str2 "def"}))))
                  {:status 200
                   :headers {"Content-Type" "application/json;charset=UTF-8"
                             "Vary" "Accept"}
                   :body "{\"scramble?\":false,\"str1\":\"abc\",\"str2\":\"def\"}"}))
         (t/is (= (router/route-handler
                   (mock/request :get (router/name->path :scramble)))
                  {:status 400
                   :headers {"Content-Type" "text/plain;charset=UTF-8"}
                   :body "Must provide 'str1' and 'str2' query parameters."}))))))

#?(:clj
   (t/deftest error404
     (t/testing "Error 404"
       (t/is (= (router/route-handler (mock/request :get "/invalid"))
                {:status 404
                 :headers {"Content-Type" "text/plain"}
                 :body "Error 404"})))))
