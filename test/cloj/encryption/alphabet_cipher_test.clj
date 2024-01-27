(ns cloj.encryption.alphabet-cipher-test
  (:require [cloj.encryption.alphabet_cipher :refer :all]
            [clojure.test :refer :all]))

(deftest test-encode
  (testing "can encode a message with a secret keyword"
    (is (= "egsgqwtahuiljgs"
           (encode "scones" "meetmebythetree")))))
; add test with keyword longer than message

(deftest test-decode
  (testing "can decode a message given an encoded message and a secret keyword"
    (is (= "meetmebythetree"
           (decode "scones" "egsgqwtahuiljgs")))))

(deftest test-decipher
  (testing "can extract the secret keyword given an encrypted message and the original message"
    (is (= "scones"
           (decipher "hcqxqqtqljmlzhwiivgbsapaiwcenmyu" "packmyboxwithfivedozenliquorjugs")))))
