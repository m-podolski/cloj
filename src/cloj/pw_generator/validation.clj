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
             :bad-surr-chars  [\$ \! \? \#]
             :pc-german       {:alphabetical ["qwertzuiopü" "asdfghjklöä"
                                              "<yxcvbnm,.-" ">YXCVBNM;:_"]
                               :numerical    ["147" "258" "369" "159" "753"]
                               :special      ["!\"§$%&/()=?`" "{[]}\\~" ",.-#+"
                                              ";:_'*" "/*-+"]}})


(defn in-range? [n bounds-list]
  (some true?
        (for [bounds bounds-list]
          (and (<= (-> bounds :lo) n) (>= (-> bounds :up) n)))))

(defn get-char-classes [password]
  (reduce (fn [acc ucp]
            (cond
              (in-range? ucp (-> config :uc-ranges :numbers))
              (conj acc :numbers)
              (in-range? ucp (-> config :uc-ranges :upper-case))
              (conj acc :upper-case)
              (in-range? ucp (-> config :uc-ranges :lower-case))
              (conj acc :lower-case)
              (in-range? ucp (-> config :uc-ranges :special))
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


(defn- digit? [char]
  (not (nil? (parse-long (str char)))))

(defn- bad-char? [char]
  (not (empty? (filter #(= char %) (-> config :bad-surr-chars)))))

(defn- slice-first [string] (subs string 1))
(defn- slice-last [string] (subs string 0 (- (count string) 1)))
(def slice-both (comp slice-last slice-first))

(defn- slice-pw [password number-last bad-char-first bad-char-last]
  (cond
    (and number-last (not bad-char-first))
    {:rest (slice-last password)
     :bad  [(last password)]}
    (and number-last bad-char-first)
    {:rest (slice-both password)
     :bad  [(first password) (last password)]}
    (and bad-char-first bad-char-last)
    {:rest (slice-both password)
     :bad  [(first password) (last password)]}
    bad-char-first
    {:rest (slice-first password)
     :bad  [(first password)]}
    bad-char-last
    {:rest (slice-last password)
     :bad  [(last password)]}
    :else
    {:rest password :bad []}))

(defn- alphabetical? [pw-rest]
  (cc/xor (not (nil? (re-matches #"[a-z]+" pw-rest)))
          (not (nil? (re-matches #"[A-Z]+" pw-rest)))))


(defn- check-surrounding-chars [result]
  (let [password (:password result)
        number-last (digit? (last password))
        bad-char-first (bad-char? (first password))
        bad-char-last (bad-char? (last password))
        sliced-pw (slice-pw password number-last bad-char-first bad-char-last)
        simple (alphabetical? (-> sliced-pw :rest))
        valid (not (and simple (not (empty? (-> sliced-pw :bad)))))]

    (merge-with into result {:validation
                             {:surrounding-chars
                              {:valid   valid
                               :matches (-> sliced-pw :bad)}}})))


(defn- check-repeated-sequences [result]
  (merge-with into result {:validation
                           {:repeated-sequences {:valid   false
                                                 :matches []}}}))


(defn- check-char-patterns [result]
  (merge-with into result {:validation
                           {:char-patterns {:valid   false
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
