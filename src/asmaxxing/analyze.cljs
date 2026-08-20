(ns asmaxxing.analyze
  (:require [clojure.string :as str]
            [asmaxxing.i18n :as i18n]
            [asmaxxing.isa :as isa]
            [asmaxxing.tokens :as tk]))

(defn parse-imm [text]
  (let [raw (str/replace (str/lower-case text) #"^\$" "")
        negative? (str/starts-with? raw "-")
        body (str/replace raw #"^-" "")]
    (cond
      (str/starts-with? body "0x")
      {:value (cond-> (js/parseInt (subs body 2) 16) negative? -) :hex? true}
      (str/ends-with? body "h")
      {:value (cond-> (js/parseInt (str/replace body #"h$" "") 16) negative? -) :hex? true}
      :else {:value (cond-> (js/parseInt body 10) negative? -) :hex? false})))

(defn- hex-str [n]
  (if (neg? n) (str "-0x" (.toString (js/Math.abs n) 16)) (str "0x" (.toString n 16))))

(defn imm-phrase [text]
  (let [{:keys [value hex?]} (parse-imm text)]
    (if hex? (str value " (" (str/replace (str/lower-case text) #"^\$" "") ")") (str value))))

(defn line-size [parsed]
  (or (:suffix-bits parsed)
      (some #(when (= :size (:kind %))
               (isa/size-hints (first (str/split (:word %) #"\s+"))))
            (:tokens parsed))
      (:bits (isa/lookup (:mnemonic parsed)))
      (some #(when (= :register (:kind %)) (:bits (isa/register-info (:bare %)))) (:ops parsed))
      64))

(defn- direct? [mem] (and (nil? (:base mem)) (nil? (:index mem))))
(defn- absolute? [mem] (and (direct? mem) (nil? (:symbol mem))))

(defn mem-address-phrase [mem]
  (let [{:keys [base index scale disp]} mem
        plus (i18n/t [:phrase :plus])
        minus (i18n/t [:phrase :minus])
        times (i18n/t [:phrase :times])
        base-part (when base (str/upper-case base))
        index-part (when index
                     (str (when base (str plus " ")) (str/upper-case index)
                          (when (and scale (> scale 1)) (str " " times " " scale))))
        disp-part (when (and disp (not= disp 0))
                    (if (neg? disp)
                      (str minus " " (js/Math.abs disp))
                      (str plus " " disp)))
        sym-part (when-let [sym (:symbol mem)]
                   (if (direct? mem) sym (str plus " " sym)))]
    (cond
      (absolute? mem) (hex-str (or disp 0))
      (direct? mem) (:symbol mem)
      :else (str/join " " (remove nil? [base-part index-part disp-part sym-part])))))

(defn mem-phrase [op bits]
  (let [mem (:mem op)
        args [(isa/bits->bytes bits) (mem-address-phrase mem)]]
    (i18n/t [:phrase (if (direct? mem) :mem-abs :mem-ptr)] args)))

(defn operand-phrase [op bits]
  (case (:kind op)
    :register (str/upper-case (:bare op))
    :immediate (imm-phrase (:text op))
    :memory (mem-phrase op bits)
    :label-ref (i18n/t [:phrase :label] [(:text op)])
    (:text op)))

(defn- op-role [parsed index]
  (get (vec (:roles (isa/lookup (:mnemonic parsed)))) index))

(defn- semantic-index [parsed token]
  (first (keep-indexed #(when (= (:start %2) (:start token)) %1) (:ops parsed))))

(defn describe-mnemonic [parsed]
  (when (isa/lookup (:mnemonic parsed))
    (let [bits (line-size parsed)]
      (-> (isa/template (:mnemonic parsed))
          (str/replace "%1" (fn [_] (or (some-> (get (:ops parsed) 0) (operand-phrase bits))
                                        (i18n/t [:phrase :the-destination]))))
          (str/replace "%2" (fn [_] (or (some-> (get (:ops parsed) 1) (operand-phrase bits))
                                        (i18n/t [:phrase :the-source]))))))))

(def ^:private role-note-key {:rw :role-rw :w :role-w :r :role-r})
(def ^:private role-here-key {:rw :here-rw :w :here-w :r :here-r})
(def ^:private mem-note-key {:rw :note-rw :w :note-w :r :note-r})

(defn- part-key [info]
  (case (:doc info)
    "vector" :part-vector
    "segment" :part-segment
    (case (:bits info)
      64 :part-64
      32 :part-32
      16 :part-16
      8 (if (:high info) :part-8h :part-8)
      :part-64)))

(defn- register-note [parsed token role]
  (let [info (isa/register-info (:bare token))
        nm (str/upper-case (:bare token))
        whole (str/upper-case (:family info))
        frag (if role (i18n/t [:note :register (role-note-key role)]) "")]
    {:note (i18n/t [:note :register :note] [nm (:bits info) frag])
     :detail (str/join
              " "
              (remove nil?
                      [(i18n/t [:note :register (part-key info)] [whole (:bits info)])
                       (i18n/t [:note :register :job]
                               [(isa/register-text (:doc info) :role)
                                (isa/register-text (:doc info) :note)])
                       (when role (i18n/t [:note :register (role-here-key role)]))
                       (when (= 32 (:bits info))
                         (i18n/t [:note :register :zero-extend] [nm whole]))
                       (when (isa/needs-rex? (:bare token))
                         (i18n/t [:note :register :rex]))]))}))

(defn- memory-note [parsed token role size]
  (let [mem (:mem token)
        addr (mem-address-phrase mem)
        where (i18n/t [:note :memory (if (direct? mem) :where-abs :where-ptr)] [addr])
        scale (:scale mem)
        note-key (if (isa/address-only (:mnemonic parsed))
                   :note-addr
                   (get mem-note-key role :note-r))]
    {:note (str (i18n/t [:note :memory note-key] [size where])
                (when-let [seg (:segment mem)]
                  (i18n/t [:note :memory :segment] [(str/upper-case seg)])))
     :detail (str (i18n/t [:note :memory :detail])
                  (when (and (:index mem) (> scale 1))
                    (i18n/t [:note :memory :detail-scale] [scale]))
                  (when (and (:base mem) (neg? (:disp mem)))
                    (i18n/t [:note :memory :detail-local])))}))

(defn- mnemonic-note [parsed]
  (if-let [info (isa/lookup (:mnemonic parsed))]
    {:note (isa/gloss (:mnemonic parsed))
     :detail (str (i18n/t [:note :mnemonic :detail])
                  (when-let [suffix (:suffix-bits parsed)]
                    (i18n/t [:note :mnemonic :detail-suffix] [(isa/bits->bytes suffix)]))
                  (when (>= (count (:ops parsed)) 2)
                    (i18n/t [:note :mnemonic (if (= :att (:syntax parsed)) :detail-att :detail-intel)])))}
    {:note (i18n/t [:note :mnemonic :unknown-note])
     :detail (i18n/t [:note :mnemonic :unknown-detail])}))

(defn token-note [parsed token]
  (let [bits (line-size parsed)
        size (isa/bits->bytes bits)
        role (some->> (semantic-index parsed token) (op-role parsed))]
    (case (:kind token)
      :mnemonic (mnemonic-note parsed)
      :register (register-note parsed token role)
      :memory (memory-note parsed token role size)

      :prefix {:note (i18n/t [:note :prefix :note])
               :detail (i18n/t [:note :prefix :detail])}

      :immediate {:note (i18n/t [:note :immediate :note] [(imm-phrase (:text token))])
                  :detail (i18n/t [:note :immediate :detail])}

      :label-ref
      (let [k (cond
                (and (:directive parsed) (nil? (:mnemonic parsed))) :symbol
                (nil? (isa/lookup (:mnemonic parsed))) :unknown
                (isa/branch? (:mnemonic parsed)) :branch
                :else :symbol-ref)]
        (case k
          :symbol {:note (i18n/t [:note :label-ref :symbol-note])
                   :detail (i18n/t [:note :label-ref :symbol-detail])}
          :unknown {:note (i18n/t [:note :label-ref :unknown-note])
                    :detail (i18n/t [:note :label-ref :unknown-detail])}
          :branch {:note (i18n/t [:note :label-ref :note])
                   :detail (i18n/t [:note :label-ref :detail])}
          {:note (i18n/t [:note :label-ref :symbol-ref-note])
           :detail (i18n/t [:note :label-ref :symbol-ref-detail])}))

      :label-def {:note (i18n/t [:note :label-def :note])
                  :detail (i18n/t [:note :label-def :detail])}

      :size {:note (i18n/t [:note :size :note] [size])
             :detail (i18n/t [:note :size :detail])}

      :directive {:note (isa/directive-doc (:word token))
                  :detail (i18n/t [:note :directive :detail])}

      :string {:note (i18n/t [:note :string :note])
               :detail (i18n/t [:note :string :detail])}

      :comment {:note (i18n/t [:note :comment :note])
                :detail (i18n/t [:note :comment :detail])}

      nil)))

(defn term-card [{:keys [kind key]}]
  (case kind
    :register
    (let [info (isa/register-info key)]
      {:title (str/upper-case key)
       :kind (i18n/t [:kind :register])
       :sub (str (:bits info) " " (i18n/t [:ui :bits]))
       :lines [(i18n/t [:note :register (part-key info)] [(str/upper-case (:family info)) (:bits info)])
               (i18n/t [:note :register :job]
                       [(isa/register-text (:doc info) :role)
                        (isa/register-text (:doc info) :note)])]})
    :flag
    {:title (isa/flag-short key)
     :kind (i18n/t [:kind :flag])
     :sub (isa/flag-text key :name)
     :lines [(isa/flag-text key :desc)]}
    :mnemonic
    {:title key
     :kind (i18n/t [:kind :mnemonic])
     :sub nil
     :lines [(isa/gloss key)]}
    nil))

(defn annotate [parsed]
  (->> (:tokens parsed)
       (keep (fn [t]
               (when-let [n (token-note parsed t)]
                 (merge t n))))
       (remove #(= :punct (:kind %)))
       vec))

(defn- expand-tabs [line]
  (if-not (str/includes? line "\t")
    line
    (loop [chars (seq line) col 0 out []]
      (if-let [c (first chars)]
        (if (= c \tab)
          (let [n (- 4 (mod col 4))]
            (recur (rest chars) (+ col n) (into out (repeat n " "))))
          (recur (rest chars) (inc col) (conj out c)))
        (str/join out)))))

(defn analyze [text]
  (let [syntax (tk/detect-syntax text)
        raw-lines (map expand-tabs (str/split-lines text))
        parsed (map-indexed (fn [i l] (assoc (tk/parse-line l syntax) :index i)) raw-lines)
        labels (into {} (keep #(when (:label-def %) [(:label-def %) (:index %)]) parsed))]
    {:syntax syntax
     :labels labels
     :lines
     (vec
      (map
       (fn [p]
         (let [info (isa/lookup (:mnemonic p))
               target (some #(when (= :label-ref (:kind %)) (:word %)) (:ops p))
               target-line (get labels target)
               back-jump? (and target-line (< target-line (:index p)) (:cond info))
               source (when (:cond info)
                        (last (filter #(and (< (:index %) (:index p))
                                            (seq (:flags (isa/lookup (:mnemonic %)))))
                                      parsed)))]
           (assoc p
                  :annotations (annotate p)
                  :reading (describe-mnemonic p)
                  :flags-written (:flags info)
                  :condition (:cond info)
                  :flag-source (when source
                                 {:line (:index source) :text (str/trim (:text source))})
                  :loop-back (when back-jump? {:label target :line target-line})
                  :blank? (str/blank? (str/trim (:text p))))))
       parsed))}))
