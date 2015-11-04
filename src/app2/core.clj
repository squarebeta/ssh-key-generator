(ns app2.core
  (:gen-class))
  (use '[clojure.java.shell :only [sh]])
  (require '[clojure.java.io :as io])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Host:  ")
  (def host (read-line))
  (println "HostName:  ")
  (def hostname (read-line))
  (println "User:  ")
  (def user (read-line))
  (println "Comment:  ")
  (def comment (read-line))
  (println "Password:  ")
  (defn fixed-length-password
        ([] (fixed-length-password 8))
        ([n]
           (let [chars (map char (range 33 127))
                 password (take n (repeatedly #(rand-nth chars)))]
             (reduce str password))))
  (def password (if (.equals (.trim (read-line)) "") (str) (fixed-length-password 64)))
  (sh "mkdir" "--parents" (str (io/file (System/getenv "HOME") ".ssh" "keys2")))
  (sh "chmod" "0700" (str (io/file (System/getenv "HOME") ".ssh" "keys2")))
  (def keyfile (.trim (get (sh "mktemp" (str (io/file (System/getenv "HOME") ".ssh" "keys2" "XXXXXXXX_id_rsa"))) :out)))
  (sh "rm" keyfile)
  (sh "ssh-keygen" "-f" keyfile "-C" comment "-P" password)
  (sh "mkdir" "--parents" (str (io/file (System/getenv "HOME") ".ssh" "control")))
  (sh "chmod" "0700" (str (io/file (System/getenv "HOME") ".ssh" "control")))
  (def ctrlfile (.trim (get (sh "mktemp" (str (io/file (System/getenv "HOME") ".ssh" "control" "XXXXXXXX_%h_%p_%r_ctrl_path"))) :out)))
  (sh "rm" ctrlfile)
  (spit (str (io/file (System/getenv "HOME") ".ssh" "config2")) (format "%nHost %1$s%nHostName %2$s%nUser %3$s%nIdentityFile %4$s%nIdentitiesOnly yes%nControlMaster auto%nControlPath %5$s%n" host hostname user keyfile ctrlfile) :append true)
  (println password)
  (println (slurp keyfile))
  (println (slurp (str keyfile ".pub")))
  (shutdown-agents)
)