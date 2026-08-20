(ns asmaxxing.ui
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [asmaxxing.i18n :as i18n]
            [asmaxxing.isa :as isa]
            [asmaxxing.tokens :as tk]
            [asmaxxing.analyze :as an]
            [asmaxxing.machine :as mc]
            [asmaxxing.presets :as presets]))

(defonce app
  (r/atom {:source (:source presets/default-preset)
           :seed (:seed presets/default-preset)
           :preset (:id presets/default-preset)
           :pinned nil
           :revision 0
           :machine-open? true
           :custom? false
           :theme :auto
           :machine nil
           :prev-regs nil}))

(def kind-key
  {:mnemonic :mnemonic :register :register :memory :memory
   :immediate :immediate :label-ref :label :label-def :label
   :size :size :directive :directive :comment :comment :prefix :prefix
   :string :string :flag :flag})

(defn kind-label [kind] (i18n/t [:kind (kind-key kind)]))

(defn kind-card [kind]
  {:title (kind-label kind)
   :kind (i18n/t [:ui :category])
   :tok kind
   :lines [(i18n/t [:kind-doc (kind-key kind)])]})

(defn- tok-class [kind] (str "t-" (name kind)))

(defonce tip (r/atom nil))

(def ^:private tip-stop
  #{"and" "or" "not" "in" "out" "test" "call" "enter" "pause" "loop" "set"
    "sub" "div" "mul" "int" "bt" "do" "no"})

(defn- term-at [word code?]
  (let [k (str/lower-case (str/replace (str word) #"^[%$]" ""))
        upper? (and (>= (count word) 2) (= word (str/upper-case word)))]
    (cond
      code? (cond
              (isa/register? k) {:kind :register :key k}
              (isa/known? k) {:kind :mnemonic :key k}
              :else nil)
      (tip-stop k) nil
      (and upper? (isa/register? k)) {:kind :register :key k}
      (and upper? (isa/flag-by-short k)) {:kind :flag :key (isa/flag-by-short k)}
      (and (not upper?) (>= (count k) 3) (isa/known? k)) {:kind :mnemonic :key k}
      :else nil)))

(defn- show-tip! [e payload]
  (let [r (.getBoundingClientRect (.-currentTarget e))
        w (.-innerWidth js/window)
        x (js/Math.min (- w 170) (js/Math.max 170 (+ (.-left r) (/ (.-width r) 2))))]
    (reset! tip (merge payload {:x x :top (.-top r) :bottom (.-bottom r)
                                :below? (< (.-top r) 190)}))))

(defn kind-ref [kind label]
  [:em {:class "kind-ref"
        :tabIndex 0
        :on-mouse-enter #(show-tip! % {:card (kind-card kind)})
        :on-mouse-leave #(reset! tip nil)
        :on-focus #(show-tip! % {:card (kind-card kind)})
        :on-blur #(reset! tip nil)}
   label])

(defn term-span [word info]
  [:span {:class (str "term t-" (name (:kind info)))
          :tabIndex 0
          :on-mouse-enter #(show-tip! % {:info info})
          :on-mouse-leave #(reset! tip nil)
          :on-focus #(show-tip! % {:info info})
          :on-blur #(reset! tip nil)
          :on-click #(when (= :flag (:kind info))
                       (swap! app assoc :pinned [:flag (:key info)]))}
   word])

(defn linkify
  ([text] (linkify text false))
  ([text code?]
   (if (str/blank? (str text))
     [(str text)]
     (let [re (js/RegExp. "[A-Za-z][A-Za-z0-9]{1,9}" "g")]
       (loop [cursor 0 out []]
         (if-let [m (.exec re text)]
           (let [word (aget m 0)
                 start (.-index m)
                 info (term-at word code?)]
             (if info
               (recur (+ start (count word))
                      (-> out (conj (subs text cursor start)) (conj [term-span word info])))
               (recur cursor out)))
           (conj out (subs text cursor))))))))

(defn tooltip []
  (when-let [{:keys [info card x top bottom below?]} @tip]
    (when-let [card (or card (an/term-card info))]
      [:div.tip {:class (when below? "below")
                 :style {:left (str x "px") :top (str (if below? bottom top) "px")}}
       [:div.tip-head
        [:code {:class (str "t-" (name (or (:kind info) (:tok card))))} (:title card)]
        [:em (:kind card)]
        (when (:sub card) [:span.tip-sub (:sub card)])]
       (doall (for [l (remove str/blank? (:lines card))] ^{:key l} [:p l]))
       (when (= :flag (:kind info))
         (let [on? (boolean (get (:flags (:machine @app)) (:key info)))]
           [:p.tip-state [:b (if on? "1" "0")] " " (isa/flag-text (:key info) (if on? :on :off))]))])))

(defn parse-seed [text]
  (reduce
   (fn [acc raw]
     (let [line (str/trim (str/lower-case raw))]
       (cond
         (str/blank? line) acc
         (re-find #"\[" line)
         (let [[_ size-word addr values] (re-find #"^(byte|word|dword|qword)?\s*\[\s*([^\]]+)\s*\]\s*=\s*(.*)$" line)
               nbytes (quot (get isa/size-hints (or size-word "dword") 32) 8)
               base (mc/bi (:value (an/parse-imm (str/trim addr))))
               nums (remove str/blank? (map str/trim (str/split values #"[,\s]+")))]
           (update acc :mem
                   (fn [m]
                     (first
                      (reduce (fn [[mm off] n]
                                [(mc/write-mem mm (+ base (mc/bi off)) nbytes
                                               (mc/bi (:value (an/parse-imm n))))
                                 (+ off nbytes)])
                              [m 0] nums)))))
         :else
         (let [[_ reg value] (re-find #"^([a-z0-9]+)\s*=\s*(\S+)$" line)]
           (if (and reg (isa/register? reg))
             (update acc :regs mc/write-reg reg (mc/bi (:value (an/parse-imm value))))
             acc)))))
   {:regs {} :mem {}}
   (str/split-lines text)))

(defn analysis [] (an/analyze (:source @app)))

(defn reset-machine! []
  (swap! app assoc
         :machine (mc/fresh-state (parse-seed (:seed @app)))
         :prev-regs nil))

(defn ensure-machine! []
  (when-not (:machine @app) (reset-machine!)))

(defn step! []
  (ensure-machine!)
  (let [{:keys [lines labels]} (analysis)
        st (:machine @app)]
    (when (:running? st)
      (swap! app assoc
             :prev-regs (:regs st)
             :machine (mc/step st (vec lines) labels)))))

(defn run-all! []
  (ensure-machine!)
  (let [{:keys [lines labels]} (analysis)
        st (:machine @app)]
    (swap! app assoc
           :prev-regs (:regs st)
           :machine (mc/run st (vec lines) labels 2000))))

(defn load-preset! [p]
  (swap! app #(-> %
                  (assoc :source (:source p) :seed (:seed p) :preset (:id p) :pinned nil
                         :machine-open? true :custom? false)
                  (update :revision inc)))
  (reset-machine!))

(defn- token-card [t]
  {:title (:text t)
   :kind (kind-label (:kind t))
   :tok (:kind t)
   :lines [(:note t) (:detail t)]})

(defn render-line-tokens
  ([line tokens] (render-line-tokens line tokens nil))
  ([line tokens notes]
   (let [sorted (sort-by :start tokens)]
     (loop [cursor 0 [t & more] sorted out []]
       (if (nil? t)
         (conj out ^{:key "tail"} [:span (subs line cursor)])
         (let [note (get notes (:start t))
               attrs (if note
                       {:class (str (tok-class (:kind t)) " hoverable")
                        :on-mouse-enter #(show-tip! % {:card (token-card note)})
                        :on-mouse-leave #(reset! tip nil)}
                       {:class (tok-class (:kind t))})]
           (recur (:end t) more
                  (-> out
                      (conj ^{:key (str "g" (:start t))} [:span (subs line cursor (:start t))])
                      (conj ^{:key (str "t" (:start t))} [:span attrs (:text t)])))))))))

(defonce ^:private mirror-el (atom nil))

(defn editor []
  (let [source (:source @app)
        revision (:revision @app)
        syntax (tk/detect-syntax source)
        lines (str/split-lines (str source "\n"))]
    [:div.editor
     [:div.gutter
      (doall (for [i (range (count lines))]
        ^{:key i} [:span (inc i)]))]
     [:div.code-area
      [:pre.mirror {:ref #(reset! mirror-el %)}
       (doall (for [[i l] (map-indexed vector lines)]
         (with-meta (into [:div.mline] (render-line-tokens l (tk/classify l syntax))) {:key i})))]
      [:textarea.input
       {:key revision
        :default-value source
        :spellCheck false
        :autoComplete "off"
        :on-scroll #(when-let [m @mirror-el]
                      (set! (.-scrollTop m) (.. % -target -scrollTop))
                      (set! (.-scrollLeft m) (.. % -target -scrollLeft)))
        :on-change #(let [v (.. % -target -value)]
                      (swap! app (fn [a]
                                   (cond-> (assoc a :source v :pinned nil :preset nil)
                                     (not (:custom? a)) (assoc :custom? true :machine-open? false))))
                      (reset-machine!))}]]]))

(defn- token-center [t] (+ (:start t) (/ (count (:text t)) 2)))

(defn callout [line-data]
  (let [annos (vec (sort-by :start (:annotations line-data)))
        n (count annos)
        text (:text line-data)
        label-col (+ 3 (max (count text) 14))]
    [:div.callout
     (into [:div.callout-code]
           (render-line-tokens text (:tokens line-data)
                               (into {} (map (juxt :start identity) annos))))
     [:div.rails
      [:div.rail-dots
       (doall (for [t annos]
                ^{:key (:start t)}
                [:span {:class (str "rail-dot " (tok-class (:kind t)))
                        :style {:left (str (token-center t) "ch")}}]))]
      (doall
       (for [row (range n)]
         (let [j (- n 1 row)
               t (nth annos j)
               cx (token-center t)
               pinned? (= (:pinned @app) [:token (:index line-data) (:start t)])]
           ^{:key (:start t)}
           [:div {:class (str "rail-row " (tok-class (:kind t)) (when pinned? " pinned"))
                  :style {:padding-left (str label-col "ch")}
                  :on-click #(swap! app assoc :pinned
                                    (when-not pinned? [:token (:index line-data) (:start t)]))}
            (doall (for [p (subvec annos 0 j)]
                     ^{:key (:start p)}
                     [:span {:class (str "seg-v through " (tok-class (:kind p)))
                             :style {:left (str (token-center p) "ch")}}]))
            [:span.seg-v.elbow {:style {:left (str cx "ch")}}]
            [:span.seg-h {:style {:left (str cx "ch") :width (str (- label-col cx) "ch")}}]
            [:span.rail-label
             [kind-ref (:kind t) (kind-label (:kind t))]
             (into [:span.rail-note] (linkify (:note t)))]])))]]))

(defn flag-chips [flags]
  [:span.flagset
   (doall (for [f [:zf :sf :cf :of :pf :af]
         :when (contains? flags f)]
     ^{:key f}
     [:span.flag-chip {:on-click #(swap! app assoc :pinned [:flag f])
                       :title (isa/flag-text f :desc)}
      (isa/flag-short f)]))])

(defn line-card [line-data current?]
  (when (seq (:annotations line-data))
    [:article {:class (str "card" (when current? " current"))}
     [:header
      [:span.card-num (inc (:index line-data))]
      (when current? [:span.card-now (i18n/t [:ui :here])])]
     [callout line-data]
     (when (:reading line-data)
       (into [:p.reading] (linkify (:reading line-data))))
     [:div.card-meta
      (when (seq (:flags-written line-data))
        [:span.meta-item [:em (i18n/t [:ui :leaves-set])] [flag-chips (:flags-written line-data)]])
      (when-let [src (:flag-source line-data)]
        [:span.meta-item [:em (i18n/t [:ui :reads-flags])] (into [:code] (linkify (:text src) true))
         [:span.meta-dim (str (i18n/t [:ui :line]) " " (inc (:line src)))]])
      (when-let [lb (:loop-back line-data)]
        [:span.meta-item.loop [:em (i18n/t [:ui :loop])]
         (i18n/t [:ui :loop-back] [(:label lb) (inc (:line lb))])])]]))

(defn token-lens [li start]
  (let [line (get (:lines (analysis)) li)
        t (some #(when (= start (:start %)) %) (:annotations line))]
    (if-not t
      [:div.lens.empty [:p (i18n/t [:ui :lens-gone])]]
      [:div.lens
       [:div.lens-head
        [:code {:class (tok-class (:kind t))} (:text t)]
        [kind-ref (:kind t) (kind-label (:kind t))]]
       (into [:p.lens-note] (linkify (:note t)))
       (into [:p.lens-detail] (linkify (:detail t)))
       (when (= :register (:kind t))
         (let [info (isa/register-info (:bare t))
               fam (:family info)
               names (->> isa/registers
                          (filter #(= fam (:family (val %))))
                          (sort-by #(- (:bits (val %))))
                          (map key))]
           [:div.lens-family
            [:em (i18n/t [:ui :register-family])]
            [:div.family-row
             (doall (for [nm names]
               ^{:key nm}
               [:span.family-chip {:class (when (= nm (:bare t)) "on")}
                [:b (str/upper-case nm)]
                [:i (str (:bits (isa/register-info nm)) " " (i18n/t [:ui :bits]))]]))]]))
       (when (= :memory (:kind t))
         (let [m (:mem t)]
           [:div.lens-family
            [:em (i18n/t [:ui :address-parts])]
            [:div.family-row
             [:span.family-chip [:b (i18n/t [:ui :base])]
              [:i (or (some-> (:base m) str/upper-case) (i18n/t [:ui :none-f]))]]
             [:span.family-chip [:b (i18n/t [:ui :index])]
              [:i (or (some-> (:index m) str/upper-case) (i18n/t [:ui :none-m]))]]
             [:span.family-chip [:b (i18n/t [:ui :scale])] [:i (str (:scale m))]]
             [:span.family-chip [:b (i18n/t [:ui :displacement])] [:i (str (:disp m))]]]]))
       (when-let [info (isa/lookup (:mnemonic line))]
         (when (= :mnemonic (:kind t))
           [:div.lens-family
            [:em (i18n/t [:ui :flags-left])]
            (if (seq (:flags info))
              [:div.family-row
               (doall (for [f isa/flag-order
                     :when (contains? (:flags info) f)]
                 ^{:key f}
                 [:span.family-chip.clickable
                  {:on-click #(swap! app assoc :pinned [:flag f])}
                  [:b (isa/flag-short f)]
                  [:i (isa/flag-text f :desc)]]))]
              [:p.lens-detail (i18n/t [:ui :no-flags])])]))])))

(defn flag-lens [f]
  (let [st (:machine @app)
        on? (boolean (get (:flags st) f))]
    [:div.lens.lens-flag
     [:div.lens-head
      [:code.t-flag (isa/flag-short f)]
      [kind-ref :flag (str (i18n/t [:kind :flag]) " " (isa/flag-text f :name))]]
     (into [:p.lens-note] (linkify (isa/flag-text f :desc)))
     [:div.lens-family
      [:em (i18n/t [:ui :flag-now])]
      [:div.family-row
       [:span.family-chip.flag-state {:class (when on? "on")}
        [:b (if on? "1" "0")]
        [:i (isa/flag-text f (if on? :on :off))]]]]
     [:div.lens-family
      [:em (i18n/t [:ui :flag-writers])]
      [:div.family-row
       (doall (for [m (isa/writers-of f)]
         ^{:key m} [:span.family-chip.mono [:b [term-span m {:kind :mnemonic :key m}]]]))]]
     [:div.lens-family
      [:em (i18n/t [:ui :flag-readers])]
      [:div.family-row
       (doall (for [m (isa/flag-readers f)]
         ^{:key m} [:span.family-chip.mono
                    [:b (if (isa/known? m) [term-span m {:kind :mnemonic :key m}] m)]]))]]]))

(defn lens []
  (let [pinned (:pinned @app)]
    (case (first pinned)
      :token [token-lens (nth pinned 1) (nth pinned 2)]
      :flag [flag-lens (nth pinned 1)]
      [:div.lens.empty [:p (i18n/t [:ui :lens-empty])]])))

(defn hex64 [v]
  (let [s (.toString (bit-and v mc/m64) 16)]
    (str "0x" (str/join (repeat (max 0 (- 16 (count s))) "0")) s)))

(defn registers-panel []
  (let [st (:machine @app)
        prev (:prev-regs @app)
        used (set (mapcat (fn [l] (keep #(when (= :register (:kind %))
                                           (:family (isa/register-info (:bare %))))
                                        (:annotations l)))
                          (:lines (analysis))))
        shown (filter #(or (used %) (not (identical? (get (:regs st) %) mc/zero)))
                      mc/families)]
    [:div.regs
     (if (empty? shown)
       [:p.hint (i18n/t [:ui :no-registers])]
       (doall (for [f (if (empty? shown) [] shown)]
         (let [v (get (:regs st) f mc/zero)
               changed? (and prev (not (identical? (get prev f mc/zero) v)))]
           ^{:key f}
           [:div {:class (str "reg" (when changed? " changed"))}
            [:span.reg-name [term-span (str/upper-case f) {:kind :register :key f}]]
            [:span.reg-hex (hex64 v)]
            [:span.reg-dec (.toString (mc/signed v 64))]]))))]))

(defn memory-panel []
  (let [st (:machine @app)
        cells (->> (:mem st)
                   keys
                   (map #(js/Number %))
                   sort)
        groups (partition-by #(js/Math.floor (/ % 4)) cells)]
    (if (empty? cells)
      [:p.hint (i18n/t [:ui :empty-memory])]
      [:div.mem
       (doall (for [g (take 24 groups)]
         (let [base (first g)
               v (mc/read-mem (:mem st) (mc/bi base) 4)]
           ^{:key base}
           [:div.mem-cell
            [:span.mem-addr (str "0x" (.toString base 16))]
            [:span.mem-val (.toString (mc/signed v 32))]])))])))

(defn machine-panel []
  (let [st (:machine @app)]
    [:section.machine
     [:div.machine-head
      [:h2 (i18n/t [:ui :machine])]
      [:span.step-count (str (:count st) " " (i18n/t [:ui :steps]))]]
     [:div.controls
      [:button.primary {:on-click step! :disabled (not (:running? st))} (i18n/t [:ui :step])]
      [:button {:on-click run-all! :disabled (not (:running? st))} (i18n/t [:ui :run])]
      [:button {:on-click reset-machine!} (i18n/t [:ui :reset])]]
     (when-let [m (:message st)] (into [:p.machine-msg] (linkify (i18n/t (:path m) (:args m)) true)))
     [:h3 (i18n/t [:ui :flags])]
     [:div.flags
      (doall (for [f [:zf :sf :cf :of :pf :af]]
        ^{:key f}
        [:div {:class (str "flag" (when (get (:flags st) f) " on")
                           (when (= (:pinned @app) [:flag f]) " picked"))
               :on-click #(swap! app assoc :pinned [:flag f])
               :title (isa/flag-text f :desc)}
         [:b (isa/flag-short f)]
         [:i (if (get (:flags st) f) "1" "0")]]))]
     [:h3 (i18n/t [:ui :registers])]
     [registers-panel]
     [:h3 (i18n/t [:ui :memory-head])]
     [memory-panel]
     [:h3 (i18n/t [:ui :initial-state])]
     [:textarea.seed
      {:value (:seed @app)
       :spellCheck false
       :on-change #(do (swap! app assoc :seed (.. % -target -value)) (reset-machine!))}]]))

(def legend-items [:mnemonic :register :memory :immediate :label-ref :directive])

(defn apply-theme! [theme]
  (let [el (.-documentElement js/document)]
    (if (= theme :auto)
      (.removeAttribute el "data-theme")
      (.setAttribute el "data-theme" (name theme)))))

(defn cycle-theme! []
  (let [next-theme (case (:theme @app) :auto :light :light :dark :dark)]
    (swap! app assoc :theme next-theme)
    (apply-theme! next-theme)))

(def theme-key {:auto :theme-auto :light :theme-light :dark :theme-dark})

(defn header [syntax]
  [:header.top
   [:div.brand
    [:span.mark "asmaxxing"]
    [:span.tag (i18n/t [:ui :tagline])]
    [:div.legend
     (doall (for [k legend-items]
       ^{:key k} [:span.legend-item {:class (tok-class k)}
                  [:span]
                  [kind-ref k (kind-label k)]]))]]
   [:div.top-right
    [:span {:class (str "syntax-badge " (name syntax))}
     (if (= syntax :att) "AT&T / GAS" "Intel")]
    [:button {:class (str "machine-toggle" (when (:machine-open? @app) " on"))
              :on-click #(swap! app update :machine-open? not)}
     (i18n/t [:ui :machine])]
    [:button.lang {:on-click i18n/toggle!} (if (= @i18n/locale :pt) "EN" "PT")]
    [:button {:on-click cycle-theme!} (i18n/t [:ui (theme-key (:theme @app))])]]])

(defn presets-bar []
  [:nav.presets
   [:em (i18n/t [:ui :examples])]
   (doall (for [p presets/all]
     ^{:key (:id p)}
     [:button {:class (when (= (:id p) (:preset @app)) "on")
               :on-click #(load-preset! p)}
      (i18n/t [:preset (:id p)]) [:i (:syntax p)]]))])

(defn root []
  (ensure-machine!)
  (let [{:keys [syntax lines]} (analysis)
        st (:machine @app)
        current (:ip st)]
    [:div.shell
     [header syntax]
     [presets-bar]
     [:main {:class (when-not (:machine-open? @app) "no-machine")}
      [:section.source
       [:h2 (i18n/t [:ui :code])]
       [editor]
       [:p.hint (i18n/t [:ui :editor-hint])]
       [:h2 (i18n/t [:ui :lens])]
       [lens]]
      [:section.stage
       [:h2 (i18n/t [:ui :breakdown])]
       (let [cards (filter #(seq (:annotations %)) lines)]
         (if (empty? cards)
           [:p.hint (i18n/t [:ui :stage-empty])]
           (doall
            (doall (for [l cards]
              ^{:key (:index l)} [line-card l (= (:index l) current)])))))]
      (when (:machine-open? @app) [machine-panel])]
     [:footer.bottom
      [:span "asmaxxing"]]
     [tooltip]]))
