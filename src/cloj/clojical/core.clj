(ns cloj.clojical.core
  (:refer-clojure :exclude [and or])
  (:require [clojure.spec.alpha :as s]))


(defn- apply-to-booleans [fn args]
  (if (s/valid? (s/coll-of boolean?) args) (fn args) nil))


(defn and
  "Returns true if all args are true and false if one or more are false.
   Returns nil when called without args or if any arg is not a boolean."
  ([] nil)
  ([& args]
   (apply-to-booleans
     (fn [args]
       (reduce
         (fn [acc val] (if (true? val) (if (true? acc) true false) false))
         true args))
     args)))


(defn nand
  "Returns true if one or more args are false and false if all are true.
  Returns nil when called without args or if any arg is not a boolean."
  ([] nil)
  ([& args]
   (apply-to-booleans
     (fn [args]
       (reduce
         (fn [acc val] (if (true? val) (if (true? acc) true false) true))
         false args))
     args)))


(defn or
  "Returns true if one or more args are true and false if all are false.
  Returns nil when called without args or if any arg is not a boolean."
  ([] nil)
  ([& args]
   (apply-to-booleans
     (fn [args] (if (<= 1 (count (filter true? args))) true false))
     args)))


(defn xor
  "Returns true if exactly one arg is true and false if two or more are true
   or if all are false.
  Returns nil when called without args or if any arg is not a boolean."
  ([] nil)
  ([& args]
   (apply-to-booleans
     (fn [args] (if (= 1 (count (filter true? args))) true false))
     args)))


(defn nor
  "Returns true if all args are false and false if one or more args are true.
  Returns nil when called without args or if any arg is not a boolean."
  ([] nil)
  ([& args]
   (apply-to-booleans
     (fn [args] (if (every? false? args) true false))
     args)))


(defn lif
  "Returns true if both args are true or if the first arg is false.
  Returns false if the first arg is true and the second is false.
  Returns nil when called without args, with only one arg or if any are
  is not a boolean."
  ([] nil)
  ([x] nil)
  ([x y]
   (apply-to-booleans
     (fn [[hyp con]]
       (if (and (true? hyp) (false? con)) false true))
     (seq [x y]))))


(defn iff
  "Returns true if both args are true or if the first arg is false.
  Returns false if the first arg is true and the second is false.
  Returns nil when called without args, with only one arg or if any arg
  is not a boolean."
  ([] nil)
  ([x] nil)
  ([x y]
   (apply-to-booleans
     (fn [[hyp con]]
       (if (xor (and hyp con) (nor hyp con)) true false))
     (seq [x y]))))
