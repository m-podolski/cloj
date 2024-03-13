(ns cloj.pw-tool.generation
  (:require
    [cloj.pw-tool.validation :refer [config]]
    [clojure.string :as s]))


(defn get-ucp-list [char-class]
  (reduce (fn [acc val] (into acc (range (:lo val) (:up val))))
          '() (-> config :uc-ranges char-class)))


(defn generate-list [length]
  (let [all-ucp-lists (mapv get-ucp-list (keys (-> config :uc-ranges)))]

    (loop [i length char-list '()]
      (if (= 0 i)
        char-list
        (recur (dec i) (-> (mod i 4)
                           (#(nth all-ucp-lists %))
                           (rand-nth)
                           (#(conj char-list %))))))))


(defn generate [length]
  {:password (s/join (map char (shuffle (generate-list length))))})
