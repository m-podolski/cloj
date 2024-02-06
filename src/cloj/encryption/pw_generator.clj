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
        "validate" {:command (nth args 0) :problem :no-arg}
        "generate" {:command (nth args 0)
                    :problem nil
                    :result  (generate)}
        {:command (nth args 0) :problem :unknown-command})
    2 (case (nth args 0)
        "validate" {:command (nth args 0)
                    :problem nil
                    :result  (validate (nth args 1))}
        "generate" {:command (nth args 0) :problem :too-many-args}
        {:command (nth args 0) :problem :unknown-command})
    {:command (nth args 0) :problem :too-many-args}
    )
  )


(defn -main [& args]
  (print (dispatch args)))
