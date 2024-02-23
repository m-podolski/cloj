(ns cloj.pw-generator.validation
  (:require [cloj.clojical.core :as cc]))


(def config {:long            {:len 20 :comp 2}
             :short           {:len 8 :comp 4}
             :keyboard-layout :pc-german
             :uc-ranges       {:numbers    [{:lo 48 :up 57}]
                               :upper-case [{:lo 65 :up 90}]
                               :lower-case [{:lo 97 :up 122}]
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


(defn is-in-range? [n bounds-list]
  (some true?
        (for [bounds bounds-list]
          (and (<= (-> bounds :lo) n) (>= (-> bounds :up) n)))))


(defn get-char-classes [password]
  (reduce (fn [acc ucp]
            (cond
              (is-in-range? ucp (-> config :uc-ranges :numbers))
              (conj acc :numbers)
              (is-in-range? ucp (-> config :uc-ranges :upper-case))
              (conj acc :upper-case)
              (is-in-range? ucp (-> config :uc-ranges :lower-case))
              (conj acc :lower-case)
              (is-in-range? ucp (-> config :uc-ranges :special))
              (conj acc :special)))
          #{} (map int password)))


(defn- check-length-complexity [password]
  (let [length (count password)
        char-classes (get-char-classes password)
        complexity (count char-classes)
        valid (cond
                (and (<= (-> config :short :len) length)
                     (= (-> config :short :comp) complexity))
                true
                (and (<= (-> config :long :len) length)
                     (<= (-> config :long :comp) complexity))
                true
                :else false)]
    {:password   password
     :validation {:length-complexity {:valid        valid
                                      :length       length
                                      :complexity   complexity
                                      :char-classes char-classes}}}))


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
           (cc/xor (valid? :surrounding-chars)
                   (and (valid? :repeated-sequences) (valid? :char-patterns))))
      (conj result {:rating :moderate})

      (cc/xor (valid? :length-complexity)
              (or (valid? :surrounding-chars)
                  (and (valid? :repeated-sequences) (valid? :char-patterns))))
      (conj result {:rating :weak})
      :else (conj result {:rating :weak}))))


(defn validate [password]
  (-> password
      (check-length-complexity)
      (check-surrounding-chars)
      (check-repeated-sequences)
      (check-char-patterns)
      (rate)))
