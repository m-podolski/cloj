(ns cloj.encryption.alphabet_cipher
  (:require [clojure.string :as cs]))


(defn- chain-kw [keyword message]
  (let [times (+ 1 (quot (count message) (count keyword)))
        chain (apply concat (take times (repeat (seq keyword))))]

    (take (count message) chain)))


(def ucp-a (int \a))
(def ucp-z (int \z))


(defn- encrypt-char [kw-char msg-char]
  (let [pos-kw-char (- (int kw-char) ucp-a)
        rest (- ucp-z (int msg-char))]

    (if (<= pos-kw-char rest)
      (char (+ (int msg-char) pos-kw-char))
      (char (+ ucp-a (- pos-kw-char (+ 1 rest)))))))


(defn- decrypt-char [kw-char cp-char]
  (let [pos-msg-char (- (- (int kw-char) ucp-a)
                        (- (int cp-char) ucp-a))]

    (if (<= 0 pos-msg-char)
      (char (- (+ 1 ucp-z) pos-msg-char))
      (char (+ ucp-a (* -1 pos-msg-char))))))


(defn- decipher-char [cp-char msg-char]
  (let [pos-cp-char (- (int cp-char) ucp-a)
        pos-msg-char (- (int msg-char) ucp-a)
        offset-cp-char (+ (- ucp-z pos-msg-char)
                          (- pos-cp-char ucp-a))]

    (if (< (- ucp-z ucp-a) offset-cp-char)
      (char (+ ucp-a (- pos-cp-char pos-msg-char)))
      (char (+ ucp-a
               (- (+ 1 ucp-z) pos-msg-char)
               (- pos-cp-char ucp-a))))))


(defn- segments-equal? [seg-1 seg-2]
  (let [mismatches (filter nil? (map #(cond (= %1 %2) %) seg-1 seg-2))]
    (empty? mismatches)))

(defn find-kw [kw-chain seg-size]
  "assumes keyword is at least 3 characters long and will only return the first segment if it happens to consist of repeating sequences (i.e 'dodo')"
  (if (segments-equal? (take seg-size kw-chain)
                       (take seg-size (drop seg-size kw-chain)))
    (take seg-size kw-chain)
    (recur kw-chain (+ 1 seg-size))))


(defn- map-to-string [fn coll-1 coll-2]
  (cs/join (map #(fn %1 %2) coll-1 coll-2)))


(defn encode [keyword message]
  (map-to-string encrypt-char (chain-kw keyword message) (seq message)))

(defn decode [keyword message]
  (map-to-string decrypt-char (chain-kw keyword message) (seq message)))

(defn decipher [cipher message]
  (let [kw-chain (map #(decipher-char %1 %2) (seq cipher) (seq message))
        kw (find-kw kw-chain 3)]
    (cs/join kw)))
