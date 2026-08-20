(ns asmaxxing.i18n.en)

(def messages
 {:ui
  {:tagline "Understand how assembly works, visually"
   :examples "examples"
   :code "code"
   :breakdown "breakdown"
   :lens "lens"
   :machine "machine"
   :steps "steps"
   :step "step"
   :run "run"
   :reset "reset"
   :flags "flags"
   :registers "registers"
   :memory-head "memory, four bytes at a time"
   :initial-state "initial state"
   :here "here"
   :leaves-set "writes these flags"
   :reads-flags "reads the flags from"
   :line "line"
   :loop "loop"
   :loop-back "goes back to %1 on line %2"
   :theme-auto "system theme"
   :theme-light "light theme"
   :theme-dark "dark theme"
   :editor-hint "Write Intel or AT&T."
   :stage-empty "Paste assembly in the editor and every token gets its own arrow."
   :lens-empty "Click an arrow in the breakdown and the whole explanation opens here: which piece of the register it is, how the address decomposes into base, index, scale and displacement, which flags the instruction writes. The flags in the machine open here too, with who writes and who reads each one."
   :lens-gone "That annotation is gone from the code."
   :register-family "the same register, in pieces"
   :address-parts "the parts of the address"
   :flags-left "flags it writes"
   :no-flags "none, the flags from before still stand"
   :flag-now "how it stands right now"
   :flag-writers "instructions that write it"
   :flag-readers "who reads it afterwards"
   :base "base"
   :index "index"
   :scale "scale"
   :displacement "displacement"
   :none-f "none"
   :bits "bits"
   :category "category"
   :none-m "none"
   :no-registers "No register mentioned yet."
   :empty-memory "Memory is empty. Write something like dword [0x1000] = 1, 2, 3 in the initial state."}

  :kind
  {:mnemonic "instruction"
   :register "register"
   :memory "memory"
   :immediate "immediate"
   :label "label"
   :size "size"
   :directive "directive"
   :comment "comment"
   :prefix "prefix"
   :string "text"
   :flag "flag"}

  :kind-doc
  {:mnemonic "The operation the processor carries out. The assembler swaps the mnemonic for an opcode of one or more bytes, and it decides what happens to the operands beside it."
   :register "Storage inside the processor itself, the fastest there is. x86-64 has 16 general purpose ones, and each can be used in pieces of 64, 32, 16 or 8 bits."
   :memory "A value that lives outside the processor and has to be fetched. What sits in brackets is the address arithmetic, not the value, and every trip to memory costs far more than touching a register."
   :immediate "A constant number written inside the instruction itself. It costs no memory access and never changes while the program runs."
   :label "A name given to an address. It becomes no byte in the assembled program: the assembler swaps each use for the matching address or displacement."
   :size "Says how many bytes the memory access moves, and it is only needed when no register on the line gives that width away."
   :directive "A command for the assembler, not for the processor. It reserves space, opens a section or names a constant, and disappears at assembly time."
   :comment "Gone at lexing time, before any program exists. It is there only for whoever reads the text."
   :prefix "A byte placed before the opcode that changes how the instruction runs, such as repeating over a string or locking the memory access."
   :string "Characters written byte by byte. Assembly has no text type: what you are left with is the address of the first byte."
   :flag "A bit the last operation left set or clear, and which conditional branches read to pick their path."}

  :phrase
  {:plus "plus"
   :minus "minus"
   :times "times"
   :mem-abs "the %1 at address %2"
   :mem-ptr "the %1 at the address %2 points to"
   :label "label %1"
   :the-destination "the destination"
   :the-source "the source"}

  :note
  {:mnemonic
   {:unknown-note "a mnemonic this explainer does not know"
    :unknown-detail "The operands beside it are still explained, because register, memory and immediate do not depend on the instruction. It may be a typo, a macro from your assembler, or an extension left out here, such as x87, AVX-512 or system instructions."
    :detail "This is the mnemonic for the operation, always the first word on the line, and the assembler swaps it for an opcode of one or more bytes."
    :detail-suffix " The suffix gives the operand width: it works on %1 at a time."
    :detail-intel " In Intel order the first operand is the destination and the second is the source."
    :detail-att " In AT&T order the operands come the other way round from Intel: the source first and the destination last."}

   :prefix
   {:note "prefix, a byte before the opcode that changes how the instruction runs"
    :detail "It means nothing on its own. rep and repne repeat a string instruction using RCX as the counter, and lock makes the memory access atomic."}

   :register
   {:note "%1, %2 bits%3"
    :role-rw ", the register that feeds the operation and takes its result, destination operand, read and written"
    :role-w ", the register that will receive the value, destination operand, written only"
    :role-r ", the register the value is read from, source operand, read only"
    :part-64 "This is the whole register, all 64 bits of %1."
    :part-32 "These are the low 32 bits of %1, the piece that 32 bit mode saw as the whole register."
    :part-16 "These are the low 16 bits of %1, inherited from the 8086, and reaching them costs a 0x66 prefix in the encoding."
    :part-8h "This is the second byte of %1, the high one of the pair ending in H and L, and it cannot appear in an instruction that carries a REX prefix."
    :part-8 "This is the lowest byte of %1."
    :job "Its job under the System V calling convention is %1: %2."
    :here-rw "In this instruction it is read and written in the same step: the old value goes into the operation and the result overwrites it."
    :here-w "In this instruction it is only written, so the previous contents are discarded without ever being read."
    :here-r "In this instruction it is only read, and it leaves holding the same value it came in with."
    :zero-extend "One catch: writing to %1 clears the top 32 bits of %2 on its own, while writing to the 16 or 8 bit pieces leaves the rest of the register alone."
    :rex "Naming this register takes a REX prefix byte, so the instruction comes out one byte longer."
    :part-vector "This is the %2 bit vector register. XMM, YMM and ZMM are nested slices of the same physical register, and every float or double calculation goes through them."
    :part-segment "This is a segment register, 16 bits. In 64 bit mode most of them are decoration with a base pinned at zero, and only FS and GS still matter because they anchor per thread data."}

   :memory
   {:note-r "memory operand, reads %1 at the %2"
    :note-w "memory operand, writes %1 to the %2"
    :note-rw "memory operand, reads %1 at the %2 and writes the result back to the same spot"
    :note-addr "address operand, and memory is never touched: what matters is the %2"
    :segment ", counted from the %1 segment, which in 64 bit is how per thread data is reached"
    :where-abs "address %1"
    :where-ptr "address held in %1"
    :detail "The brackets mark an access to memory, not a value. What sits inside is the address arithmetic, in the form base plus index times scale plus displacement, and it is encoded in the ModRM and SIB bytes of the instruction. The register holds the address, and the data the instruction uses is whatever is stored at that spot."
    :detail-scale " The scale of %1 comes from the SIB field and is what walks the index one array item at a time, because each item takes %1 bytes."
    :detail-local " A negative displacement from the frame base is the ordinary shape of a local variable, sitting below RBP in the room the function prologue reserved."}

   :immediate
   {:note "immediate operand, the number %1 written into the instruction itself"
    :detail "It sits in the immediate bytes of the encoding, right after the opcode and the ModRM, so it costs no memory access and never changes while the program runs. On x86-64 an immediate reaches 32 bits at most and is sign extended to 64, which is why movabs exists to load a full 64 bit constant."}

   :label-ref
   {:note "branch target operand, where the run goes next"
    :detail "The assembler swaps the name for a displacement relative to the next instruction, rel8 when it fits in one byte and rel32 when it does not. The processor adds that displacement to RIP, which is what lets the code work at whatever address it is loaded."
    :symbol-note "the symbol name the directive takes"
    :symbol-detail "The directive works on this name, usually a function or a piece of data other files will look for at link time."
    :symbol-ref-note "symbol, and the assembler swaps it for the address or value it stands for"
    :symbol-ref-detail "It may be a data label, a constant made with equ, or a name only the linker resolves. The processor never sees the text, only the number left in its place."
    :unknown-note "an identifier this explainer did not recognise"
    :unknown-detail "Since the mnemonic on this line is unknown too, there is no telling whether this is a register, a symbol or a macro. If this is assembly for another architecture, that is expected: only x86-64 is recognised here."}

   :label-def
   {:note "local symbol, names the address of this line"
    :detail "It produces no byte in the assembled program. It goes into the assembler symbol table as the address of this line, and disappears once the branches naming it become displacements."}

   :size
   {:note "size prefix, says the access is %1 wide"
    :detail "Needed when no register on the line gives the width away, as when a constant is written straight to memory. In the encoding this becomes the 0x66 prefix for 16 bits and the W bit of REX for 64."}

   :directive
   {:detail "This is an assembler directive, consumed by the assembler. It has no opcode and never reaches the processor."}

   :string
   {:note "literal text, written byte by byte where it appears"
    :detail "Assembly has no text type. The assembler writes one byte per character and what you are left with is the address of the first one; the length you keep separately or mark with a zero at the end."}

   :comment
   {:note "a comment, the assembler throws it away"
    :detail "It is gone at lexing time, before any program exists. It is there only for whoever reads the text."}}

  :machine
  {:end "end of program"
   :unsupported "%1 is explained above, but the simulator does not run it"
   :no-label "there is no label %1"
   :halted "%1 ended the run"
   :unknown "the simulator does not know %1, so it stopped here"
   :step-limit "stopped at the step limit, the loop may never end"}

  :preset
  {:soma-intel "array sum"
   :soma-gas "array sum, in GAS"
   :fatorial "factorial"
   :maior "larger of two, branchless"
   :quadro "stack frame"
   :nasm "hello in NASM"
   :float "floating point average"
   :bits "bit twiddling"}

  :reg
  {"rax" {:role "accumulator" :note "carries the return value of a function"}
   "rbx" {:role "base" :note "callee saved, whoever touches it puts it back"}
   "rcx" {:role "counter" :note "counts loop turns and shift places, and carries the 4th argument"}
   "rdx" {:role "data" :note "takes the high half of multiply and divide, and carries the 3rd argument"}
   "rsi" {:role "source index" :note "points at where data is read from, and carries the 2nd argument"}
   "rdi" {:role "destination index" :note "points at where data goes, and carries the 1st argument"}
   "rbp" {:role "frame base" :note "anchors the local variables of a function"}
   "rsp" {:role "stack top" :note "points at the last value pushed"}
   "r8" {:role "general purpose" :note "carries the 5th argument"}
   "r9" {:role "general purpose" :note "carries the 6th argument"}
   "r10" {:role "scratch" :note "gone after a call, nobody promises the value"}
   "r11" {:role "scratch" :note "gone after a call, nobody promises the value"}
   "r12" {:role "general purpose" :note "callee saved, a call gives it back untouched"}
   "r13" {:role "general purpose" :note "callee saved, a call gives it back untouched"}
   "r14" {:role "general purpose" :note "callee saved, a call gives it back untouched"}
   "r15" {:role "general purpose" :note "callee saved, a call gives it back untouched"}
   "rip" {:role "instruction pointer" :note "points at the next instruction, nobody writes it by hand"}
   "vector" {:role "vector" :note "holds floating point and SIMD data, and every float or double calculation goes through it"}
   "segment" {:role "segment" :note "a leftover from real mode; in 64 bit only FS and GS still point at anything, usually the thread data area"}
   "rflags" {:role "flags" :note "holds what the last arithmetic left behind, bit by bit"}}

  :flag
  {:cf {:name "carry"
        :desc "the unsigned arithmetic ran past the width of the register"
        :on "the unsigned arithmetic did not fit and left a carry, which in a subtraction means it had to borrow"
        :off "the unsigned arithmetic fit whole, with no carry and no borrow"}
   :pf {:name "parity"
        :desc "records whether the lowest byte of the result has an even number of bits set"
        :on "the lowest byte has an even number of bits set, a leftover from serial line checking"
        :off "the lowest byte has an odd number of bits set"}
   :af {:name "adjust"
        :desc "there was a carry out of the low nibble, bits 0 to 3, into the high one, and only packed decimal arithmetic ever looks at it"
        :on "the sum overflowed the low nibble, carrying from bit 3 into bit 4, and in BCD that nibble is where one decimal digit lives"
        :off "the low nibble, bits 0 to 3, held without carrying into bit 4"}
   :zf {:name "zero"
        :desc "the result came out exactly zero, which is how cmp says two values are equal"
        :on "the last result came out zero, and after a cmp that means the two values were equal"
        :off "the last result was not zero, and after a cmp that means the values differed"}
   :sf {:name "sign"
        :desc "copies the top bit of the result, which is the sign bit when the value is read as signed"
        :on "the top bit of the result is 1, so read as signed the value is negative"
        :off "the top bit is 0, so read as signed the value is positive or zero"}
   :of {:name "overflow"
        :desc "the signed arithmetic left the range that fits, so the sign of the result came out wrong"
        :on "the signed arithmetic overflowed: adding two positives came out negative, or the other way round"
        :off "the signed arithmetic fit, so the sign of the result can be trusted"}}

  :cc-frame
  {:j {:gloss "branches if %1" :template "the run jumps to %1 when %2, that is %3"}
   :set {:gloss "writes 1 if %1" :template "%1 turns into 1 when %2, and 0 otherwise"}
   :cmov {:gloss "copies only if %1" :template "%1 takes %2 only when %3, with no branch at all"}}

  :cc
  {"e" {:label "equal" :why "the comparison came out equal" :flags "ZF=1"}
   "z" {:label "zero" :why "the last result came out zero" :flags "ZF=1"}
   "ne" {:label "not equal" :why "the comparison came out different" :flags "ZF=0"}
   "nz" {:label "not zero" :why "the last result was not zero" :flags "ZF=0"}
   "g" {:label "greater, signed" :why "the first one is greater than the second" :flags "ZF=0 and SF=OF"}
   "nle" {:label "greater, signed" :why "the first one is greater than the second" :flags "ZF=0 and SF=OF"}
   "ge" {:label "greater or equal, signed" :why "the first one is not smaller" :flags "SF=OF"}
   "nl" {:label "greater or equal, signed" :why "the first one is not smaller" :flags "SF=OF"}
   "l" {:label "less, signed" :why "the first one is smaller than the second" :flags "SF≠OF"}
   "nge" {:label "less, signed" :why "the first one is smaller than the second" :flags "SF≠OF"}
   "le" {:label "less or equal, signed" :why "the first one is not greater" :flags "ZF=1 or SF≠OF"}
   "ng" {:label "less or equal, signed" :why "the first one is not greater" :flags "ZF=1 or SF≠OF"}
   "a" {:label "above, unsigned" :why "the first one is greater with everything read as positive" :flags "CF=0 and ZF=0"}
   "nbe" {:label "above, unsigned" :why "the first one is greater with everything read as positive" :flags "CF=0 and ZF=0"}
   "ae" {:label "above or equal, unsigned" :why "the subtraction never had to borrow" :flags "CF=0"}
   "nb" {:label "above or equal, unsigned" :why "the subtraction never had to borrow" :flags "CF=0"}
   "b" {:label "below, unsigned" :why "the subtraction had to borrow" :flags "CF=1"}
   "nae" {:label "below, unsigned" :why "the subtraction had to borrow" :flags "CF=1"}
   "be" {:label "below or equal, unsigned" :why "the first one is not greater, unsigned" :flags "CF=1 or ZF=1"}
   "na" {:label "below or equal, unsigned" :why "the first one is not greater, unsigned" :flags "CF=1 or ZF=1"}
   "s" {:label "negative" :why "the result came out negative" :flags "SF=1"}
   "ns" {:label "not negative" :why "the result did not come out negative" :flags "SF=0"}
   "o" {:label "overflowed" :why "the signed arithmetic did not fit" :flags "OF=1"}
   "no" {:label "did not overflow" :why "the signed arithmetic fit" :flags "OF=0"}
   "c" {:label "carry" :why "the unsigned arithmetic did not fit" :flags "CF=1"}
   "nc" {:label "no carry" :why "the unsigned arithmetic fit" :flags "CF=0"}
   "p" {:label "even parity" :why "the low byte has an even number of bits set" :flags "PF=1"}
   "pe" {:label "even parity" :why "the low byte has an even number of bits set" :flags "PF=1"}
   "np" {:label "odd parity" :why "the low byte has an odd number of bits set" :flags "PF=0"}
   "po" {:label "odd parity" :why "the low byte has an odd number of bits set" :flags "PF=0"}}

  :dir
  {:default "an assembler directive, an instruction for the tool and not for the processor"
   "section" "opens a section, and the name right after says which one"
   "segment" "opens a section, and the name right after says which one"
   "global" "makes the symbol visible to the linker"
   "extern" "says the symbol lives in another file"
   "bits" "tells the assembler which mode to assemble for, 16, 32 or 64"
   "default" "picks the default addressing, relative or absolute"
   "org" "says at which address this chunk starts"
   "align" "pushes the next item to a round address"
   "times" "repeats the instruction or the data as many times as asked"
   "db" "reserves 1 byte for each listed value"
   "dw" "reserves 2 bytes for each listed value"
   "dd" "reserves 4 bytes for each listed value"
   "dq" "reserves 8 bytes for each listed value"
   "dt" "reserves 10 bytes for each listed value"
   "resb" "reserves the number of bytes asked for, with no initial value"
   "resw" "reserves 2 byte words, with no initial value"
   "resd" "reserves 4 byte words, with no initial value"
   "resq" "reserves 8 byte words, with no initial value"
   "equ" "gives a constant a name, resolved at assembly time"
   "struc" "opens the description of a structure"
   "endstruc" "closes the description of a structure"
   :cfi "a note for the debugger stack unwinder, it becomes no instruction at all"
   :loc "marks the source line for the debugger"
   ".text" "opens the executable code section"
   ".data" "opens the section for data that starts out with a value"
   ".bss" "reserves space that starts zeroed and takes no room in the file"
   ".rodata" "opens the read only data section"
   ".section" "picks which section everything below goes into"
   ".globl" "makes the symbol visible to the linker"
   ".global" "makes the symbol visible to the linker"
   ".extern" "says the symbol lives in another file"
   ".long" "reserves 4 bytes for each listed value"
   ".int" "reserves 4 bytes for each listed value"
   ".quad" "reserves 8 bytes for each listed value"
   ".word" "reserves 2 bytes for each listed value"
   ".short" "reserves 2 bytes for each listed value"
   ".byte" "reserves 1 byte for each listed value"
   ".zero" "reserves the number of bytes asked for, all zeroed"
   ".space" "reserves the number of bytes asked for"
   ".ascii" "writes the characters with no terminator"
   ".asciz" "writes the characters with a zero at the end"
   ".string" "writes the characters with a zero at the end"
   ".align" "pushes the next item to a round address"
   ".p2align" "pushes the next item to an address that is a power of two multiple"
   ".type" "tells the linker whether the symbol is a function or data"
   ".size" "records the size of the symbol"
   ".equ" "gives a constant a name"
   ".set" "gives a constant a name"
   ".intel_syntax" "tells the assembler to read the rest as Intel"
   ".att_syntax" "tells the assembler to read the rest as AT&T"}

  :mn
  {"mov" {:gloss "copies the source into the destination" :template "%1 now holds %2"}
   "movabs" {:gloss "copies a full 64 bit constant" :template "%1 now holds %2"}
   "movzx" {:gloss "copies and pads with zeros" :template "%1 takes %2 padded with zeros on the left"}
   "movzbl" {:gloss "copies a byte and pads with zeros" :template "%1 takes %2 padded with zeros on the left"}
   "movsx" {:gloss "copies and repeats the sign bit" :template "%1 takes %2 stretched out with its sign kept"}
   "movsxd" {:gloss "stretches 32 bits to 64 keeping the sign" :template "%1 takes %2 stretched to 64 bits with its sign kept"}
   "lea" {:gloss "works out the address without reading memory" :template "%1 takes the address %2 as a plain number, and memory is never touched"}
   "xchg" {:gloss "swaps the two values" :template "%1 and %2 swap values"}
   "add" {:gloss "adds the source into the destination" :template "%1 now holds %1 plus %2"}
   "adc" {:gloss "adds along with the carry" :template "%1 now holds %1 plus %2 plus the carry from the previous sum"}
   "sub" {:gloss "subtracts the source from the destination" :template "%1 now holds %1 minus %2"}
   "sbb" {:gloss "subtracts along with the borrow" :template "%1 now holds %1 minus %2 minus the borrow from the previous subtraction"}
   "inc" {:gloss "adds 1" :template "%1 goes up by 1, and the carry stays as it was"}
   "dec" {:gloss "subtracts 1" :template "%1 goes down by 1, and the carry stays as it was"}
   "neg" {:gloss "flips the sign" :template "%1 now holds zero minus %1"}
   "imul" {:gloss "multiplies with sign" :template "%1 now holds %1 times %2"}
   "mul" {:gloss "multiplies unsigned through RAX" :template "RAX times %1, low half into RAX and high half into RDX"}
   "idiv" {:gloss "divides signed through RDX:RAX" :template "divides RDX:RAX by %1, quotient into RAX and remainder into RDX"}
   "div" {:gloss "divides unsigned through RDX:RAX" :template "divides RDX:RAX by %1, quotient into RAX and remainder into RDX"}
   "and" {:gloss "keeps only the bits set in both" :template "%1 keeps only the bits that are set in it and in %2"}
   "or" {:gloss "sets the bits present in either one" :template "%1 keeps the bits set in it or in %2"}
   "xor" {:gloss "sets the bits that differ" :template "%1 keeps the bits set in exactly one of the two"}
   "not" {:gloss "flips every bit" :template "every bit of %1 turns into its opposite"}
   "test" {:gloss "compares bits without keeping anything" :template "looks at the bits set in both and only updates the flags"}
   "cmp" {:gloss "compares by subtracting without keeping" :template "works out %1 minus %2 only to update the flags, and throws the result away"}
   "shl" {:gloss "pushes the bits left" :template "%1 moves %2 places left, which doubles the value at each place"}
   "sal" {:gloss "pushes the bits left" :template "%1 moves %2 places left, which doubles the value at each place"}
   "shr" {:gloss "pushes right with zeros coming in" :template "%1 moves %2 places right with zeros coming in, halving the value at each place"}
   "sar" {:gloss "pushes right keeping the sign" :template "%1 moves %2 places right repeating the sign bit, halving the value even when it is negative"}
   "rol" {:gloss "rotates the bits left" :template "the bits of %1 rotate %2 places left, and what leaves one end comes back in the other"}
   "ror" {:gloss "rotates the bits right" :template "the bits of %1 rotate %2 places right, and what leaves one end comes back in the other"}
   "push" {:gloss "pushes the value on the stack" :template "RSP moves down and %1 is kept on top of the stack"}
   "pop" {:gloss "pops the stack into the destination" :template "%1 takes the value on top of the stack and RSP moves up"}
   "call" {:gloss "calls and keeps the way back" :template "pushes the address of the next instruction and branches to %1"}
   "ret" {:gloss "returns to the caller" :template "pops the address that call kept and goes back there"}
   "leave" {:gloss "tears down the stack frame" :template "RSP goes back to RBP and RBP is popped"}
   "jmp" {:gloss "always branches" :template "the run carries on at %1, with no condition at all"}
   "loop" {:gloss "counts RCX down and goes back if any is left" :template "RCX goes down by 1 and the run goes back to %1 while RCX is not zero"}
   "loope" {:gloss "counts RCX down and goes back while any is left and it was equal" :template "RCX goes down by 1 and goes back to %1 while RCX is not zero and ZF=1"}
   "loopne" {:gloss "counts RCX down and goes back while any is left and it differed" :template "RCX goes down by 1 and goes back to %1 while RCX is not zero and ZF=0"}
   "nop" {:gloss "does nothing" :template "spends space and time on purpose, usually to line the code up"}
   "hlt" {:gloss "halts the processor" :template "the processor stops until an interrupt arrives"}
   "ud2" {:gloss "faults on purpose" :template "raises an invalid instruction fault on purpose"}
   "endbr64" {:gloss "marks a legal branch target" :template "marks this spot as able to take an indirect branch, a guard against forged jumps"}
   "syscall" {:gloss "asks the system for a service" :template "calls into the kernel, with the service number in RAX and the arguments in RDI, RSI, RDX"}
   "int" {:gloss "raises an interrupt" :template "raises interrupt %1"}
   "cdq" {:gloss "stretches EAX into EDX:EAX" :template "EDX becomes nothing but sign bits of EAX, the step before idiv"}
   "cltd" {:gloss "stretches EAX into EDX:EAX" :template "EDX becomes nothing but sign bits of EAX, the step before idivl"}
   "cqo" {:gloss "stretches RAX into RDX:RAX" :template "RDX becomes nothing but sign bits of RAX, the step before idiv"}
   "cqto" {:gloss "stretches RAX into RDX:RAX" :template "RDX becomes nothing but sign bits of RAX, the step before idivq"}
   "cdqe" {:gloss "stretches EAX into RAX" :template "RAX takes EAX stretched out with its sign kept"}
   "cltq" {:gloss "stretches EAX into RAX" :template "RAX takes EAX stretched out with its sign kept"}
   "bt" {:gloss "tests a bit" :template "copies bit %2 of %1 into the carry, leaving %1 alone"}
   "bts" {:gloss "tests and sets a bit" :template "copies bit %2 of %1 into the carry and then sets that bit"}
   "btr" {:gloss "tests and clears a bit" :template "copies bit %2 of %1 into the carry and then clears that bit"}
   "btc" {:gloss "tests and flips a bit" :template "copies bit %2 of %1 into the carry and then flips that bit"}
   "bsf" {:gloss "finds the lowest set bit" :template "%1 takes the position of the lowest set bit of %2, and ZF tells you whether %2 was zero"}
   "bsr" {:gloss "finds the highest set bit" :template "%1 takes the position of the highest set bit of %2, and ZF tells you whether %2 was zero"}
   "popcnt" {:gloss "counts the set bits" :template "%1 takes how many bits are set in %2"}
   "bswap" {:gloss "reverses the byte order" :template "the bytes of %1 swap ends, which is how you convert between little endian and big endian"}
   "shld" {:gloss "shifts left pulling bits from a neighbour" :template "%1 moves %3 places left and the gaps are filled with the top bits of %2"}
   "shrd" {:gloss "shifts right pulling bits from a neighbour" :template "%1 moves %3 places right and the gaps are filled with the low bits of %2"}
   "xadd" {:gloss "exchanges and adds" :template "%1 and %2 swap values and then %1 takes the sum of both"}
   "cmpxchg" {:gloss "compares and swaps on a match" :template "compares RAX with %1; if they match %1 takes %2, otherwise RAX takes %1"}
   "enter" {:gloss "builds the stack frame" :template "pushes RBP, points RBP at the top and opens %1 bytes of local room"}
   "pushf" {:gloss "pushes the flags" :template "the whole flags register goes on top of the stack"}
   "popf" {:gloss "pops the flags" :template "the top of the stack becomes the new flags register"}
   "movsb" {:gloss "copies a string byte" :template "copies the byte RSI points at to where RDI points and advances both, and with rep in front it repeats RCX times"}
   "stosb" {:gloss "stores AL into the string" :template "writes AL where RDI points and advances RDI, and with rep in front it repeats RCX times"}
   "lodsb" {:gloss "loads a string byte" :template "AL takes the byte RSI points at and RSI advances"}
   "cmpsb" {:gloss "compares bytes of two strings" :template "compares the byte RSI points at with the one RDI points at, advances both and keeps only the flags"}
   "scasb" {:gloss "scans the string for AL" :template "compares AL with the byte RDI points at, advances RDI and keeps only the flags"}
   "pause" {:gloss "says this is a spin wait" :template "hints to the processor that this is a waiting loop, which saves power and avoids a memory ordering penalty"}
   "cpuid" {:gloss "asks what the processor can do" :template "with the query number in EAX, it answers in EAX, EBX, ECX and EDX with what this processor supports"}
   "rdtsc" {:gloss "reads the cycle counter" :template "EDX:EAX take the count of cycles since the machine powered on"}
   "int3" {:gloss "breakpoint" :template "raises the debug interrupt, the byte a debugger writes over the code to stop the program"}
   "jrcxz" {:gloss "branches when RCX is zero" :template "the run jumps to %1 when RCX is zero, without looking at any flag"}
   "movss" {:gloss "copies a 32 bit float" :template "%1 takes %2 as a 32 bit scalar, and the rest of the vector register stays as it was"}
   "movsd" {:gloss "copies a 64 bit float" :template "%1 takes %2 as a 64 bit scalar, and the rest of the vector register stays as it was"}
   "movaps" {:gloss "copies the whole vector register" :template "%1 takes all 16 bytes of %2 at once, and the aligned form demands an address that is a multiple of 16"}
   "movdqa" {:gloss "copies 16 bytes of integers" :template "%1 takes all 16 bytes of %2 read as integers, and the aligned form demands an address that is a multiple of 16"}
   "movd" {:gloss "crosses between general and vector" :template "%1 takes %2, moving between a general purpose register and a vector register"}
   "addss" {:gloss "adds in 32 bit float" :template "%1 now holds %1 plus %2, in 32 bit floating point"}
   "addsd" {:gloss "adds in 64 bit float" :template "%1 now holds %1 plus %2, in 64 bit floating point"}
   "subss" {:gloss "subtracts in 32 bit float" :template "%1 now holds %1 minus %2, in 32 bit floating point"}
   "subsd" {:gloss "subtracts in 64 bit float" :template "%1 now holds %1 minus %2, in 64 bit floating point"}
   "mulss" {:gloss "multiplies in 32 bit float" :template "%1 now holds %1 times %2, in 32 bit floating point"}
   "mulsd" {:gloss "multiplies in 64 bit float" :template "%1 now holds %1 times %2, in 64 bit floating point"}
   "divss" {:gloss "divides in 32 bit float" :template "%1 now holds %1 divided by %2, in 32 bit floating point"}
   "divsd" {:gloss "divides in 64 bit float" :template "%1 now holds %1 divided by %2, in 64 bit floating point"}
   "sqrtss" {:gloss "square root in 32 bit float" :template "%1 takes the square root of %2, in 32 bit floating point"}
   "sqrtsd" {:gloss "square root in 64 bit float" :template "%1 takes the square root of %2, in 64 bit floating point"}
   "ucomiss" {:gloss "compares 32 bit floats" :template "compares %1 with %2 in 32 bit floating point and only touches the flags, and a NaN sets ZF, PF and CF all at once"}
   "ucomisd" {:gloss "compares 64 bit floats" :template "compares %1 with %2 in 64 bit floating point and only touches the flags, and a NaN sets ZF, PF and CF all at once"}
   "cvtsi2ss" {:gloss "converts an integer to 32 bit float" :template "%1 takes %2 converted from integer to 32 bit floating point"}
   "cvtsi2sd" {:gloss "converts an integer to 64 bit float" :template "%1 takes %2 converted from integer to 64 bit floating point"}
   "cvttss2si" {:gloss "converts a 32 bit float to integer" :template "%1 takes %2 converted to an integer, cutting the fraction off instead of rounding"}
   "cvttsd2si" {:gloss "converts a 64 bit float to integer" :template "%1 takes %2 converted to an integer, cutting the fraction off instead of rounding"}
   "cvtss2sd" {:gloss "widens a float from 32 to 64 bits" :template "%1 takes %2 converted from 32 bit to 64 bit floating point"}
   "cvtsd2ss" {:gloss "narrows a float from 64 to 32 bits" :template "%1 takes %2 converted from 64 bit to 32 bit floating point, and the precision that does not fit is lost"}
   "pxor" {:gloss "XOR over the vector bits" :template "%1 keeps the bits set in exactly one of the two, and pxor of a register with itself is the short way to zero it"}
   "xorps" {:gloss "XOR over packed floats" :template "%1 keeps the bits set in exactly one of the two, and xorps of a register with itself is the short way to zero it"}}})
