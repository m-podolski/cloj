(ns cloj.encryption.pw-generator-test
  (:require [cloj.encryption.pw-generator :refer :all]
            [clojure.test :refer :all]))

(deftest test-main
  (testing "runs"
    (let [out-str (with-out-str (-main "validate" "mompl"))]
      (is (= out-str "{:command validate, :problem nil, :result {:rating :strong}}")))))

(deftest test-dispatch
  (testing "handles invalid args"
    (is (= {:command nil :problem :no-command} (dispatch '())))
    (is (= {:command "validate" :problem :no-arg} (dispatch '("validate"))))
    (is (= {:command "murf" :problem :unknown-command} (dispatch '("murf" "mompl"))))
    (is (= {:command "generate" :problem :too-many-args}
           (dispatch '("generate" "this"))))
    (is (= {:command "generate" :problem :too-many-args}
           (dispatch '("generate" "this" "more")))))

  (testing "handle valid args"
    (is (= {:command "validate" :problem nil :result {:rating :strong}}
           (dispatch '("validate" "mompl"))))
    (is (= {:command "generate" :problem nil :result {:password "password"}}
           (dispatch '("generate"))))))


(deftest test-validate
  (testing "rates passwords"
    (testing "strong"
      (is (= {:rated :strong} (validate "hU7e*2Y-")))
      (is (= {:rated :strong} (validate "MomplSmurftMalRundDieEcke")))
      )
    (testing "moderate"
      (testing "with patterns"
        (is (= {:rated :moderate} (validate "abcd*2Y-")))
        (is (= {:rated :moderate} (validate "MomplSmurftMalRundDieEcke")))
        )
      (testing "with surrounding special characters"
        (is (= {:rated :moderate} (validate "hU7e*2Y-")))
        (is (= {:rated :moderate} (validate "MomplSmurftMalRundDieEcke")))
        )
      )
    (testing "weak"
      (testing "without sufficient length or complexity"
        (is (= {:rated :weak} (validate "ab12.-")))
        )
      (testing "with patterns or surrounding special chars"
        (is (= {:rated :weak} (validate "ab12.-")))))))
