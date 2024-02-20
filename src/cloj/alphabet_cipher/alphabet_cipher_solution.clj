(ns cloj.alphabet-cipher.alphabet-cipher-solution
  (:require [clojure.test :refer :all]))

(defn idx [c]
  (- (int c) 97))

(defn encode-one [k m]
  (char (+ (mod (+ (idx k) (idx m)) 26) 97)))

(defn decode-one [k m]
  (char (+ (mod (- (idx m) (idx k)) 26) 97)))

(defn char-pairs [s1 s2]
  (map vector (cycle s1) s2))

(defn transcode [f keyword message]
  (apply str (map #(apply f %) (char-pairs keyword message))))

(defn encode [keyword message]
  (transcode encode-one keyword message))

(defn decode [keyword message]
  (transcode decode-one keyword message))


(deftest test-encode
  (testing "can encode given a secret keyword"
    (is (= "egsgqwtahuiljgs"
           (encode "scones" "meetmebythetree")))))

(deftest test-decode
  (testing "can decode an cyrpted message given a secret keyword"
    (is (= "meetmebythetree"
           (decode "scones" "egsgqwtahuiljgs")))))
