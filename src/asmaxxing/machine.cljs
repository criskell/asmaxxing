(ns asmaxxing.machine
  (:require [clojure.string :as str]
            [asmaxxing.isa :as isa]
            [asmaxxing.analyze :as an]))

(defn bi [n] (js/BigInt (if (js/isNaN n) 0 n)))

(def zero (js/BigInt 0))
(def one (js/BigInt 1))
(def eight (js/BigInt 8))
(def byte-mask (js/BigInt 255))

(defn mask [bits] (- (bit-shift-left one (bi bits)) one))

(def m64 (mask 64))

(defn- top-bit? [v bits]
  (identical? one (bit-and (bit-shift-right v (bi (dec bits))) one)))

(defn signed [v bits]
  (if (top-bit? v bits) (- v (bit-shift-left one (bi bits))) v))

(def families
  ["rax" "rbx" "rcx" "rdx" "rsi" "rdi" "rbp" "rsp"
   "r8" "r9" "r10" "r11" "r12" "r13" "r14" "r15"])

(defn read-reg [regs nm]
  (let [{:keys [family bits high]} (isa/register-info nm)
        full (get regs family zero)]
    (if high
      (bit-and (bit-shift-right full eight) byte-mask)
      (bit-and full (mask bits)))))

(defn write-reg [regs nm v]
  (let [{:keys [family bits high]} (isa/register-info nm)
        full (get regs family zero)
        v (bit-and v (mask bits))]
    (assoc regs family
           (cond
             high (bit-or (bit-and full (bit-xor m64 (bit-shift-left byte-mask eight)))
                          (bit-shift-left v eight))
             (>= bits 32) v
             :else (bit-or (bit-and full (bit-xor m64 (mask bits))) v)))))

(defn- addr-key [a] (.toString (bit-and a m64)))

(defn read-mem [mem addr nbytes]
  (loop [i 0 acc zero]
    (if (>= i nbytes)
      acc
      (recur (inc i)
             (bit-or acc (bit-shift-left (bi (get mem (addr-key (+ addr (bi i))) 0))
                                         (bi (* 8 i))))))))

(defn write-mem [mem addr nbytes v]
  (loop [i 0 m mem]
    (if (>= i nbytes)
      m
      (recur (inc i)
             (assoc m (addr-key (+ addr (bi i)))
                    (js/Number (bit-and (bit-shift-right v (bi (* 8 i))) byte-mask)))))))

(defn effective-address [regs parts]
  (let [{:keys [base index scale disp]} parts]
    (bit-and (+ (+ (if base (read-reg regs base) zero)
                   (* (if index (read-reg regs index) zero) (bi (or scale 1))))
                (bi (or disp 0)))
             m64)))

(defn- parity? [v]
  (loop [b (js/Number (bit-and v byte-mask)) n 0]
    (if (zero? b) (even? n) (recur (bit-shift-right b 1) (+ n (bit-and b 1))))))

(defn- result-flags [flags result bits]
  (let [masked (bit-and result (mask bits))]
    (assoc flags
           :zf (identical? masked zero)
           :sf (top-bit? masked bits)
           :pf (parity? masked))))

(defn op-value [st op bits]
  (case (:kind op)
    :register (read-reg (:regs st) (:bare op))
    :immediate (bit-and (bi (:value (an/parse-imm (:text op)))) m64)
    :memory (read-mem (:mem st) (effective-address (:regs st) (:mem op)) (quot bits 8))
    :label-ref zero
    zero))

(defn store [st op bits v]
  (case (:kind op)
    :register (update st :regs write-reg (:bare op) v)
    :memory (assoc st :mem (write-mem (:mem st)
                                      (effective-address (:regs st) (:mem op))
                                      (quot bits 8) v))
    st))

(defn cond-true? [flags c]
  (let [{:keys [zf sf of cf pf]} flags]
    (case c
      ("e" "z") zf
      ("ne" "nz") (not zf)
      ("g" "nle") (and (not zf) (= sf of))
      ("ge" "nl") (= sf of)
      ("l" "nge") (not= sf of)
      ("le" "ng") (or zf (not= sf of))
      ("a" "nbe") (and (not cf) (not zf))
      ("ae" "nb" "nc") (not cf)
      ("b" "nae" "c") cf
      ("be" "na") (or cf zf)
      "s" sf
      "ns" (not sf)
      "o" of
      "no" (not of)
      ("p" "pe") pf
      ("np" "po") (not pf)
      false)))

(def supported
  #{"mov" "movzx" "movsx" "movsxd" "lea" "add" "sub" "adc" "sbb" "inc" "dec" "neg"
    "and" "or" "xor" "not" "cmp" "test" "imul" "shl" "sal" "shr" "sar"
    "push" "pop" "xchg" "jmp" "loop" "nop" "ret" "leave" "syscall" "hlt"
    "cdq" "cltd" "cqo" "cqto" "cdqe" "cltq" "endbr64"})

(defn- supported? [mnemonic]
  (let [info (isa/lookup mnemonic)]
    (or (contains? supported mnemonic)
        (and info (:cond info) (str/starts-with? mnemonic "j"))
        (and info (:cond info) (str/starts-with? mnemonic "set"))
        (and info (:cond info) (str/starts-with? mnemonic "cmov")))))

(defn- push-value [st v]
  (let [rsp (- (read-reg (:regs st) "rsp") eight)]
    (-> st
        (assoc :regs (write-reg (:regs st) "rsp" rsp))
        (as-> s (assoc s :mem (write-mem (:mem s) rsp 8 v))))))

(defn- pop-value [st]
  (let [rsp (read-reg (:regs st) "rsp")
        v (read-mem (:mem st) rsp 8)]
    [(assoc st :regs (write-reg (:regs st) "rsp" (+ rsp eight))) v]))

(defn- arith [st line bits kind]
  (let [ops (:ops line)
        a (op-value st (first ops) bits)
        b (op-value st (second ops) bits)
        m (mask bits)
        raw (case kind
              :add (+ a b)
              :adc (+ (+ a b) (if (:cf (:flags st)) one zero))
              :sub (- a b)
              :sbb (- (- a b) (if (:cf (:flags st)) one zero))
              :cmp (- a b))
        masked (bit-and raw m)
        adding? (#{:add :adc} kind)
        sa (top-bit? a bits)
        sb (top-bit? b bits)
        sr (top-bit? masked bits)
        cf (if adding? (> raw m) (< raw zero))
        of (if adding? (and (= sa sb) (not= sr sa)) (and (not= sa sb) (not= sr sa)))
        flags (-> (result-flags (:flags st) masked bits) (assoc :cf cf :of of))
        st (assoc st :flags flags)]
    (if (= kind :cmp) st (store st (first ops) bits masked))))

(defn- logic [st line bits kind]
  (let [ops (:ops line)
        a (op-value st (first ops) bits)
        b (op-value st (second ops) bits)
        raw (case kind
              :and (bit-and a b)
              :or (bit-or a b)
              :xor (bit-xor a b)
              :test (bit-and a b))
        masked (bit-and raw (mask bits))
        flags (-> (result-flags (:flags st) masked bits) (assoc :cf false :of false))
        st (assoc st :flags flags)]
    (if (= kind :test) st (store st (first ops) bits masked))))

(defn- shift [st line bits kind]
  (let [ops (:ops line)
        a (op-value st (first ops) bits)
        n (bit-and (op-value st (second ops) bits) (bi (if (= bits 64) 63 31)))
        m (mask bits)
        raw (case kind
              :shl (bit-shift-left a n)
              :shr (bit-shift-right a n)
              :sar (bit-shift-right (signed a bits) n))
        masked (bit-and raw m)
        st (assoc st :flags (result-flags (:flags st) masked bits))]
    (store st (first ops) bits masked)))

(defn- jump-to [st labels label]
  (if-let [target (get labels label)]
    (assoc st :ip target)
    (assoc st :running? false :message {:path [:machine :no-label] :args [label]})))

(defn step [st program labels]
  (let [line (get program (:ip st))]
    (cond
      (nil? line) (assoc st :running? false :message {:path [:machine :end]})

      (or (:blank? line) (nil? (:mnemonic line)))
      (recur (update st :ip inc) program labels)

      (not (supported? (:mnemonic line)))
      (assoc st :running? false
             :message {:path [:machine (if (isa/known? (:mnemonic line)) :unsupported :unknown)]
                       :args [(:raw-mnemonic line)]})

      :else
      (let [mnemonic (:mnemonic line)
            info (isa/lookup mnemonic)
            bits (an/line-size line)
            ops (:ops line)
            advance #(update % :ip inc)
            st (assoc st :count (inc (:count st)) :last-line (:ip st))]
        (case mnemonic
          ("mov" "movzx" "movsx" "movsxd")
          (let [src-bits (if (= mnemonic "mov") bits (or (:bits (isa/register-info (:bare (second ops)))) 32))
                v (op-value st (second ops) src-bits)
                v (if (= mnemonic "mov") v
                      (if (str/starts-with? mnemonic "movs")
                        (bit-and (signed v src-bits) m64)
                        v))]
            (advance (store st (first ops) bits v)))

          "lea" (advance (store st (first ops) bits (effective-address (:regs st) (:mem (second ops)))))
          "xchg" (let [a (op-value st (first ops) bits)
                       b (op-value st (second ops) bits)]
                   (advance (-> st (store (first ops) bits b) (store (second ops) bits a))))
          "add" (advance (arith st line bits :add))
          "adc" (advance (arith st line bits :adc))
          "sub" (advance (arith st line bits :sub))
          "sbb" (advance (arith st line bits :sbb))
          "cmp" (advance (arith st line bits :cmp))
          ("inc" "dec")
          (let [a (op-value st (first ops) bits)
                delta (if (= mnemonic "inc") one (- zero one))
                raw (+ a delta)
                masked (bit-and raw (mask bits))
                of (and (not= (top-bit? a bits) (top-bit? masked bits))
                        (= (top-bit? masked bits) (= mnemonic "inc")))
                st (assoc st :flags (assoc (result-flags (:flags st) masked bits) :of of))]
            (advance (store st (first ops) bits masked)))
          "neg" (let [a (op-value st (first ops) bits)
                      masked (bit-and (- zero a) (mask bits))
                      st (assoc st :flags (assoc (result-flags (:flags st) masked bits)
                                                 :cf (not (identical? a zero)) :of false))]
                  (advance (store st (first ops) bits masked)))
          "and" (advance (logic st line bits :and))
          "or" (advance (logic st line bits :or))
          "xor" (advance (logic st line bits :xor))
          "test" (advance (logic st line bits :test))
          "not" (advance (store st (first ops) bits (bit-xor (op-value st (first ops) bits) (mask bits))))
          "imul" (let [a (op-value st (first ops) bits)
                       b (if (second ops) (op-value st (second ops) bits) one)]
                   (advance (store st (first ops) bits (bit-and (* a b) (mask bits)))))
          ("shl" "sal") (advance (shift st line bits :shl))
          "shr" (advance (shift st line bits :shr))
          "sar" (advance (shift st line bits :sar))
          "push" (advance (push-value st (op-value st (first ops) 64)))
          "pop" (let [[st v] (pop-value st)] (advance (store st (first ops) 64 v)))
          ("cdq" "cltd") (advance (assoc st :regs (write-reg (:regs st) "edx"
                                                             (if (top-bit? (read-reg (:regs st) "eax") 32)
                                                               (mask 32) zero))))
          ("cqo" "cqto") (advance (assoc st :regs (write-reg (:regs st) "rdx"
                                                             (if (top-bit? (read-reg (:regs st) "rax") 64)
                                                               m64 zero))))
          ("cdqe" "cltq") (advance (assoc st :regs (write-reg (:regs st) "rax"
                                                              (bit-and (signed (read-reg (:regs st) "eax") 32) m64))))
          ("nop" "endbr64") (advance st)
          "leave" (let [st (assoc st :regs (write-reg (:regs st) "rsp" (read-reg (:regs st) "rbp")))
                        [st v] (pop-value st)]
                    (advance (assoc st :regs (write-reg (:regs st) "rbp" v))))
          ("ret" "syscall" "hlt")
          (assoc st :running? false
                 :message {:path [:machine :halted] :args [(:raw-mnemonic line)]})
          "jmp" (let [t (some #(when (= :label-ref (:kind %)) (:word %)) ops)]
                  (jump-to st labels t))
          "loop" (let [rcx (bit-and (- (read-reg (:regs st) "rcx") one) m64)
                       st (assoc st :regs (write-reg (:regs st) "rcx" rcx))
                       t (some #(when (= :label-ref (:kind %)) (:word %)) ops)]
                   (if (identical? rcx zero) (advance st) (jump-to st labels t)))
          (cond
            (str/starts-with? mnemonic "j")
            (if (cond-true? (:flags st) (:cond info))
              (jump-to st labels (some #(when (= :label-ref (:kind %)) (:word %)) ops))
              (advance st))
            (str/starts-with? mnemonic "set")
            (advance (store st (first ops) 8 (if (cond-true? (:flags st) (:cond info)) one zero)))
            (str/starts-with? mnemonic "cmov")
            (advance (if (cond-true? (:flags st) (:cond info))
                       (store st (first ops) bits (op-value st (second ops) bits))
                       st))
            :else (advance st)))))))

(defn fresh-state [seed]
  {:regs (merge (into {} (map (fn [f] [f zero]) families))
                {"rsp" (js/BigInt "0x7ffffff0") "rbp" (js/BigInt "0x7ffffff0")}
                (:regs seed))
   :flags {:cf false :pf false :af false :zf false :sf false :of false}
   :mem (:mem seed)
   :ip 0
   :count 0
   :last-line nil
   :running? true
   :message nil})

(defn run [st program labels limit]
  (loop [s st n 0]
    (if (or (not (:running? s)) (>= n limit))
      (if (>= n limit) (assoc s :running? false :message {:path [:machine :step-limit]}) s)
      (recur (step s program labels) (inc n)))))
