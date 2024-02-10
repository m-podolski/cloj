(ns cloj.encryption.pw-generator.validation)


(defn check-length-complexity [password]
  {:password          password
   :length-complexity {:valid      true
                       :length     20
                       :complexity 2}})


(defn check-surrounding-chars [validation-result]
  (conj validation-result {:surrounding-chars {:valid true
                                               :chars []}}))


(defn check-repeated-sequences [validation-result]
  (conj validation-result {:repeated-sequences {:valid     true
                                                :sequences []}}))


(def char-patterns {:alphabetical ["qwertzuiopü" "asdfghjklöä"
                                   "<yxcvbnm,.-" ">YXCVBNM;:_"]
                    :numerical    ["147" "258" "369" "159" "753"]
                    :special      ["!\"§$%&/()=?`" "{[]}\\~"
                                   ",.-#+" ";:_'*" "/*-+"]})

(defn check-char-patterns [validation-result]
  (conj validation-result {:char-patterns {:valid    true
                                           :patterns []}}))


(defn rate [validation-result]
  (conj validation-result {:rating   :strong
                           :problems []}))


(defn validate [password]
  (-> password
      (check-length-complexity)
      (check-surrounding-chars)
      (check-repeated-sequences)
      (check-char-patterns)
      (rate)))
