(ns cloj.encryption.pw-generator-test
  (:require
    [cloj.encryption.pw-generator.main :refer :all]
    [cloj.encryption.pw-generator.validation :refer :all]
    [clojure.test :refer :all]))

(deftest test-main
  (testing "runs"
    (let [out-str (with-out-str (-main))]
      (is (= out-str "Invalid Command: No command given")))
    (let [out-str (with-out-str (-main "validate" "mompl"))]
      (is (= out-str ":strong")))
    ))

(deftest test-dispatch
  (testing "handles invalid args"
    (is (= {:command nil :problem :no-command} (dispatch '())))
    (is (= {:command :validate :problem :no-arg} (dispatch '("validate"))))
    (is (= {:command "murf" :problem :unknown-command}
           (dispatch '("murf" "mompl"))))
    (is (= {:command :generate :problem :too-many-args}
           (dispatch '("generate" "this"))))
    (is (= {:command :generate :problem :too-many-args}
           (dispatch '("generate" "this" "more")))))

  (testing "handle valid args"
    (is (= {:command :validate :problem nil :result {:rating :strong}}
           (dispatch '("validate" "mompl"))))
    (is (= {:command :generate :problem nil :result {:password "password"}}
           (dispatch '("generate"))))))


(deftest test-validate
  (testing "rates passwords"
    (testing "strong"
      (is (= {:rating :strong} (validate "%7_x*2Y-")))
      (is (= {:rating :strong} (validate "MomplMurftRundDieEck")))
      (is (= {:rating :strong} (validate "mompl murft rund die")))
      (is (= {:rating :strong} (validate "83624.68246.58549.64"))))

    (testing "moderate"
      (testing "with surrounding special characters on simple pw"
        (is (= {:rating :moderate} (validate "derpalmenstrand06198")))
        (is (= {:rating :moderate} (validate "palmenstrandkrabben5")))
        (is (= {:rating :moderate} (validate "!palmenstrandkrabbe?")))
        (is (= {:rating :moderate} (validate "#PALMENSTRANDKRABBE$"))))

      (testing "with character patterns"
        (is (= {:rating :moderate} (validate "abcD12.-")))
        (is (= {:rating :moderate} (validate ":_kjhGF1")))
        (is (= {:rating :moderate} (validate "12qWER()")))
        (is (= {:rating :moderate} (validate "WERTZUIOPASD87654321")))
        (is (= {:rating :moderate} (validate "&/()=?LKJHGFDSUZTREW")))
        (is (= {:rating :moderate} (validate "mnbvdfghjpoiuz§$%&/(")))
        (is (= {:rating :moderate} (validate "{[]}=)(/&%$§23456789")))
        (is (= {:rating :moderate} (validate "mnopqrstdefghijkl678")))))

    (testing "weak"
      (testing "without sufficient length/complexity ratio"
        (is (= {:rating :weak} (validate "%7_x*2Y")))
        (is (= {:rating :weak} (validate "MomplMurftRundNeEck"))))

      (testing "with surrounding special characters on simple pw"
        (is (= {:rating :weak} (validate "derpalmenstrand1987")))
        (is (= {:rating :weak} (validate "!kokospalmenstrand?")))
        (is (= {:rating :weak} (validate "#KOKOSPALMENSTRAND$"))))

      (testing "with character patterns"
        (is (= {:rating :weak} (validate "abcd12.-")))
        (is (= {:rating :weak} (validate ":_kjhgfd")))
        (is (= {:rating :weak} (validate "123WER()")))
        (is (= {:rating :weak} (validate "ERTZUIOPASD87654321")))
        (is (= {:rating :weak} (validate "&/()=?LKJHGFDUZTREW")))
        (is (= {:rating :weak} (validate "mnbvdfghjpoiuz§$%&/")))
        (is (= {:rating :weak} (validate "{[]}=)(/&%$§2345678")))
        (is (= {:rating :weak} (validate "mnopqrstdefghijkl78"))))

      (testing "failing all criteria"
        (is (= {:rating :weak} (validate "abcdefghijklmno1987")))
        (is (= {:rating :weak} (validate "#XWVUTSRQPONMLKJIH$"))))))
