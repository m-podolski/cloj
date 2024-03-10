(ns cloj.pw-tool.main
  (:require
    [cloj.pw-tool.generation :refer :all]
    [cloj.pw-tool.validation :refer :all]))


(defn dispatch [args]
  (let [arg-count (count args)]
    (case arg-count
      0 {:problem :no-command}
      (case (nth args 0)
        "validate" (case arg-count
                     1 {:command :validate :problem :no-arg}
                     2 {:command :validate :result (validate (nth args 1))}
                     {:command :validate :problem :too-many-args})
        "generate" (case arg-count
                     1 {:command :generate :result (generate)}
                     {:command :generate :problem :too-many-args})
        {:command (nth args 0) :problem :unknown-command}))))


(def problems
  {:no-command      {:message "No command given"}
   :unknown-command {:message "Unknown command"}
   :no-arg          {:message "No argument given"}
   :too-many-args   {:message "Too many arguments given"}})


(defmulti print-record (fn [record] [(if (nil? (:problem record))
                                       (:command record)
                                       :problem)]))

(defmethod print-record [:problem] [record]
  (print (format "Invalid Command: %s" (:message ((:problem record) problems)))))

(defmethod print-record [:validate] [record]
  (print (-> record :result :rating)))

(defmethod print-record [:generate] [record]
  (print (-> record :result :password)))


(defn -main [& args]
  (print-record (dispatch args)))
