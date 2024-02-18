(ns cloj.math.clojical.core-test
  (:refer-clojure :exclude [and or])
  (:require [cloj.math.clojical.core :as c]
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
    (is (true? (c/or true true false)))
    (is (true? (c/or true true true)))
    (is (true? (c/or true)))
    (is (false? (c/or false)))
    (is (false? (c/or false false false)))
    (is (nil? (c/or true "string")))
    (is (nil? (c/or true 0))))

  (testing "nor"
    (is (true? (c/nor)))
    )

  (testing "xor"
    (is (true? (c/xor)))
    ))


(deftest conditionals
  (testing "if"
    (is (true? (c/if)))
    )
  (testing "iff"
    (is (true? (c/iff)))
    ))
