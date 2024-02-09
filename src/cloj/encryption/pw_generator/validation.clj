(ns cloj.encryption.pw-generator.validation)

(def ratings {:strong 1 :moderate 2 :weak 3})

(def char-patterns {:alphabetical ["qwertzuiopü" "asdfghjklöä"
                                   "<yxcvbnm,.-" ">YXCVBNM;:_"]
                    :numerical    ["147" "258" "369" "159" "753"]
                    :special      ["!\"§$%&/()=?`" "{[]}\\~"
                                   ",.-#+" ";:_'*" "/*-+"]})

(defn validate [arg] {:rating :strong})
