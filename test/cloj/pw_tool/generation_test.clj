(ns cloj.pw-tool.generation-test
  (:require
    [cloj.pw-tool.generation :refer :all]
    [clojure.spec.alpha :as s]
    [clojure.test :refer :all]))


(s/def ::password string?)
(s/def ::result (s/keys :req-un [::password]))


(deftest validation-record
  (testing "produces expected record"
    (is (s/valid? ::result (generate 10)))
    (is (= 10 (count (-> (generate 10) :password))))
    ))
