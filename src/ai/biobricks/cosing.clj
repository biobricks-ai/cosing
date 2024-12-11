(ns ai.biobricks.cosing
  (:require [babashka.fs :as fs]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [tech.v3.dataset :as ds]
            [tech.v3.libs.parquet :as parquet]
            [clojure.string :as str]))

(def download-dir "download")
(def brick-dir "brick")

(defn csv-url [annex]
  (format "https://api.tech.ec.europa.eu/cosing20/1.0/api/annexes/%s/export-csv" annex))

(defn skip-lines [^java.io.BufferedReader rdr n]
  (when (pos? n)
    (loop []
      (when (not= (.read rdr) (int \newline))
        (recur)))
    (recur rdr (dec n))))

(defn skip-csv-header [rdr]
  (skip-lines rdr 4))

(def annexes ["II" "III" "IV" "V" "VI"])

(defn download [_kwargs]
  (fs/create-dirs download-dir)
  (doseq [annex annexes]
    (println (str "Downloading Annex " annex "..."))
    (with-open [rdr (:body (client/get (csv-url annex) {:as :reader}))
                wtr (io/writer (fs/file download-dir (str "annex-" (str/lower-case annex) ".csv")))]
      (skip-csv-header rdr)
      (io/copy rdr wtr))))

(comment
  (download nil))

(defn sanitize-ds
  "Clean up CSV data:
  - Replace \"-\" with nil"
  [ds]
  (ds/row-map ds
   (fn [row]
     (reduce-kv
      (fn [m k v]
        (cond-> m
          (= v "-") (assoc k nil)))
      row row))))

(defn build-brick [_kwargs]
  (fs/create-dirs brick-dir)
  (doseq [annex annexes]
    (println (str "Converting Annex " annex " from csv to parquet..."))
    (let [annex (str/lower-case annex)
          csv-file (fs/file download-dir (str "annex-" annex ".csv"))
          parquet-file (fs/file brick-dir (str "annex-" annex ".parquet"))]
      (-> (ds/->dataset csv-file)
          (sanitize-ds)
          (parquet/ds->parquet parquet-file)))))

(comment
  (build-brick nil))
