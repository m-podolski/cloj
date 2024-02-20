(ns cloj.clojical.core-test
  (:require [cloj.clojical.core :as c]
            [clojure.test :refer :all]))


(deftest conjunctions
  (testing "and"
    (is (true? (c/and true true true)))
    (is (false? (c/and true false true)))
    (is (false? (c/and true true false)))
    (is (false? (c/and true false false)))
    (is (nil? (c/and true)))
    (is (nil? (c/and false)))
    (is (nil? (c/and true "string")))
    (is (nil? (c/and true 0))))

  (testing "nand"
    (is (true? (c/nand true false true)))
    (is (true? (c/nand true true false)))
    (is (true? (c/nand true false false)))
    (is (true? (c/nand false false false)))
    (is (false? (c/nand true true true)))
    (is (nil? (c/nand false)))
    (is (nil? (c/nand true)))
    (is (nil? (c/and true "string")))
    (is (nil? (c/and true 0)))))


(deftest disjunctions
  (testing "or"
    (is (true? (c/or true false false)))
    (is (true? (c/or true false true)))
    (is (true? (c/or false true false)))
    (is (false? (c/or false false false)))
    (is (nil? (c/or true)))
    (is (nil? (c/or false)))
    (is (nil? (c/or true "string")))
    (is (nil? (c/or true 0))))

  (testing "xor"
    (is (true? (c/xor true false false)))
    (is (true? (c/xor false true false)))
    (is (true? (c/xor false false true)))
    (is (false? (c/xor true true true)))
    (is (false? (c/xor true false true)))
    (is (false? (c/xor false false false)))
    (is (nil? (c/xor true)))
    (is (nil? (c/xor false)))
    (is (nil? (c/xor true "string")))
    (is (nil? (c/xor true 0))))

  (testing "nor"
    (is (true? (c/nor false false false)))
    (is (false? (c/nor false true false)))
    (is (false? (c/nor false false true)))
    (is (false? (c/nor true true true)))
    (is (false? (c/nor true false true)))
    (is (nil? (c/nor true)))
    (is (nil? (c/nor false)))
    (is (nil? (c/nor true "string")))
    (is (nil? (c/nor true 0)))))


(deftest conditionals
  (testing "lif"
    (is (true? (c/lif true true)))
    (is (true? (c/lif true true)))
    (is (false? (c/lif true false)))
    (is (true? (c/lif false true)))
    (is (true? (c/lif false false)))
    (is (nil? (c/lif true)))
    (is (nil? (c/lif false)))
    (is (nil? (c/lif true "string")))
    (is (nil? (c/lif true 0))))

  (testing "iff"
    (is (true? (c/iff true true)))
    (is (true? (c/iff false false)))
    (is (false? (c/iff true false)))
    (is (false? (c/iff false true)))
    (is (nil? (c/iff true)))
    (is (nil? (c/iff false)))
    (is (nil? (c/iff true "string")))
    (is (nil? (c/iff true 0)))))
