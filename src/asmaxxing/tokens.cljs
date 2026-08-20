(ns asmaxxing.tokens
  (:require [clojure.string :as str]
            [asmaxxing.isa :as isa]))

(def prefixes #{"rep" "repe" "repz" "repne" "repnz" "lock" "bnd" "notrack"})

(def bare-directives
  #{"section" "segment" "global" "extern" "bits" "default" "org" "align" "times"
    "db" "dw" "dd" "dq" "dt" "ddq" "do" "resb" "resw" "resd" "resq" "rest"
    "equ" "struc" "endstruc" "istruc" "at" "iend" "use16" "use32" "use64"})

(def suffix-bits {"b" 8 "w" 16 "l" 32 "q" 64})

(def ^:private scanner-source
  (str "(;[^\\n]*|#[^\\n]*|//[^\\n]*)"
       "|(\"[^\"\\n]*\"|'[^'\\n]*')"
       "|(\\[[^\\]\\n]*\\])"
       "|((?:[a-z_.$][a-z0-9_.$@]*|-?(?:0x[0-9a-f]+|[0-9]+))?\\([^)\\n]*%[^)\\n]*\\))"
       "|((?:byte|word|dword|qword|xmmword|ymmword|zmmword|tbyte)(?:\\s+ptr)?)"
       "|(%[a-z][a-z0-9]*)"
       "|(\\$-?(?:0x[0-9a-f]+|[0-9]+))"
       "|(\\.[a-z_][a-z0-9_.]*)"
       "|([a-z_][a-z0-9_.$@]*)"
       "|(-?(?:0x[0-9a-f]+|[0-9]+h|[0-9]+))"
       "|([,:+*\\-()])"))

(def ^:private group-kinds
  [:comment :string :mem-intel :mem-att :size :reg-att :imm-att :directive :word :number :punct])

(defn detect-syntax [text]
  (cond
    (re-find #"%[a-zA-Z]" text) :att
    (re-find #"\$-?[0-9]" text) :att
    (re-find #"(?i)\bptr\b" text) :intel
    (re-find #"(?i)^\s*\.(text|data|globl|global|section|long|quad|byte)\b" text) :att
    :else :intel))

(defn- raw-scan [line]
  (let [re (js/RegExp. scanner-source "gi")]
    (loop [acc []]
      (if-let [m (.exec re line)]
        (let [text (aget m 0)
              start (.-index m)
              kind (loop [i 0]
                     (cond (>= i (count group-kinds)) :punct
                           (aget m (inc i)) (nth group-kinds i)
                           :else (recur (inc i))))]
          (recur (conj acc {:text text :start start :end (+ start (count text)) :kind kind})))
        acc))))

(defn- colon-next? [line token]
  (str/starts-with? (str/triml (subs line (:end token))) ":"))

(defn- strip-suffix [word]
  (let [base (subs word 0 (dec (count word)))
        suffix (subs word (dec (count word)))]
    (when (and (suffix-bits suffix) (isa/known? base))
      {:base base :bits (suffix-bits suffix)})))

(def ^:private att-suffix-wins #{"movq"})

(defn split-suffix [word syntax]
  (let [plain {:base word :bits nil}]
    (if (= syntax :att)
      (if (and (isa/known? word) (not (att-suffix-wins word)))
        plain
        (or (strip-suffix word) plain))
      (if (isa/known? word) plain (or (strip-suffix word) plain)))))

(defn classify [line syntax]
  (loop [[t & more] (raw-scan line)
         seen-op? false
         seen-dir? false
         out []]
    (if (nil? t)
      out
      (let [word (str/lower-case (:text t))
            bare (str/replace word #"^[%$]" "")
            kind (:kind t)
            resolved
            (cond
              (= kind :comment) :comment
              (= kind :string) :string
              (#{:mem-intel :mem-att} kind) :memory
              (and (= kind :size) (not seen-op?) (not seen-dir?)) :mnemonic
              (= kind :size) :size
              (#{:number :imm-att} kind) :immediate
              (= kind :reg-att) (if (isa/register? bare) :register :label-ref)
              (= kind :punct) :punct
              (and (= kind :directive) (colon-next? line t)) :label-def
              (and (= kind :directive) seen-op?) :label-ref
              (= kind :directive) :directive
              (and (= kind :word) (colon-next? line t) (not seen-op?)) :label-def
              (and (= kind :word) (not seen-op?) (not seen-dir?) (bare-directives word)) :directive
              (and (= kind :word) (not seen-op?) (not seen-dir?) (prefixes word)) :prefix
              (and (= kind :word) (not seen-op?) (not seen-dir?)) :mnemonic
              (and (= kind :word) (= syntax :intel) (isa/register? word)) :register
              :else :label-ref)]
        (recur more
               (or seen-op? (= resolved :mnemonic))
               (or seen-dir? (= resolved :directive))
               (conj out (assoc t :kind resolved :word word :bare bare)))))))

(def ^:private empty-mem {:base nil :index nil :scale 1 :disp 0 :symbol nil :segment nil})

(defn- number-term? [body]
  (re-find #"^-?(0x[0-9a-f]+|[0-9]+h?)$" body))

(defn- term-value [body]
  (js/parseInt (str/replace body #"h$" "") (if (str/starts-with? body "0x") 16 10)))

(defn parse-memory [text syntax]
  (if (= syntax :att)
    (let [[_ disp inner] (re-find #"^([a-zA-Z_.$][a-zA-Z0-9_.$@]*|-?(?:0x[0-9a-fA-F]+|[0-9]+))?\((.*)\)$" text)
          parts (map str/trim (str/split (or inner "") #","))
          [base index scale] (map #(str/replace (str/lower-case (or % "")) #"^%" "") (concat parts ["" "" ""]))
          numeric? (and disp (re-find #"^-?(?:0x|[0-9])" disp))]
      {:base (when (seq base) base)
       :index (when (seq index) index)
       :scale (if (seq scale) (js/parseInt scale 10) 1)
       :disp (if numeric? (js/parseInt disp) 0)
       :symbol (when (and disp (not numeric?)) disp)})
    (let [raw (str/lower-case (str/replace text #"^\[|\]$" ""))
          [_ seg after] (re-find #"^([a-z]{2}):(.*)$" raw)
          segment (when (and seg (isa/register? seg)) seg)
          inner (str/replace (if segment after raw) #"\b(rel|abs)\s+" "")
          terms (re-seq #"[+-]?\s*[^+-]+" inner)]
      (reduce
       (fn [acc term]
         (let [t (str/replace term #"\s" "")
               negative? (str/starts-with? t "-")
               body (str/replace t #"^[+-]" "")]
           (cond
             (str/blank? body) acc
             (#{"rel" "abs"} body) acc
             (re-find #"\*" body)
             (let [[a b] (str/split body #"\*")]
               (if (isa/register? a)
                 (assoc acc :index a :scale (js/parseInt b 10))
                 (assoc acc :index b :scale (js/parseInt a 10))))
             (isa/register? body)
             (if (:base acc) (assoc acc :index body) (assoc acc :base body))
             (number-term? body)
             (assoc acc :disp (cond-> (term-value body) negative? -))
             :else (assoc acc :symbol body))))
       (assoc empty-mem :segment segment) terms))))

(defn parse-line [line syntax]
  (let [tokens (classify line syntax)
        mnemonic-token (some #(when (= :mnemonic (:kind %)) %) tokens)
        raw-mnemonic (some-> mnemonic-token :word)
        {:keys [base bits]} (if raw-mnemonic (split-suffix raw-mnemonic syntax) {:base nil :bits nil})
        visual-ops (vec (remove #(#{:punct :comment :mnemonic :prefix :label-def :directive :size :string} (:kind %))
                                tokens))
        ops (if (= syntax :att) (vec (reverse visual-ops)) visual-ops)]
    {:text line
     :syntax syntax
     :tokens (mapv #(if (= :memory (:kind %))
                      (assoc % :mem (parse-memory (:text %) syntax))
                      %)
                   tokens)
     :mnemonic base
     :raw-mnemonic raw-mnemonic
     :suffix-bits bits
     :label-def (some #(when (= :label-def (:kind %)) (str/lower-case (str/replace (:text %) #":$" ""))) tokens)
     :directive (some #(when (= :directive (:kind %)) (:word %)) tokens)
     :ops (mapv #(if (= :memory (:kind %))
                   (assoc % :mem (parse-memory (:text %) syntax))
                   %)
                ops)}))
