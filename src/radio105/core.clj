(ns radio105.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [org.httpkit.client :as http]
            [net.cgrand.enlive-html :as html])
  (:gen-class))

(def urls #{"http://radio105.wanex.net/arts_geo.htm" "http://radio105.wanex.net/songs_geo.htm"})

(defn- clear-screen
  []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))

(defn- show-download-progress
  [current total]
  (clear-screen)
  (let [complete (->> (/ current total)
                      (* 100)
                      Math/ceil
                      int)]
    (println
     (str (if (> complete 80) "Almost there " "Completed ") complete "%"))))

(defn- get-dom
  [url]
  (html/html-snippet (:body @(http/get url))))

(defn- build-url
  [audio-link]
  {:download-url (format "http://radio105.wanex.net/%s" audio-link)
   :filename     audio-link})

(defn- is-mp3?
  [link]
  (re-matches #"(?i).*\.mp3" link))

(defn- extract-links
  [dom]
  (->> (html/select dom [:td :a])
       (map #(get-in % [:attrs :href]))
       (filter is-mp3?)))

(defn- download-file
  [{:keys [download-url filename]}]
  (let [filename (string/replace filename #"\/" "_")
        file     (io/file "audio" filename)]
    (if-not (.exists file)
      (do
        (println "Downloading" filename)
        (with-open [in  (io/input-stream download-url)
                    out (io/output-stream file)]
          (io/copy in out)))
      (println "File" filename "already exists, skipping"))))

(defn- download-files
  [files]
  (let [total-count (count files)]
    (doall
     (for [[file index] (mapv vector files (range))
           :let         [current (+ index 1)]]
       (do
         (show-download-progress current total-count)
         (download-file file))))))

(defn -main
  [& args]
  (when-not (.isDirectory (io/file "audio"))
    (.mkdir (io/file "audio")))
  (some->> urls
           (mapcat (comp extract-links get-dom))
           (map build-url)
           download-files))
