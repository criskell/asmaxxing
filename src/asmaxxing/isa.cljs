(ns asmaxxing.isa
  (:require [clojure.string :as str]
            [asmaxxing.i18n :as i18n]))

(def ^:private classic
  [["rax" ["rax" "eax" "ax" "al" "ah"] [64 32 16 8 8]]
   ["rbx" ["rbx" "ebx" "bx" "bl" "bh"] [64 32 16 8 8]]
   ["rcx" ["rcx" "ecx" "cx" "cl" "ch"] [64 32 16 8 8]]
   ["rdx" ["rdx" "edx" "dx" "dl" "dh"] [64 32 16 8 8]]
   ["rsi" ["rsi" "esi" "si" "sil"] [64 32 16 8]]
   ["rdi" ["rdi" "edi" "di" "dil"] [64 32 16 8]]
   ["rbp" ["rbp" "ebp" "bp" "bpl"] [64 32 16 8]]
   ["rsp" ["rsp" "esp" "sp" "spl"] [64 32 16 8]]])

(def registers
  (let [numbered (for [n (range 8 16)
                       [suffix bits] [["" 64] ["d" 32] ["w" 16] ["b" 8]]]
                   [(str "r" n suffix) {:family (str "r" n) :doc (str "r" n) :bits bits}])
        legacy (for [[family names bits] classic
                     [nm b] (map vector names bits)]
                 [nm {:family family :doc family :bits b :high (str/ends-with? nm "h")}])
        pointers [["rip" {:family "rip" :doc "rip" :bits 64}]
                  ["eip" {:family "rip" :doc "rip" :bits 32}]
                  ["rflags" {:family "rflags" :doc "rflags" :bits 64}]
                  ["eflags" {:family "rflags" :doc "rflags" :bits 32}]]
        vectors (for [n (range 0 32)
                      [prefix bits] [["xmm" 128] ["ymm" 256] ["zmm" 512]]]
                  [(str prefix n) {:family (str "v" n) :doc "vector" :bits bits}])
        segments (for [nm ["cs" "ds" "es" "fs" "gs" "ss"]]
                   [nm {:family nm :doc "segment" :bits 16}])]
    (into {} (concat legacy numbered pointers vectors segments))))

(defn register? [nm] (contains? registers (str/lower-case (str nm))))

(defn register-info [nm]
  (let [k (str/lower-case (str nm))]
    (when-let [base (registers k)]
      (assoc base :name k))))

(def ^:private rex-only #{"sil" "dil" "bpl" "spl"})

(defn needs-rex? [nm]
  (let [k (str/lower-case (str nm))]
    (boolean (or (rex-only k) (re-matches #"r(8|9|1[0-5])[dwb]?" k)))))

(defn register-text [doc field] (i18n/t [:reg doc field]))

(def size-hints {"byte" 8 "word" 16 "dword" 32 "qword" 64 "xmmword" 128 "tbyte" 80})

(def bits->bytes {8 "1 byte" 16 "2 bytes" 32 "4 bytes" 64 "8 bytes" 128 "16 bytes" 80 "10 bytes"})

(def conditions
  ["e" "z" "ne" "nz" "g" "nle" "ge" "nl" "l" "nge" "le" "ng"
   "a" "nbe" "ae" "nb" "b" "nae" "be" "na"
   "s" "ns" "o" "no" "c" "nc" "p" "pe" "np" "po"])

(def arith-flags #{:of :sf :zf :af :pf :cf})
(def logic-flags #{:sf :zf :pf})

(def base-mnemonics
  {"mov" {:roles [:w :r] :flags #{}}
   "movabs" {:roles [:w :r] :flags #{}}
   "movzx" {:roles [:w :r] :flags #{}}
   "movzbl" {:roles [:w :r] :flags #{}}
   "movsx" {:roles [:w :r] :flags #{}}
   "movsxd" {:roles [:w :r] :flags #{}}
   "lea" {:roles [:w :r] :flags #{}}
   "xchg" {:roles [:rw :rw] :flags #{}}
   "add" {:roles [:rw :r] :flags arith-flags}
   "adc" {:roles [:rw :r] :flags arith-flags}
   "sub" {:roles [:rw :r] :flags arith-flags}
   "sbb" {:roles [:rw :r] :flags arith-flags}
   "inc" {:roles [:rw] :flags #{:of :sf :zf :af :pf}}
   "dec" {:roles [:rw] :flags #{:of :sf :zf :af :pf}}
   "neg" {:roles [:rw] :flags arith-flags}
   "imul" {:roles [:rw :r] :flags #{:of :cf}}
   "mul" {:roles [:r] :flags #{:of :cf}}
   "idiv" {:roles [:r] :flags #{}}
   "div" {:roles [:r] :flags #{}}
   "and" {:roles [:rw :r] :flags logic-flags}
   "or" {:roles [:rw :r] :flags logic-flags}
   "xor" {:roles [:rw :r] :flags logic-flags}
   "not" {:roles [:rw] :flags #{}}
   "test" {:roles [:r :r] :flags logic-flags}
   "cmp" {:roles [:r :r] :flags arith-flags}
   "shl" {:roles [:rw :r] :flags arith-flags}
   "sal" {:roles [:rw :r] :flags arith-flags}
   "shr" {:roles [:rw :r] :flags arith-flags}
   "sar" {:roles [:rw :r] :flags arith-flags}
   "rol" {:roles [:rw :r] :flags #{:of :cf}}
   "ror" {:roles [:rw :r] :flags #{:of :cf}}
   "push" {:roles [:r] :flags #{}}
   "pop" {:roles [:w] :flags #{}}
   "call" {:roles [:r] :flags #{}}
   "ret" {:roles [] :flags #{}}
   "leave" {:roles [] :flags #{}}
   "jmp" {:roles [:r] :flags #{}}
   "loop" {:roles [:r] :flags #{}}
   "loope" {:roles [:r] :flags #{}}
   "loopne" {:roles [:r] :flags #{}}
   "nop" {:roles [] :flags #{}}
   "hlt" {:roles [] :flags #{}}
   "ud2" {:roles [] :flags #{}}
   "endbr64" {:roles [] :flags #{}}
   "syscall" {:roles [] :flags #{}}
   "int" {:roles [:r] :flags #{}}
   "cdq" {:roles [] :flags #{}}
   "cltd" {:roles [] :flags #{}}
   "cqo" {:roles [] :flags #{}}
   "cqto" {:roles [] :flags #{}}
   "cdqe" {:roles [] :flags #{}}
   "cltq" {:roles [] :flags #{}}
   "bt" {:roles [:r :r] :flags #{:cf}}
   "bts" {:roles [:rw :r] :flags #{:cf}}
   "btr" {:roles [:rw :r] :flags #{:cf}}
   "btc" {:roles [:rw :r] :flags #{:cf}}
   "bsf" {:roles [:w :r] :flags #{:zf}}
   "bsr" {:roles [:w :r] :flags #{:zf}}
   "popcnt" {:roles [:w :r] :flags #{:zf}}
   "bswap" {:roles [:rw] :flags #{}}
   "shld" {:roles [:rw :r :r] :flags arith-flags}
   "shrd" {:roles [:rw :r :r] :flags arith-flags}
   "xadd" {:roles [:rw :rw] :flags arith-flags}
   "cmpxchg" {:roles [:rw :r] :flags arith-flags}
   "enter" {:roles [:r :r] :flags #{}}
   "pushf" {:roles [] :flags #{}}
   "pushfq" {:roles [] :flags #{} :alias "pushf"}
   "popf" {:roles [] :flags arith-flags}
   "popfq" {:roles [] :flags arith-flags :alias "popf"}
   "movsb" {:roles [] :flags #{}}
   "stosb" {:roles [] :flags #{}}
   "lodsb" {:roles [] :flags #{}}
   "cmpsb" {:roles [] :flags arith-flags}
   "scasb" {:roles [] :flags arith-flags}
   "pause" {:roles [] :flags #{}}
   "cpuid" {:roles [] :flags #{}}
   "rdtsc" {:roles [] :flags #{}}
   "int3" {:roles [] :flags #{}}
   "jrcxz" {:roles [:r] :flags #{}}
   "movsbl" {:roles [:w :r] :flags #{} :alias "movsx"}
   "movsbw" {:roles [:w :r] :flags #{} :alias "movsx"}
   "movsbq" {:roles [:w :r] :flags #{} :alias "movsx"}
   "movswl" {:roles [:w :r] :flags #{} :alias "movsx"}
   "movswq" {:roles [:w :r] :flags #{} :alias "movsx"}
   "movslq" {:roles [:w :r] :flags #{} :alias "movsxd"}
   "movzbw" {:roles [:w :r] :flags #{} :alias "movzx"}
   "movzbq" {:roles [:w :r] :flags #{} :alias "movzx"}
   "movzwl" {:roles [:w :r] :flags #{} :alias "movzx"}
   "movzwq" {:roles [:w :r] :flags #{} :alias "movzx"}
   "movss" {:roles [:w :r] :flags #{} :bits 32}
   "movsd" {:roles [:w :r] :flags #{} :bits 64}
   "movaps" {:roles [:w :r] :flags #{} :bits 128}
   "movups" {:roles [:w :r] :flags #{} :alias "movaps" :bits 128}
   "movapd" {:roles [:w :r] :flags #{} :alias "movaps" :bits 128}
   "movdqa" {:roles [:w :r] :flags #{} :bits 128}
   "movdqu" {:roles [:w :r] :flags #{} :alias "movdqa" :bits 128}
   "movd" {:roles [:w :r] :flags #{} :bits 32}
   "movq" {:roles [:w :r] :flags #{} :alias "movd" :bits 64}
   "addss" {:roles [:rw :r] :flags #{} :bits 32}
   "addsd" {:roles [:rw :r] :flags #{} :bits 64}
   "subss" {:roles [:rw :r] :flags #{} :bits 32}
   "subsd" {:roles [:rw :r] :flags #{} :bits 64}
   "mulss" {:roles [:rw :r] :flags #{} :bits 32}
   "mulsd" {:roles [:rw :r] :flags #{} :bits 64}
   "divss" {:roles [:rw :r] :flags #{} :bits 32}
   "divsd" {:roles [:rw :r] :flags #{} :bits 64}
   "sqrtss" {:roles [:w :r] :flags #{} :bits 32}
   "sqrtsd" {:roles [:w :r] :flags #{} :bits 64}
   "ucomiss" {:roles [:r :r] :flags #{:zf :pf :cf} :bits 32}
   "ucomisd" {:roles [:r :r] :flags #{:zf :pf :cf} :bits 64}
   "comiss" {:roles [:r :r] :flags #{:zf :pf :cf} :alias "ucomiss" :bits 32}
   "comisd" {:roles [:r :r] :flags #{:zf :pf :cf} :alias "ucomisd" :bits 64}
   "cvtsi2ss" {:roles [:w :r] :flags #{}}
   "cvtsi2sd" {:roles [:w :r] :flags #{}}
   "cvttss2si" {:roles [:w :r] :flags #{} :bits 32}
   "cvttsd2si" {:roles [:w :r] :flags #{} :bits 64}
   "cvtss2sd" {:roles [:w :r] :flags #{} :bits 32}
   "cvtsd2ss" {:roles [:w :r] :flags #{} :bits 64}
   "pxor" {:roles [:rw :r] :flags #{} :bits 128}
   "xorps" {:roles [:rw :r] :flags #{} :bits 128}
   "xorpd" {:roles [:rw :r] :flags #{} :alias "xorps"}})

(def mnemonics
  (reduce
   (fn [acc c]
     (assoc acc
            (str "j" c) {:roles [:r] :flags #{} :cond c :cc-kind :j :bits 128}
            (str "set" c) {:roles [:w] :flags #{} :cond c :cc-kind :set}
            (str "cmov" c) {:roles [:w :r] :flags #{} :cond c :cc-kind :cmov}))
   base-mnemonics
   conditions))

(defn known? [nm] (contains? mnemonics (str/lower-case (str nm))))

(defn lookup [nm] (get mnemonics (str/lower-case (str nm))))

(defn- doc-name [nm]
  (let [k (str/lower-case (str nm))]
    (or (:alias (mnemonics k)) k)))

(defn gloss [nm]
  (let [info (lookup nm)]
    (if-let [c (:cond info)]
      (i18n/t [:cc-frame (:cc-kind info) :gloss] [(i18n/t [:cc c :label])])
      (i18n/t [:mn (doc-name nm) :gloss]))))

(defn template [nm]
  (let [info (lookup nm)]
    (if-let [c (:cond info)]
      (let [why (i18n/t [:cc c :why])
            marks (i18n/t [:cc c :flags])]
        (case (:cc-kind info)
          :j (i18n/t [:cc-frame :j :template] [nil why marks])
          :set (i18n/t [:cc-frame :set :template] [nil why])
          :cmov (i18n/t [:cc-frame :cmov :template] [nil nil why])))
      (i18n/t [:mn (doc-name nm) :template]))))

(def address-only #{"lea"})

(def ^:private branch-set #{"jmp" "call" "loop" "loope" "loopne" "jrcxz" "jecxz"})

(defn branch? [nm]
  (let [k (str/lower-case (str nm))
        info (lookup k)]
    (boolean (or (branch-set k) (and (:cond info) (= :j (:cc-kind info)))))))

(def flag-order [:cf :pf :af :zf :sf :of])

(def flag-short {:cf "CF" :pf "PF" :af "AF" :zf "ZF" :sf "SF" :of "OF"})

(def flag-readers
  {:cf ["jb" "jc" "jae" "jnc" "jbe" "ja" "adc" "sbb" "setb"]
   :pf ["jp" "jpe" "jnp" "jpo" "setp"]
   :af ["daa" "das" "aaa" "aas"]
   :zf ["je" "jz" "jne" "jnz" "jbe" "ja" "jle" "jg" "sete" "cmove" "loope"]
   :sf ["js" "jns" "jl" "jge" "jle" "jg" "sets"]
   :of ["jo" "jno" "jl" "jge" "jle" "jg" "seto"]})

(def flag-by-short
  (into {} (map (fn [[k v]] [(str/lower-case v) k]) flag-short)))

(defn flag-text [flag field] (i18n/t [:flag flag field]))

(defn writers-of [flag]
  (->> base-mnemonics
       (filter (fn [[_ v]] (contains? (:flags v) flag)))
       (map key)
       sort
       vec))

(defn directive-doc [d]
  (let [k (str/lower-case (str d))]
    (cond
      (get-in (i18n/dictionaries i18n/fallback) [:dir k]) (i18n/t [:dir k])
      (str/starts-with? k ".cfi_") (i18n/t [:dir :cfi])
      (str/starts-with? k ".loc") (i18n/t [:dir :loc])
      :else (i18n/t [:dir :default]))))
