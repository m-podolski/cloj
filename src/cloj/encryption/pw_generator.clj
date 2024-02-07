(ns cloj.encryption.pw-generator)

(defn validate [arg] {:rating :strong})

(defn generate [] {:password "password"})

(def ratings {:strong 1 :moderate 2 :weak 3})

(def problems
  {:no-command      {:message "No command given"}
   :unknown-command {:message "Unknown command"}
   :no-arg          {:message "No argument given"}
   :too-many-args   {:message "Too many arguments given"}})

(defn dispatch [args]
  (case (count args)
    0 {:command nil :problem :no-command}
    1 (case (nth args 0)
        "validate" {:command :validate :problem :no-arg}
        "generate" {:command :generate
                    :problem nil
                    :result  (generate)}
        {:command (nth args 0) :problem :unknown-command})
    2 (case (nth args 0)
        "validate" {:command :validate
                    :problem nil
                    :result  (validate (nth args 1))}
        "generate" {:command :generate :problem :too-many-args}
        {:command (nth args 0) :problem :unknown-command})
    (case (nth args 0)
      "validate" {:command :validate :problem :too-many-args}
      "generate" {:command :generate :problem :too-many-args}
      {:command (nth args 0) :problem :too-many-args})
    ))


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
