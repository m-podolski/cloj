(ns cloj.pw-tool.main-test
  (:require
    [cloj.pw-tool.main :refer :all]
    [cloj.pw-tool.validation :refer :all]
    [clojure.test :refer :all]))


(deftest command-dispatch
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
      (is (= (-> data :result :rating) :weak)))

    (let [data (dispatch '("generate"))]
      (is (= (-> data :command) :generate))
      (is (= (-> data :problem) nil))
      (is (= (-> data :result :password) "password")))
    ))


(deftest console-main
  (testing "runs"
    (let [out-str (with-out-str (-main))]
      (is (= out-str "Invalid Command: No command given")))

    (let [out-str (with-out-str (-main "validate" "mompl"))]
      (is (= out-str ":weak")))
    ))
