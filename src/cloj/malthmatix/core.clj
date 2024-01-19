(ns cloj.malthmatix.core)

(defn evaluate-polynomial [coefficients x]
  (reduce + (map-indexed (fn [degree coeff] (* coeff (Math/pow x degree))) coefficients)))

(defn polynomial-derivative [coefficients]
  (map-indexed (fn [degree coeff] (* coeff degree)) (rest coefficients)))

(defn format-polynomial [coefficients]
  (->> (map-indexed
         (fn [degree coeff]
           (cond
             (= degree 0) (str coeff)
             (= degree 1) (str coeff "x")
             :else (str coeff "x^" degree)))
         coefficients)
       (remove (fn [term] (= term "0")))
       (interpose " + ")
       (apply str)))

(defn polynomial-example []
  (let [coefficients [1 2 3]] ; Represents 1 + 2x + 3x^2
    (println (format-polynomial coefficients))
    (println (evaluate-polynomial coefficients 2)) ; Evaluate for x = 2
    (println (polynomial-derivative coefficients)))) ; Derivative
