(ns asmaxxing.i18n
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [asmaxxing.i18n.en :as en]
            [asmaxxing.i18n.pt :as pt]))

(def dictionaries {:pt pt/messages :en en/messages})

(def fallback :en)

(defn detect []
  (if (str/starts-with? (str/lower-case (or (.-language js/navigator) "en")) "pt") :pt :en))

(defonce locale (r/atom (detect)))

(defn set-locale! [loc]
  (reset! locale loc)
  (.setAttribute (.-documentElement js/document) "lang" (if (= loc :pt) "pt-BR" "en")))

(defn toggle! [] (set-locale! (if (= @locale :pt) :en :pt)))

(defn- fill [template args]
  (reduce
   (fn [acc [i a]]
     (if (nil? a)
       acc
       (str/replace acc (str "%" (inc i)) (fn [_] (str a)))))
   template
   (map-indexed vector args)))

(defn t
  ([path] (t path nil))
  ([path args]
   (let [p (if (vector? path) path [path])
         template (or (get-in (dictionaries @locale) p)
                      (get-in (dictionaries fallback) p))]
     (if template
       (fill template args)
       (str/join "/" (map #(if (keyword? %) (name %) (str %)) p))))))
