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
             :pc-german           {:alphabetical ["qwertzuiopü" "asdfghjklöä"
                                                  "<yxcvbnm,.-" ">YXCVBNM;:_"
                                                  ]
                                   :numerical    ["147" "258" "369" "159" "753"]
                                   :special      ["!\"§$%&/()=?`" "{[]}\\~" ",.-#+"
                                                  ";:_'*" "/*-+"]
                                   :altgr        ["¹²³¼½¬{[]}\\¸{" "@ł€¶ŧ←↓→øþ¨~"
                                                  "æſðđŋħ.ĸł" "|»«¢„“”µ·…–"
                                                  "¡⅛'£¤⅜⅝⅞™±°¿˛" "ΩŁ€®Ŧ¥↑ıØÞ˚¯"
                                                  "ÆẞÐªŊĦ˙&Ł" "ˍ‹©‚‘’º×÷—"]}})


(defn in-range? [n bounds-list]
  (some true?
        (for [bounds bounds-list]
          (and (<= (-> bounds :lo) n) (>= (-> bounds :up) n)))))


(defn get-char-classes [password]
  (reduce (fn [acc ucp]
            (reduce #(conj %1 %2)
                    acc
                    (for [range (keys (-> config :uc-ranges))
                          :when (in-range? ucp (-> config :uc-ranges range))]
                      range)))
          #{} (map int password)))


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


(defn- digit? [char]
  (cc/nnil? (parse-long (str char))))


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


(defn- alpha-numerical? [pw-rest]
  (cc/xor (cc/nnil? (re-matches #"[a-z0-9]+" pw-rest))
          (cc/nnil? (re-matches #"[A-Z0-9]+" pw-rest))))


(defn- check-surrounding-chars [result]
  (let [password (:password result)
        sliced-pw (slice-pw password (digit? (last password)) (bad-char? (first password)) (bad-char? (last password)))
        simple (alpha-numerical? (-> sliced-pw :rest))
        valid (not (and simple (not (empty? (-> sliced-pw :bad)))))]

    (merge-with into result
                {:validation {:surrounding-chars {:valid   valid
                                                  :matches (-> sliced-pw :bad)}}})))


(defn compare-segments [string size]
  (let [pattern (take size string)]
    (reduce
      (fn [acc val]
        (if (= (count pattern) (count val))
          (if (and (= pattern val) (true? acc)) true false)
          (if (and (= (take (count val) pattern) val) (true? acc)) true false)))
      true (partition-all size string))))


(defn ceil-half [n] (if (even? n) (/ n 2) (/ (+ 1 n) 2)))


(defn find-repetitions [string min-length length]
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
        match (if (nil? pattern) [] [pattern])]

    (merge-with into result
                {:validation {:repeating-pattern {:valid   (not invalid)
                                                  :matches match}}})))


(defn- check-keyboard-patterns [result]
  (merge-with into result
              {:validation {:keyboard-patterns {:valid   false
                                                :matches []}}}))


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
