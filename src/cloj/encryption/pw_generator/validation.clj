(ns cloj.encryption.pw-generator.validation)


(defn- check-length-complexity [password]
  {:password          password
   :length-complexity {:valid      true
                       :length     20
                       :complexity 2}})


(defn- check-surrounding-chars [result]
  (conj result {:surrounding-chars {:valid   true
                                    :matches []}}))


(defn- check-repeated-sequences [result]
  (conj result {:repeated-sequences {:valid   true
                                     :matches []}}))


(def char-patterns {:pc-german {:alphabetical ["qwertzuiopü" "asdfghjklöä"
                                               "<yxcvbnm,.-" ">YXCVBNM;:_"]
                                :numerical    ["147" "258" "369" "159" "753"]
                                :special      ["!\"§$%&/()=?`" "{[]}\\~"
                                               ",.-#+" ";:_'*" "/*-+"]}})

(defn- check-char-patterns [result]
  (conj result {:char-patterns {:valid   true
                                :matches []}}))


(defn- xor
  ([] nil)
  ([& preds]
   (if (= 1 (count (filter true? preds))) true false)))

(defn- rate [result]
  (let [valid? (fn [res-key] (-> result res-key :valid))
        problems (vec (filter #(false? (valid? %)) (keys result)))]
    (cond
      (and
        (valid? :length-complexity)
        (valid? :surrounding-chars)
        (and (valid? :repeated-sequences) (valid? :char-patterns)))
      (conj result {:rating :strong :problems problems})

      (and (valid? :length-complexity)
           (xor (valid? :surrounding-chars)
                (and (valid? :repeated-sequences) (valid? :char-patterns))))
      (conj result {:rating :moderate :problems problems})

      (xor (valid? :length-complexity)
           (or (valid? :surrounding-chars)
               (and (valid? :repeated-sequences) (valid? :char-patterns))))
      (conj result {:rating :weak :problems problems}))))


(defn validate [password]
  (-> password
      (check-length-complexity)
      (check-surrounding-chars)
      (check-repeated-sequences)
      (check-char-patterns)
      (rate)))
