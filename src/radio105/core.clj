(ns radio105.core
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clojure.java.io :as io]
            [clojure.string :as string])
  (:gen-class))

(def urls #{"http://radio105.wanex.net/arts_geo.htm" "http://radio105.wanex.net/songs_geo.htm"})

(defn- get-dom
  [url]
  (html/html-snippet (:body @(http/get url))))

(defn- build-url
  [audio-link]
  {:download-url (format "http://radio105.wanex.net/%s" audio-link)
   :filename audio-link})

(defn- extract-links
  [dom]
  (map #(get-in % [:attrs :href]) (html/select dom [:td :a])))

(defn- download-file
  [{:keys [download-url filename] :as file}]
  (let [filename (string/replace filename #"\/" "_")
        file (io/file "audio" filename)]
    (if-not (.exists file)
      (do
        (println "Downloading" filename)
        (with-open [in (io/input-stream download-url)
                    out (io/output-stream file)]
          (io/copy in out)))
      (println "File" filename "already exists, skipping"))))

(defn- is-mp3?
  [link]
  (re-matches #"(?i).*\.mp3" link))

(defn -main
  [& args]
  (when-not (.isDirectory (io/file "audio"))
    (.mkdir (io/file "audio")))
  (some->> urls
           (mapcat get-dom)
           extract-links
           (filter is-mp3?)
           (map build-url)
           (run! download-file)))
