(ns cloj.encryption.alphabet_cipher
  (:require [clojure.string :as cs]))

(defn- chain-kw [keyword message]
  (let [times (+ 1 (quot (count message) (count keyword)))
        chain (apply concat (take times (repeat (seq keyword))))]
    (take (count message) chain)))

(defn- encrypt-char [kw-char msg-char]
  (let [offset-kw-char (- (int kw-char) (int \a))
        rest (- (int \z) (int msg-char))]
    (if (<= offset-kw-char rest)
      (char (+ (int msg-char) offset-kw-char))
      (char (+ (int \a) (- offset-kw-char (+ 1 rest)))))))

(defn- decrypt-char [kw-char cp-char]
  (let [offset-msg-char (- (- (int kw-char) (int \a))
                           (- (int cp-char) (int \a)))]
    (if (<= 0 offset-msg-char)
      (char (- (+ 1 (int \z)) offset-msg-char))
      (char (+ (int \a) (* -1 offset-msg-char))))))

;  01234567890123456789012345
;  abcdefghijklmnopqrstuvwxyz
;E efghijklmnopqrstuvwxyzabcd
;M mnopqrstuvwxyzabcdefghijkl

;sconessconessco
;egsgqwtahuiljgs
;meetmebythetree

(defn- map-to-string [fn coll-1 coll-2]
  (cs/join (map #(fn %1 %2) coll-1 coll-2)))


(defn encode [keyword message]
  (map-to-string encrypt-char (chain-kw keyword message) (seq message)))

(defn decode [keyword message]
  (map-to-string decrypt-char (chain-kw keyword message) (seq message)))

(defn decipher [cipher message]
  "decypherme")
