(ns cloj.pw-tool.validation
  (:require [cloj.clojical.core :as cc]))


(def config {:long                {:len 20 :comp 2}
             :short               {:len 8 :comp 4}
             :keyboard-layout     :pc-german
             :uc-ranges           {:numbers    [{:lo 48 :up 57}]
                                   :upper-case [{:lo 65 :up 90}]
                                   :lower-case [{:lo 97 :up 122}]
                                   :special    [{:lo 33 :up 47}
                                                {:lo 58 :up 64}
                                                {:lo 91 :up 96}
                                                {:lo 123 :up 126}]}
             :bad-surr-chars      [\$ \! \? \#]
             :min-len-repetitions 3
             :min-len-key-pat     5
             :pc-german           {:alphabetical "abcdefghijklmnopqrstuvwxyz"
                                   :upper-case   "!\"§$%&/()=?QWERTZUIOPÜ*ASDFGHJKLÖÄ>YXCVBNM;:_"
                                   :lower-case   "1234567890ßqwertzuiopüasdfghjklöä<yxcvbnm,.-"
                                   :special      "!\"§$%&/()=?`{[]}\\~,.-#+"
                                   :altgr        "¹²³¼½¬{[]}\\¸{@ł€¶ŧ←↓→øþ¨~æſðđŋħ.ĸł|»«¢„“”µ·…–¡⅛'£¤⅜⅝⅞™±°¿˛ΩŁ€®Ŧ¥↑ıØÞ˚¯ÆẞÐªŊĦ˙&Łˍ‹©‚‘’º×÷—"}})


(defn get-config-keys [config-key detect-fn password]
  (reduce (fn [acc val]
            (reduce #(conj %1 %2)
                    acc
                    (for [key (keys (-> config config-key))
                          :when (detect-fn val (-> config config-key key))]
                      key)))
          #{} (map int password)))


(defn in-range? [n bounds-list]
  (some true?
        (for [bounds bounds-list]
          (and (<= (-> bounds :lo) n) (>= (-> bounds :up) n)))))


(def get-char-classes
  (partial get-config-keys :uc-ranges in-range?))


(defn- check-length-complexity [result]
  (let [length (count (:password result))
        char-classes (get-char-classes (:password result))
        complexity (count char-classes)
        valid (cond
                (and (<= (-> config :short :len) length)
                     (= (-> config :short :comp) complexity))
                true
                (and (<= (-> config :long :len) length)
                     (<= (-> config :long :comp) complexity))
                true
                :else false)]

    (merge-with into result
                {:validation {:length-complexity {:valid        valid
                                                  :length       length
                                                  :complexity   complexity
                                                  :char-classes char-classes}}})))


(defn- is-number? [char]
  (cc/nnil? (parse-long (str char))))


(defn- bad-char? [char]
  (not (empty? (filter #(= char %) (-> config :bad-surr-chars)))))


(defn- slice-first [string] (subs string 1))

(defn- slice-last [string] (subs string 0 (- (count string) 1)))

(def slice-both (comp slice-last slice-first))


(defn- split-numbers-last [string]
  (let [number-chars (take-while
                       (fn [char] (-> char (str) (parse-long) (number?)))
                       (reverse string))
        number-seq-index (- (count string) (count number-chars))
        rest (if (< number-seq-index (count string))
               (subs string 0 number-seq-index) nil)]
    [(str rest) (vec (reverse number-chars))]))


(defn- slice-pw [password number-last bad-char-first bad-char-last]
  (cond
    (and number-last (not bad-char-first))
    {:rest (-> password (split-numbers-last) (nth 0))
     :bad  [[] (-> password (split-numbers-last) (nth 1))]}
    (and number-last bad-char-first)
    {:rest (-> password (slice-first) (split-numbers-last) (nth 0))
     :bad  [[(first password)] (-> password (split-numbers-last) (nth 1))]}
    (and bad-char-first bad-char-last)
    {:rest (slice-both password)
     :bad  [[(first password)] [(last password)]]}
    bad-char-first
    {:rest (slice-first password)
     :bad  [[(first password)] []]}
    bad-char-last
    {:rest (slice-last password)
     :bad  [[] [(last password)]]}
    :else
    {:rest password :bad [[] []]}))


(defn- alphabetical? [pw-rest]
  (cc/xor (cc/nnil? (re-matches #"[a-z]+" pw-rest))
          (cc/nnil? (re-matches #"[A-Z]+" pw-rest))))


(defn- check-surrounding-chars [result]
  (let [password (:password result)
        sliced-pw (slice-pw password
                            (is-number? (last password))
                            (bad-char? (first password))
                            (bad-char? (last password)))
        simple (alphabetical? (-> sliced-pw :rest))
        valid (-> (every? empty? (:bad sliced-pw)) (false?) (and simple) (not))]

    (merge-with into result
                {:validation {:surrounding-chars {:valid   valid
                                                  :matches (-> sliced-pw :bad)}}})))


(defn- compare-segments [string size]
  (let [pattern (take size string)]
    (reduce
      (fn [acc val]
        (if (= size (count val))
          (if (and (= pattern val) (true? acc)) true false)
          (if (and (= (take (count val) pattern) val) (true? acc)) true false)))
      true (partition-all size string))))


(defn- ceil-half [n] (if (even? n) (/ n 2) (/ (+ 1 n) 2)))


(defn- find-repetitions [string min-length length]
  (let [length (if (nil? length) (ceil-half (count string)) length)
        found-rep (compare-segments string length)
        pattern (subs string 0 length)]

    (cond
      (and (> length min-length) (false? found-rep))
      (recur string min-length (dec length))
      (and (> length min-length) (true? found-rep))
      {:found-rep true :pattern pattern}
      :else
      {:found-rep found-rep :pattern (if found-rep pattern nil)})))


(defn- check-repeating-pattern [result]
  (let [{invalid :found-rep pattern :pattern}
        (find-repetitions (:password result) (:min-len-repetitions config) nil)
        match (if-not (nil? pattern) [pattern])]

    (merge-with into result
                {:validation {:repeating-pattern {:valid   (not invalid)
                                                  :matches match}}})))


; -> password "abcde" -> (\b \c \d \e)
; run predicate for each pattern -> boolean
;   -> (\b \c \d \e) (\a \b \c ... \z)
;   (*) find index of first char in pattern
;   compare segment forwards and backwards from that index (cycle pattern)
;   if match is shorter than password drop n chars and repeat (*)
; if one true, return -> :category-key


(defn- find-index [coll item] (-> (keep-indexed (fn [ind val] (if (= val item) ind)) coll)
                                  (nth 0)))


(defn- get-matching-items [result-list compare-list from]
  (remove nil? (map #(if (= %1 %2) %1)
                    compare-list
                    (take (count compare-list) (drop from result-list)))))


(defn- match-segment-to-pattern [segment pattern min-length]
  (let [pattern-forward (cycle pattern)
        pattern-backward (cycle (reverse pattern))
        match-forward (get-matching-items
                        pattern
                        segment (find-index pattern-forward (first segment)))
        match-backward (get-matching-items
                         pattern
                         segment (find-index pattern-backward (first segment)))]

    (cond
      (and (<= min-length (count match-forward)) (= segment match-forward))
      [true match-forward]
      (and (<= min-length (count match-backward)) (= segment match-backward))
      [true match-backward]
      :else [false])))

;;'(\b \c \d \e \f)
;;'(\o \n \m \l \k)
;'(\o \n \m \l)
;'(\a \b \c \d \e \f \g \h \i \j \k \l \m \n \o \p \q \r \s \t \u \v \w \x \y \z)
;5



((fn compare-segments-with-keyboard-pattern [string size pattern]
   (let [
         ;chunk (take size string)
         ]
     (reduce
       (fn [acc val]
         ;(if (= size (count val))
         ;  (if (and (= chunk val) (true? acc)) true false)
         ;  (if (and (= (take (count val) chunk) val) (true? acc)) true false))
         (if ((fn keyboard-pattern? [segment pattern] true) val (seq pattern)) (conj acc val))
         ;acc
         ;(println val)
         )
       #{} (partition-all size string)
       )
     )
   )
 "bcdefxx" 5 "abcdefghijklmnopqrstuvwxyz"
 )


(defn- has-pattern? [string pattern-list] [true "pattern"])

((fn find-keyboard-pattern [string pattern-list min-length length]
   (let [
         pattern "value"
         ]
     [true pattern]
     )
   )
 "abcdexxx" ["abcdefghijklmnopqrstuvwxyz" "qwertzuiopü"] 5 nil
 )

;(def get-keyboard-pattern-classes
;  (partial get-config-keys ((:keyboard-layout config) config) has-pattern?))


(defn get-keyboard-pattern-classes [config-key detect-fn password]
  (reduce (fn [acc val]
            (reduce #(conj %1 %2)
                    acc
                    (for [key (keys (-> config config-key))
                          :when (-> (detect-fn val (-> config config-key key))
                                    (nth 0))]
                      key)))
          #{} (map int password)))

(get-keyboard-pattern-classes :pc-german has-pattern? "abcde")


(defn- check-keyboard-patterns [result]
  (let [
        {invalid :found-pat pattern :pattern}
        (get-keyboard-pattern-classes
          ((:keyboard-layout config) config) has-pattern? (:password result))
        match (if-not (nil? pattern) [pattern])
        ]
    (merge-with into result
                {:validation {:keyboard-patterns {:valid   (not invalid)
                                                  :matches [match]}}})
    )
  )


(defn- rate [result]
  (let [valid? (fn [res-key] (-> result :validation res-key :valid))]
    (cond
      (and
        (valid? :length-complexity)
        (valid? :surrounding-chars)
        (and (valid? :repeating-pattern) (valid? :keyboard-patterns)))
      (conj result {:rating :strong})

      (and (valid? :length-complexity)
           (cc/xor (valid? :surrounding-chars)
                   (and (valid? :repeating-pattern) (valid? :keyboard-patterns))))
      (conj result {:rating :moderate})

      (cc/xor (valid? :length-complexity)
              (or (valid? :surrounding-chars)
                  (and (valid? :repeating-pattern) (valid? :keyboard-patterns))))
      (conj result {:rating :weak})
      :else (conj result {:rating :weak}))))


(defn validate [password]
  (-> {:password password :validation {}}
      (check-length-complexity)
      (check-surrounding-chars)
      (check-repeating-pattern)
      (check-keyboard-patterns)
      (rate)))
