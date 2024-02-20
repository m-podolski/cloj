(ns cloj.pw-generator.validation
  (:require [cloj.math.clojical.core :as mc]))


(def config {:long-pw         {:min-len 20 :min-comp 2}
             :short-pw        {:min-len 8 :min-comp 4}
             :keyboard-layout :pc-german
             :uc-ranges       {:numbers    {:lo 48 :up 57}
                               :upper-case {:lo 65 :up 90}
                               :lower-case {:lo 97 :up 122}
                               :special    [{:lo 33 :up 47}
                                            {:lo 58 :up 64}
                                            {:lo 91 :up 96}
                                            {:lo 123 :up 126}
                                            {:lo 161 :up 255}
                                            {:lo 8192 :up 8303}
                                            {:lo 8352 :up 8399}]}
             :pc-german       {:alphabetical ["qwertzuiopü" "asdfghjklöä"
                                              "<yxcvbnm,.-" ">YXCVBNM;:_"]
                               :numerical    ["147" "258" "369" "159" "753"]
                               :special      ["!\"§$%&/()=?`" "{[]}\\~" ",.-#+"
                                              ";:_'*" "/*-+"]}})


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


(defn- check-char-patterns [result]
  (merge-with into result {:validation {:char-patterns {:valid   false
                                                        :matches []}}}))


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
