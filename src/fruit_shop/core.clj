(ns fruit-shop.core
  (:use cascalog.api
        cascalog.util
        [clojure.pprint     :only [cl-format pprint]]
        [fruit-shop.utils   :only [textline-parsed sort-by-field]])
  (:require [cascalog.ops   :as ops]))

(def when-tap   (textline-parsed 2 "data/when/"))
;; id when
;; [0 79236]
;; [1 169081]
;; [2 204289]

(def who-tap    (textline-parsed 2 "data/who/"))
;; id name
;; [0 "george"]
;; [1 "bob"]
;; [2 "alice"]


(def where-tap  (textline-parsed 2 "data/where/"))
;; id community
;; [0 "springtown"]
;; [1 "summerville"]
;; [2 "autumnharbor"]


(def what-tap   (textline-parsed 5 "data/what/"))
;; id item profit cost nonce
;; [0 "apple" 3 10 7230104982880057344]
;; [1 "grapes" 1 3 9223372036854775807]
;; [2 "grapes" 1 3 7785266830875357184]


(def income-tap (textline-parsed 2 "data/income/"))
;; community median-income
;; ["summerville" 120000]
;; ["winterfield" 15000]
;; ["autumnharbor" 30000]



(def visits-by-community 
  (<- [?where-from ?count]
      (where-tap :> ?id ?where-from)
      (ops/count :> ?count)))

;; (?- (stdout) (sort-by-field "?count" visits-by-community))


(def profits-by-community
  (<- [?where-from ?total-profits]
      (where-tap       :> ?id ?where-from)
      (what-tap        :> ?id ?item ?profit ?cost ?nonce)

      (ops/sum ?profit :> ?total-profits)))

;; (?- (stdout) (sort-by-field "?total-profits" profits-by-community))


(def profits-per-community-visitor
  (<- [?where-from ?income ?total-profits ?count ?average-profit]

      ;; subquery
      (profits-by-community      :> ?where-from ?total-profits)

      (where-tap                 :> ?id ?where-from)
      (income-tap                :> ?where-from ?income)

      (ops/count                 :> ?count)
      (div ?total-profits ?count :> ?average-profit)))

;; (?- (stdout) (sort-by-field "?average-profit" profits-per-community-visitor))
