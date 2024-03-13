(ns cloj.pw-tool.validation-test
  (:require
    [cloj.pw-tool.validation :refer :all]
    [clojure.spec.alpha :as s]
    [clojure.test :refer :all]))


(s/def ::password string?)
(s/def ::valid boolean?)
(s/def ::length int?)
(s/def ::complexity (s/and int? pos? #(<= % 4)))
(s/def ::char-classes (s/coll-of keyword? :kind set?))
(s/def ::matches (s/coll-of (fn string-or-char? [x] (or (string? x) (char? x)))))
(s/def ::length-complexity (s/keys :req-un [::valid
                                            ::length
                                            ::complexity
                                            ::char-classes]))
(s/def ::match-results (s/keys :req-un [::valid ::matches]))
(s/def ::surrounding-chars ::match-results)
(s/def ::repeating-pattern ::match-results)
(s/def ::keyboard-patterns ::match-results)
(s/def ::validation (s/keys :req-un [::length-complexity
                                     ::surrounding-chars
                                     ::repeating-pattern
                                     ::keyboard-patterns]))
(s/def ::rating #{:strong :moderate :weak})

(s/def ::result (s/keys :req-un [::password
                                 ::validation
                                 ::rating]))


(deftest validation-record
  (testing "produces expected record"
    (is (s/valid? ::result (validate "abcdefghijklmno1987")))))
#_(s/explain ::result (validate "abcdefghijklmno1987"))


(deftest validation-rating
  (testing "rates passwords"
    (testing "strong"

      (testing "length-complexity AND surrounding-chars AND (repeating-pattern AND keyboard-pattern)"
        (is (= (-> (validate "%7_x*2Y-") :rating) :strong))
        (is (= (-> (validate "MomplMurftRundDieEck") :rating) :strong))
        (is (= (-> (validate "mompl murft rund die") :rating) :strong))
        (is (= (-> (validate "83624.68246.58549.64") :rating) :strong))
        ))

    (testing "moderate"

      (testing "NOT surrounding-chars
      (length-complexity AND (repeating-pattern AND keyboard-pattern))"
        (is (= (-> (validate "derpalmenstrand06198") :rating) :moderate))
        (is (= (-> (validate "dpalmenstrandkrabben5") :rating) :moderate))
        (is (= (-> (validate "!palmenstrandkrabbe?") :rating) :moderate))
        (is (= (-> (validate "PALMENSTRANDKRABBE$") :rating) :moderate))
        )

      (testing "NOT repeating pattern
      (length-complexity AND surrounding-chars AND keyboard-patterns)"
        (is (= (-> (validate "aB1.aB1.") :rating) :moderate))
        (is (= (-> (validate ":kG2:kG2:") :rating) :moderate))
        (is (= (-> (validate "1qW(1qW(1") :rating) :moderate))
        (is (= (-> (validate "JEU489GSJEU489GSJEU4") :rating) :moderate))
        (is (= (-> (validate "&K/D(T)&K/D(T)&K/D(T") :rating) :moderate))
        (is (= (-> (validate "m§%m§%m§%m§%m§%m§%m§") :rating) :moderate))
        (is (= (-> (validate "23{[6]}57=)(/23{[6]}") :rating) :moderate))
        (is (= (-> (validate "4q0836t8594e4q0836t8") :rating) :moderate))
        ))

    (testing "NOT keyboard patterns
    (length-complexity AND surrounding-chars AND repeating-patterns)"
      (is (= (-> (validate "abcD12.-") :rating) :moderate))
      (is (= (-> (validate ":_kjhGF1") :rating) :moderate))
      (is (= (-> (validate "12qWER()") :rating) :moderate))
      (is (= (-> (validate "WERTZUIOPASD87654321") :rating) :moderate))
      (is (= (-> (validate "&/()=?LKJHGFDSUZTREW") :rating) :moderate))
      (is (= (-> (validate "mnbvdfghjpoiuz§$%&/(") :rating) :moderate))
      (is (= (-> (validate "{[]}=)(/&%$§23456789") :rating) :moderate))
      (is (= (-> (validate "mnopqrstdefghijkl678") :rating) :moderate))
      )

    (testing "weak"

      (testing "NOT surrounding-chars AND NOT
      (repeating-pattern AND keyboard-patterns) (length-complexity)"
        (is (= (-> (validate "%7_x*2Yb") :rating) :weak))
        (is (= (-> (validate "MomplMurftRundNeEcke") :rating) :weak))
        )

      (testing "NOT length-complexity
      (surrounding-chars AND (repeating-pattern AND keyboard-patterns))"
        (is (= (-> (validate "%7_x*2Y") :rating) :weak))
        (is (= (-> (validate "MomplMurftRundNeEck") :rating) :weak))
        )

      (testing "NOT length-complexity, NOT surrounding-chars
      (repeating-pattern AND keyboard-patterns)"
        (is (= (-> (validate "derpalmenstrand1987") :rating) :weak))
        (is (= (-> (validate "!kokospalmenstrand?") :rating) :weak))
        (is (= (-> (validate "#KOKOSPALMENSTRAND$") :rating) :weak))
        )

      (testing "NOT length-complexity, NOT repeating pattern
      (surrounding-chars AND keyboard-patterns)"
        (is (= (-> (validate "aB1.aB1") :rating) :moderate))
        (is (= (-> (validate ":kG2:kG") :rating) :moderate))
        (is (= (-> (validate "1qW(1qW") :rating) :moderate))
        (is (= (-> (validate "JEU489GSJEU489GSJEU") :rating) :moderate))
        (is (= (-> (validate "&K/D(T)&K/D(T)&K/D(") :rating) :moderate))
        (is (= (-> (validate "m§%m§%m§%m§%m§%m§%m") :rating) :moderate))
        (is (= (-> (validate "23{[6]}57=)(/23{[6]") :rating) :moderate))
        (is (= (-> (validate "4q0836t8594e4q0836t") :rating) :moderate))
        )

      (testing "NOT length-complexity, NOT keyboard patterns
      (surrounding-chars AND repeating pattern)"
        (is (= (-> (validate "abcd12.") :rating) :weak))
        (is (= (-> (validate ":_kjhgf") :rating) :weak))
        (is (= (-> (validate "123WER(") :rating) :weak))
        (is (= (-> (validate "ERTZUIOPASD87654321") :rating) :weak))
        (is (= (-> (validate "&/()=?LKJHGFDUZTREW") :rating) :weak))
        (is (= (-> (validate "mnbvdfghjpoiuz§$%&/") :rating) :weak))
        (is (= (-> (validate "{[]}=)(/&%$§2345678") :rating) :weak))
        (is (= (-> (validate "mnopqrstdefghijk678") :rating) :weak))
        )

      (testing "NOT all"
        (is (= (-> (validate "abcdefghijklmno1987") :rating) :weak))
        (is (= (-> (validate "#XWVUTSRQPONMLKJIH$") :rating) :weak))
        ))))
