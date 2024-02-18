(ns cloj.encryption.pw-generator.validation)
(ns cloj.encryption.pw-generator.validation
  (:require [cloj.math.clojical.core :as mc]))


(defn- check-length-complexity [password]
  {:password   password
   :validation {:length-complexity {:valid      true
                                    :length     20
                                    :complexity 2}}})


(defn- check-surrounding-chars [result]
  (merge-with into result {:validation {:surrounding-chars {:valid   false
                                                            :matches []}}}))


(defn- check-repeated-sequences [result]
  (merge-with into result {:validation {:repeated-sequences {:valid   false
                                                             :matches []}}}))


(def char-patterns {:pc-german {:alphabetical ["qwertzuiopü" "asdfghjklöä"
                                               "<yxcvbnm,.-" ">YXCVBNM;:_"]
                                :numerical    ["147" "258" "369" "159" "753"]
                                :special      ["!\"§$%&/()=?`" "{[]}\\~"
                                               ",.-#+" ";:_'*" "/*-+"]}})

(defn- check-char-patterns [result]
  (merge-with into result {:validation {:char-patterns {:valid   false
                                                        :matches []}}}))


(defn- xor
  ([] nil)
  ([& preds]
   (if (= 1 (count (filter true? preds))) true false)))

(defn- rate [result]
  (let [valid? (fn [res-key] (-> result :validation res-key :valid))]
    (cond
      (and
        (valid? :length-complexity)
        (valid? :surrounding-chars)
        (and (valid? :repeated-sequences) (valid? :char-patterns)))
      (conj result {:rating :strong})

      (and (valid? :length-complexity)
           (mc/xor (valid? :surrounding-chars)
                   (and (valid? :repeated-sequences) (valid? :char-patterns))))
      (conj result {:rating :moderate})

      (mc/xor (valid? :length-complexity)
              (or (valid? :surrounding-chars)
                  (and (valid? :repeated-sequences) (valid? :char-patterns))))
      (conj result {:rating :weak}))))


(defn validate [password]
  (-> password
      (check-length-complexity)
      (check-surrounding-chars)
      (check-repeated-sequences)
      (check-char-patterns)
      (rate)))
