(ns cloj.math.clojical.core
  (:refer-clojure :exclude [and or]))


(defn- apply-to-booleans [fn args]
  (if (every? boolean? args) (fn args) nil))


(defn and
  "Returns true if all args are true and false if one or more are false.
   Returns nil when called without args, with only one arg or if any arg
   is not a boolean."
  ([] nil)
  ([x] nil)
  ([x & args]
   (apply-to-booleans
     (fn [args]
       (reduce
         (fn [acc val] (if (true? val) (if (true? acc) true false) false))
         true args))
     (conj args x))))


(defn nand
  "Returns true if one or more args are false and false if all are true.
  Returns nil when called without args, with only one arg or if any arg
  is not a boolean."
  ([] nil)
  ([x] nil)
  ([x & args]
   (apply-to-booleans
     (fn [args]
       (reduce
         (fn [acc val] (if (true? val) (if (true? acc) true false) true))
         false args))
     (conj args x))))


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
