(ns asmaxxing.presets)

(def all
  [{:id :soma-intel
    :syntax "Intel"
    :source "        mov eax, 0\nloop:\n        add eax, [rsi]\n        add rsi, 4\n        dec ecx\n        jnz loop"
    :seed "rsi = 0x1000\nrcx = 5\ndword [0x1000] = 10, 20, 30, 40, 50"}
   {:id :soma-gas
    :syntax "AT&T"
    :source "        .text\n        .globl soma\nsoma:\n        movl $0, %eax\n.L1:\n        addl (%rsi), %eax\n        addq $4, %rsi\n        decl %ecx\n        jnz .L1\n        ret"
    :seed "rsi = 0x1000\nrcx = 5\ndword [0x1000] = 10, 20, 30, 40, 50"}
   {:id :fatorial
    :syntax "Intel"
    :source "        mov eax, 1\n        mov ecx, 5\nfat:\n        imul eax, ecx\n        dec ecx\n        jnz fat"
    :seed ""}
   {:id :maior
    :syntax "Intel"
    :source "        mov eax, 17\n        mov ebx, 42\n        cmp eax, ebx\n        cmovl eax, ebx"
    :seed ""}
   {:id :quadro
    :syntax "Intel"
    :source "        push rbp\n        mov rbp, rsp\n        mov dword [rbp-4], 7\n        mov eax, [rbp-4]\n        shl eax, 3\n        pop rbp"
    :seed ""}
   {:id :nasm
    :syntax "NASM"
    :source "        section .data\nmsg:    db \"oi\", 10\nlen:    equ 3\n\n        section .text\n        global _start\n_start:\n        mov rax, 1\n        mov rdi, 1\n        lea rsi, [rel msg]\n        mov rdx, len\n        syscall"
    :seed ""}
   {:id :float
    :syntax "SSE"
    :source "        cvtsi2sd xmm0, edi\n        cvtsi2sd xmm1, esi\n        addsd xmm0, xmm1\n        mulsd xmm0, [rip+meio]\n        cvttsd2si eax, xmm0"
    :seed ""}
   {:id :bits
    :syntax "Intel"
    :source "        mov eax, 0b0\n        popcnt ecx, edx\n        bsf esi, edx\n        bt edx, 7\n        bswap edx\n        shld eax, edx, 4"
    :seed ""}])

(def default-preset (first all))
