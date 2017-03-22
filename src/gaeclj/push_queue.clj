(ns gaeclj.push-queue
  (:require [gaeclj.pqutil :refer [try-with-default name?]])
  (:import [com.google.appengine.api.taskqueue 
            Queue 
            QueueFactory 
            TaskOptions 
            TaskOptions$Builder]
           [com.google.appengine.api.modules 
            ModulesService 
            ModulesServiceFactory]))

(def default-module-path nil)

(defn get-module-hostname 
  "Returns the host name of the given module, or of the current module. 

  If the executed outside of the 

  [] -> current module's host name
  [module-name] -> host name for the given module"
  ([] (get-module-hostname nil))
  ([module-name & {:keys [version]}]
    (try-with-default default-module-path 
      (-> 
      (ModulesServiceFactory/getModulesService)
      (.getVersionHostname module-name version)))))

(defn task-options 
  "Creates task options for the given URI, with key-value parameters. Note keys and values become their respective strings."
  ([^String uri ^Long eta-millis & kv-params]
    (let [opts (TaskOptions$Builder/withUrl uri)
          params (apply hash-map kv-params)]
        
        (when eta-millis 
          (.etaMillis opts eta-millis))
  
        (doseq [[key value] (seq params)]
          (.param opts (name? key) (str value)))
      opts)))

(defn get-queue 
  "Returns a queue by name

  [] -> default queue
  [queue-name] -> the named queue"
  ([] (QueueFactory/getDefaultQueue))
  ([queue-name] (QueueFactory/getQueue queue-name)))

(defn add-to-queue 
  "Adds a task (described by its task options) to the given queue."
  [queue task-options]
  (.add queue task-options))

