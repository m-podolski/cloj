(ns cloj.alphabet-cipher.alphabet-cipher-test
  (:require [cloj.alphabet-cipher.alphabet-cipher :refer :all]
            [clojure.test :refer :all]))

(deftest test-encode
  (testing "can encode a message with a secret keyword"
    (is (= "egsgqwtahuiljgs"
           (encode "scones" "meetmebythetree")))))

(deftest test-decode
  (testing "can decode a message given an encoded message and a secret keyword"
    (is (= "meetmebythetree"
           (decode "scones" "egsgqwtahuiljgs")))))

(deftest test-decipher
  (testing "can extract the secret keyword given an encrypted message and the original message"
    (is (= "scones"
           (decipher "egsgqwtahuiljgs" "meetmebythetree")))
    (is (= "scones"
           (decipher "hcqxqqtqljmlzhwiivgbsapaiwcenmyu" "packmyboxwithfivedozenliquorjugs")))
    ))
