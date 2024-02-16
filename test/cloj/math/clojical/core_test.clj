(ns cloj.math.clojical.core-test
  (:refer-clojure :exclude [and or])
  (:require [cloj.math.clojical.core :as c]
            [clojure.test :refer :all]))


(deftest conjunctions
  (testing "and"
    (is (true? (c/and true true)))
    (is (true? (c/and true true true)))
    (is (false? (c/and true)))
    (is (false? (c/and true false)))
    (is (nil? (c/and true "string")))
    (is (nil? (c/and true 0)))
    )

  (testing "nand"
    (is (true? (c/nand true)))
    (is (true? (c/nand true false)))
    (is (true? (c/nand true true false)))
    (is (false? (c/nand true true)))
    (is (false? (c/nand true true true)))
    ))


(deftest disjunctions
  (testing "or"
    (is (true? (c/or)))
    )
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
