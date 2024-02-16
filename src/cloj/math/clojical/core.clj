(ns cloj.math.clojical.core
  (:refer-clojure :exclude [and or]))


(defn- apply-if-bools [args fn]
  (if (every? boolean? args) (fn args) nil))


(defn and
  "Returns true if all args are true and false if one or more are false.
   Returns false for single args.
   Returns nil when called without args or if any arg is not a boolean."
  ([] nil)
  ([x] false)
  ([x & args]
   (apply-if-bools
     (conj args x)
     (fn [args]
       (reduce
         (fn [acc val] (if (true? val) (if (true? acc) true false) false))
         true args)))))


(defn nand
  "(nand) returns nil."
  ([] nil)
  ([x] false)
  ([x & args])
  )


(defn or
  "(or) returns nil."
  ([] nil)
  ([x] (true? x))
  ([x & args])
  )


(defn xor
  "(xor) returns nil."
  ([] nil)
  ([& preds]
   (if (= 1 (count (filter true? preds))) true false)))


(defn nor
  "(nor) returns nil."
  ([] nil)
  ([x] x)
  ([x & args])
  )


(defn if
  "(if) returns nil."
  ([] nil)
  ([x] x)
  ([x & args])
  )


(defn iff
  "(iff) returns nil."
  ([] nil)
  ([x] x)
  ([x & args])
  )
