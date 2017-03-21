(ns gaeclj.test.fixtures
(:import [com.google.appengine.tools.development.testing 
          LocalServiceTestConfig 
          LocalServiceTestHelper
          LocalFileServiceTestConfig 
          LocalDatastoreServiceTestConfig
          LocalTaskQueueTestConfig
          LocalAppIdentityServiceTestConfig
          LocalBlobstoreServiceTestConfig])
(:require [clojure.java.io :as io])
(:use clojure.test))

(defn- queue-config []
  (doto 
      (LocalTaskQueueTestConfig.)
    (.setQueueXmlPath "war-resources/WEB-INF/queue.xml")
    (.setDisableAutoTaskExecution true)))

(defn- create-local-test-helper []
  (LocalServiceTestHelper. (into-array LocalServiceTestConfig [(queue-config)])))

(defn setup-local-service-test-helper [f] 
  (let [helper (create-local-test-helper)]
    (try 
      (.setUp helper)
      (f)
      (finally 
        (.tearDown helper)))))
