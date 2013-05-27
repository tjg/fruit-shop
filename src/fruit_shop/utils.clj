(ns fruit-shop.utils
  (:use cascalog.api
        cascalog.util
        [cascalog.more-taps :only [hfs-delimited]]
        [clojure.pprint :only [cl-format pprint]])
  (:require [cascalog.ops   :as ops]
            [clojure.string :as str]
            [cheshire.core  :as json]))

(defn textline-parsed [n dir]
  "parse input file, it's one hash serialized as an s-expression per line"
  (let [source (hfs-textline dir)
        output-vars (map #(str "?a" %) (range n))]
    (<- output-vars
        (source ?line)
        (read-string ?line :>> output-vars)
        (:distinct false))))


(defn sort-by-field [field query]
  (ops/first-n query 100
               :sort [field]
               :reverse true))
