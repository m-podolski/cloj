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
    (let [data (dispatch '())]
      (is (= (-> data :command) nil))
      (is (= (-> data :problem) :no-command)))

    (let [data (dispatch '("validate"))]
      (is (= (-> data :command) :validate))
      (is (= (-> data :problem) :no-arg)))

    (let [data (dispatch '("murf" "mompl"))]
      (is (= (-> data :command) "murf"))
      (is (= (-> data :problem) :unknown-command)))

    (let [data (dispatch '("generate" "this"))]
      (is (= (-> data :command) :generate))
      (is (= (-> data :problem) :too-many-args)))

    (let [data (dispatch '("generate" "this" "more"))]
      (is (= (-> data :command) :generate))
      (is (= (-> data :problem) :too-many-args)))
    )

  (testing "handles valid args"
    (let [data (dispatch '("validate" "mompl"))]
      (is (= (-> data :command) :validate))
      (is (= (-> data :problem) nil))
      (is (= (-> data :result :rating) :strong)))

    (let [data (dispatch '("generate"))]
      (is (= (-> data :command) :generate))
      (is (= (-> data :problem) nil))
      (is (= (-> data :result :password) "password")))
    ))


(deftest test-validate
  (testing "rates passwords"
    (testing "strong"
      (is (= (-> (validate "%7_x*2Y-") :rating) :strong))
      (is (= (-> (validate "MomplMurftRundDieEck") :rating) :strong))
      (is (= (-> (validate "mompl murft rund die") :rating) :strong))
      (is (= (-> (validate "83624.68246.58549.64") :rating) :strong))
      )

    (testing "moderate"
      (testing "with surrounding special characters on simple pw"
        (is (= (-> (validate "derpalmenstrand06198") :rating) :moderate))
        (is (= (-> (validate "dpalmenstrandkrabben5") :rating) :moderate))
        (is (= (-> (validate "!palmenstrandkrabbe?") :rating) :moderate))
        (is (= (-> (validate "PALMENSTRANDKRABBE$") :rating) :moderate))
        )

      (testing "with character patterns"
        (is (= (-> (validate "abcD12.-") :rating) :moderate))
        (is (= (-> (validate ":_kjhGF1") :rating) :moderate))
        (is (= (-> (validate "12qWER()") :rating) :moderate))
        (is (= (-> (validate "WERTZUIOPASD87654321") :rating) :moderate))
        (is (= (-> (validate "&/()=?LKJHGFDSUZTREW") :rating) :moderate))
        (is (= (-> (validate "mnbvdfghjpoiuz§$%&/(") :rating) :moderate))
        (is (= (-> (validate "{[]}=)(/&%$§23456789") :rating) :moderate))
        (is (= (-> (validate "mnopqrstdefghijkl678") :rating) :moderate))
        ))

    (testing "weak"
      (testing "without sufficient length/complexity ratio"
        (is (= (-> (validate "%7_x*2Y") :rating) :weak))
        (is (= (-> (validate "MomplMurftRundNeEck") :rating) :weak))
        )

      (testing "with surrounding special characters on simple pw"
        (is (= (-> (validate "derpalmenstrand1987") :rating) :weak))
        (is (= (-> (validate "!kokospalmenstrand?") :rating) :weak))
        (is (= (-> (validate "#KOKOSPALMENSTRAND$") :rating) :weak))
        )

      (testing "with character patterns"
        (is (= (-> (validate "abcd12.-") :rating) :weak))
        (is (= (-> (validate ":_kjhgfd") :rating) :weak))
        (is (= (-> (validate "123WER()") :rating) :weak))
        (is (= (-> (validate "ERTZUIOPASD87654321") :rating) :weak))
        (is (= (-> (validate "&/()=?LKJHGFDUZTREW") :rating) :weak))
        (is (= (-> (validate "mnbvdfghjpoiuz§$%&/") :rating) :weak))
        (is (= (-> (validate "{[]}=)(/&%$§2345678") :rating) :weak))
        (is (= (-> (validate "mnopqrstdefghijk678") :rating) :weak))
        )

      (testing "failing all criteria"
        (is (= (-> (validate "abcdefghijklmno1987") :rating) :weak))
        (is (= (-> (validate "#XWVUTSRQPONMLKJIH$") :rating) :weak))
        ))))
