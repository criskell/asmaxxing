(ns asmaxxing.i18n.pt)

(def messages
 {:ui
  {:tagline "Entenda como o assembly funciona visualmente"
   :examples "exemplos"
   :code "código"
   :breakdown "decomposição"
   :lens "lente"
   :machine "máquina"
   :steps "passos"
   :step "passo"
   :run "rodar"
   :reset "reiniciar"
   :flags "flags"
   :registers "registradores"
   :memory-head "memória, de 4 em 4 bytes"
   :initial-state "estado inicial"
   :here "aqui"
   :leaves-set "escreve nas flags"
   :reads-flags "lê as flags de"
   :line "linha"
   :loop "laço"
   :loop-back "volta para %1 na linha %2"
   :theme-auto "tema do sistema"
   :theme-light "tema claro"
   :theme-dark "tema escuro"
   :editor-hint "Escreve em Intel ou em AT&T."
   :stage-empty "Cole assembly no editor e cada token ganha uma setinha."
   :lens-empty "Clique numa setinha da decomposição e a explicação inteira abre aqui: que pedaço do registrador é aquele, como o endereço se decompõe em base, índice, escala e deslocamento, que flags a instrução escreve. As flags da máquina também abrem, com quem escreve e quem lê cada uma."
   :lens-gone "Essa marcação saiu do código."
   :register-family "o mesmo registrador, por pedaços"
   :address-parts "as partes do endereço"
   :flags-left "flags que ela escreve"
   :no-flags "nenhuma, as flags anteriores continuam valendo"
   :flag-now "como está agora"
   :flag-writers "instruções que escrevem nela"
   :flag-readers "quem lê ela depois"
   :base "base"
   :index "índice"
   :scale "escala"
   :displacement "deslocamento"
   :none-f "nenhuma"
   :bits "bits"
   :category "categoria"
   :none-m "nenhum"
   :no-registers "Nenhum registrador citado ainda."
   :empty-memory "A memória está vazia. Escreva algo como dword [0x1000] = 1, 2, 3 no estado inicial."}

  :kind
  {:mnemonic "instrução"
   :register "registrador"
   :memory "memória"
   :immediate "imediato"
   :label "label"
   :size "tamanho"
   :directive "diretiva"
   :comment "comentário"
   :prefix "prefixo"
   :string "texto"
   :flag "flag"}

  :kind-doc
  {:mnemonic "A operação que o processador executa. O montador troca o mnemônico por um opcode de um ou mais bytes, e é ela que decide o que acontece com os operandos ao lado."
   :register "Um espaço de armazenamento dentro do próprio processador, o mais rápido que existe. x86-64 tem 16 de uso geral, e cada um pode ser usado em pedaços de 64, 32, 16 ou 8 bits."
   :memory "Um valor que mora fora do processador e precisa ser buscado. O que está entre colchetes é a conta do endereço, não o valor, e cada ida à memória custa muito mais que mexer num registrador."
   :immediate "Um número constante escrito dentro da própria instrução. Não custa acesso à memória e não muda enquanto o programa roda."
   :label "Um nome dado a um endereço. Não vira byte nenhum no programa montado: o montador troca cada uso pelo endereço ou pelo deslocamento correspondente."
   :size "Diz quantos bytes o acesso à memória move, e só faz falta quando nenhum registrador da linha revela essa largura."
   :directive "Um comando para o montador, não para o processador. Reserva espaço, abre seção ou dá nome a constante, e some na hora de montar."
   :comment "Some na análise léxica, antes de existir programa. Está ali só para quem lê o texto."
   :prefix "Um byte colocado antes do opcode que muda como a instrução roda, como repetir sobre uma string ou travar o acesso à memória."
   :string "Caracteres gravados byte a byte. Assembly não tem tipo texto: o que sobra é o endereço do primeiro byte."
   :flag "Um bit que a última operação deixou ligado ou desligado, e que os desvios condicionais leem para decidir o caminho."}

  :phrase
  {:plus "mais"
   :minus "menos"
   :times "vezes"
   :mem-abs "o valor de %1 no endereço %2"
   :mem-ptr "o valor de %1 no endereço apontado por %2"
   :label "o label %1"
   :the-destination "o destino"
   :the-source "a origem"}

  :note
  {:mnemonic
   {:unknown-note "mnemônico que este explicador não conhece"
    :unknown-detail "Os operandos ao lado continuam explicados, porque registrador, memória e imediato não dependem da instrução. Pode ser erro de escrita, uma macro do seu montador, ou uma extensão que ficou de fora, como x87, AVX-512 ou instruções de sistema."
    :detail "É o mnemônico da operação, sempre a primeira palavra da linha, e o montador troca ele por um opcode de um ou mais bytes."
    :detail-suffix " O sufixo diz a largura do operando: ela trabalha com %1 por vez."
    :detail-intel " No formato Intel o primeiro operando é o destino e o segundo é a origem."
    :detail-att " No formato AT&T a ordem aparece ao contrário do que se lê em português: a origem vem primeiro e o destino por último."}

   :prefix
   {:note "prefixo, um byte antes do opcode que muda como a instrução roda"
    :detail "Não vale sozinho. rep e repne repetem uma instrução de string usando RCX como contador, e lock garante que o acesso à memória seja atômico."}

   :register
   {:note "%1, %2 bits%3"
    :role-rw ", é o registrador que entra na conta e recebe o resultado dela, operando de destino, lido e escrito"
    :role-w ", é o registrador que vai receber o valor, operando de destino, só escrito"
    :role-r ", é o registrador de onde o valor é lido, operando de origem, só lido"
    :part-64 "É o registrador inteiro, os 64 bits de %1."
    :part-32 "São os 32 bits de baixo de %1, o pedaço que o modo de 32 bits enxergava como registrador inteiro."
    :part-16 "São os 16 bits de baixo de %1, herança do 8086, e alcançá-los custa um prefixo 0x66 na codificação."
    :part-8h "É o segundo byte de %1, o de cima do par que termina em H e L, e ele não pode aparecer numa instrução que use prefixo REX."
    :part-8 "É o byte mais baixo de %1."
    :job "Papel de %1 na convenção de chamada System V: %2."
    :here-rw "Nesta instrução ele é lido e escrito no mesmo passo: o valor antigo entra na conta e o resultado sobrescreve ele."
    :here-w "Nesta instrução ele só é escrito, então o conteúdo anterior é descartado sem ser lido."
    :here-r "Nesta instrução ele só é lido, e sai com o mesmo valor que entrou."
    :zero-extend "Uma pegadinha: escrever em %1 zera sozinho os 32 bits de cima de %2, enquanto escrever nos pedaços de 16 ou 8 bits preserva o resto do registrador."
    :rex "Citar este registrador exige um byte de prefixo REX, então a instrução sai um byte mais longa."
    :part-vector "É o registrador vetorial de %2 bits. XMM, YMM e ZMM são recortes encaixados do mesmo registro físico, e é por eles que passa todo cálculo com float ou double."
    :part-segment "É um registrador de segmento, 16 bits. Em modo de 64 bits quase todos viram enfeite, com base presa em zero, e só FS e GS continuam valendo por servirem de base para dados por thread."}

   :memory
   {:note-r "operando de memória, lê %1 do %2"
    :note-w "operando de memória, grava %1 no %2"
    :note-rw "operando de memória, lê %1 do %2 e grava o resultado de volta no mesmo lugar"
    :note-addr "operando de endereço, e a memória não é tocada: o que interessa é o %2"
    :segment ", contado a partir do segmento %1, que em 64 bits é como se chega aos dados por thread"
    :where-abs "endereço %1"
    :where-ptr "endereço que está em %1"
    :detail "Os colchetes marcam um acesso à memória, não um valor. O que está escrito dentro é a conta do endereço, na forma base mais índice vezes escala mais deslocamento, e ela é codificada nos bytes ModRM e SIB da instrução. O registrador guarda o endereço, e o dado que a instrução usa é o que está gravado naquele lugar."
    :detail-scale " A escala %1 vem do campo SIB e é o que faz o índice andar de item em item num vetor, porque cada item ocupa %1 bytes."
    :detail-local " Deslocamento negativo a partir da base do quadro é o desenho normal de uma variável local, que fica abaixo de RBP no espaço que o prólogo da função reservou."}

   :immediate
   {:note "operando imediato, o número %1 gravado na própria instrução"
    :detail "Fica nos bytes de imediato da codificação, logo depois do opcode e do ModRM, então não custa acesso à memória e não muda enquanto o programa roda. Em x86-64 o imediato vai no máximo a 32 bits e é esticado com sinal para 64, e é por isso que existe movabs para carregar uma constante de 64 bits inteira."}

   :label-ref
   {:note "operando de destino do desvio, para onde a execução vai"
    :detail "O montador troca o nome por um deslocamento relativo à instrução seguinte, rel8 quando cabe em um byte e rel32 quando não cabe. O processador soma esse deslocamento a RIP, e é isso que deixa o código funcionar em qualquer endereço onde for carregado."
    :symbol-note "nome do símbolo que a diretiva recebe"
    :symbol-detail "A diretiva acima trabalha sobre este nome, que costuma ser uma função ou um dado que outros arquivos vão procurar na ligação."
    :symbol-ref-note "símbolo, e o montador troca pelo endereço ou valor que ele representa"
    :symbol-ref-detail "Pode ser um rótulo de dado, uma constante criada com equ, ou um nome que só o ligador vai resolver. O processador nunca vê o texto, só o número que sobrou no lugar."
    :unknown-note "identificador que este explicador não reconheceu"
    :unknown-detail "Como o mnemônico desta linha também é desconhecido, não dá para dizer se isto é um registrador, um símbolo ou uma macro. Se for assembly de outra arquitetura, é esperado: aqui só x86-64 é reconhecido."}

   :label-def
   {:note "símbolo local, dá nome ao endereço desta linha"
    :detail "Não gera byte nenhum no programa montado. Entra na tabela de símbolos do montador como o endereço desta linha, e some depois que os desvios que citam ele viram deslocamento."}

   :size
   {:note "prefixo de tamanho, diz que o acesso é de %1"
    :detail "Necessário quando nenhum registrador da linha revela a largura, como ao gravar uma constante direto na memória. Na codificação isso vira o prefixo 0x66 para 16 bits e o bit W do REX para 64."}

   :directive
   {:detail "É uma diretiva de montagem, consumida pelo montador. Não tem opcode e não chega ao processador."}

   :string
   {:note "texto literal, gravado byte a byte no lugar onde ele aparece"
    :detail "Assembly não tem tipo texto. O montador grava um byte por caractere e o que sobra é o endereço do primeiro; o tamanho você guarda à parte ou marca com um zero no fim."}

   :comment
   {:note "comentário, o montador joga fora"
    :detail "Some na análise léxica, antes de virar programa. Existe só para quem lê o texto."}}

  :machine
  {:end "fim do programa"
   :unsupported "%1 está explicada acima, mas o simulador não executa ela"
   :no-label "não existe o label %1"
   :halted "%1 encerrou a execução"
   :unknown "o simulador não conhece %1, então parou aqui"
   :step-limit "parou no limite de passos, o laço pode não terminar"}

  :preset
  {:soma-intel "soma de vetor"
   :soma-gas "soma de vetor, em GAS"
   :fatorial "fatorial"
   :maior "maior dos dois, sem desvio"
   :quadro "quadro de pilha"
   :nasm "hello em NASM"
   :float "média em ponto flutuante"
   :bits "mexendo em bits"}

  :reg
  {"rax" {:role "acumulador" :note "leva o valor de retorno da função"}
   "rbx" {:role "base" :note "preservado entre chamadas, quem mexe devolve como estava"}
   "rcx" {:role "contador" :note "conta voltas de laço e casas de deslocamento, e leva o 4o argumento"}
   "rdx" {:role "dados" :note "recebe a parte alta de multiplicação e divisão, e leva o 3o argumento"}
   "rsi" {:role "índice de origem" :note "aponta de onde os dados são lidos, e leva o 2o argumento"}
   "rdi" {:role "índice de destino" :note "aponta para onde os dados vão, e leva o 1o argumento"}
   "rbp" {:role "base do quadro" :note "ancora as variáveis locais da função"}
   "rsp" {:role "topo da pilha" :note "aponta para o último valor empilhado"}
   "r8" {:role "uso geral" :note "leva o 5o argumento"}
   "r9" {:role "uso geral" :note "leva o 6o argumento"}
   "r10" {:role "rascunho" :note "some depois de uma chamada, ninguém garante o valor"}
   "r11" {:role "rascunho" :note "some depois de uma chamada, ninguém garante o valor"}
   "r12" {:role "uso geral" :note "preservado entre chamadas"}
   "r13" {:role "uso geral" :note "preservado entre chamadas"}
   "r14" {:role "uso geral" :note "preservado entre chamadas"}
   "r15" {:role "uso geral" :note "preservado entre chamadas"}
   "rip" {:role "ponteiro de instrução" :note "aponta para a próxima instrução, ninguém escreve nele à mão"}
   "vector" {:role "vetorial" :note "guarda ponto flutuante e dados SIMD, e é por onde passa todo cálculo com float ou double"}
   "segment" {:role "segmento" :note "sobra do modo real; em 64 bits só FS e GS ainda apontam para algo, normalmente a área de dados da thread"}
   "rflags" {:role "flags" :note "guarda o resultado da última conta, bit a bit"}}

  :flag
  {:cf {:name "carry"
        :desc "a conta sem sinal passou do tamanho do registrador"
        :on "a conta sem sinal não coube e sobrou carry, que numa subtração quer dizer que ela precisou de borrow"
        :off "a conta sem sinal coube inteira, sem carry e sem borrow"}
   :pf {:name "parity"
        :desc "conta se o byte mais baixo do resultado tem um número par de bits ligados"
        :on "o byte mais baixo tem um número par de bits ligados, coisa herdada de conferência de linha serial"
        :off "o byte mais baixo tem um número ímpar de bits ligados"}
   :af {:name "adjust"
        :desc "houve carry do nibble de baixo, os bits 0 a 3, para o de cima, e só a aritmética decimal empacotada olha para isso"
        :on "a conta transbordou o nibble de baixo, passando do bit 3 para o bit 4, e em BCD é nesse nibble que mora um dígito decimal"
        :off "o nibble de baixo, os bits 0 a 3, coube sem transbordar para o bit 4"}
   :zf {:name "zero"
        :desc "o resultado deu exatamente zero, que é como cmp diz que dois valores são iguais"
        :on "o último resultado deu zero, e depois de um cmp isso quer dizer que os dois valores eram iguais"
        :off "o último resultado não deu zero, e depois de um cmp isso quer dizer que os valores diferiam"}
   :sf {:name "sign"
        :desc "copia o bit mais alto do resultado, que é o bit de sinal na leitura com sinal"
        :on "o bit mais alto do resultado é 1, então lido com sinal o valor é negativo"
        :off "o bit mais alto é 0, então lido com sinal o valor é positivo ou zero"}
   :of {:name "overflow"
        :desc "a conta com sinal saiu da faixa que cabe, então o sinal do resultado saiu errado"
        :on "houve overflow com sinal: somar dois positivos deu negativo, ou o contrário"
        :off "a conta com sinal coube, o sinal do resultado é confiável"}}

  :cc-frame
  {:j {:gloss "desvia se %1" :template "a execução pula para %1 quando %2, ou seja %3"}
   :set {:gloss "escreve 1 se %1" :template "%1 vira 1 quando %2, e 0 caso contrário"}
   :cmov {:gloss "copia só se %1" :template "%1 recebe %2 apenas quando %3, sem desvio nenhum"}}

  :cc
  {"e" {:label "igual" :why "a comparação deu igual" :flags "ZF=1"}
   "z" {:label "zero" :why "o último resultado deu zero" :flags "ZF=1"}
   "ne" {:label "diferente" :why "a comparação deu diferente" :flags "ZF=0"}
   "nz" {:label "não zero" :why "o último resultado não deu zero" :flags "ZF=0"}
   "g" {:label "maior, com sinal" :why "o primeiro é maior que o segundo" :flags "ZF=0 e SF=OF"}
   "nle" {:label "maior, com sinal" :why "o primeiro é maior que o segundo" :flags "ZF=0 e SF=OF"}
   "ge" {:label "maior ou igual, com sinal" :why "o primeiro não é menor" :flags "SF=OF"}
   "nl" {:label "maior ou igual, com sinal" :why "o primeiro não é menor" :flags "SF=OF"}
   "l" {:label "menor, com sinal" :why "o primeiro é menor que o segundo" :flags "SF≠OF"}
   "nge" {:label "menor, com sinal" :why "o primeiro é menor que o segundo" :flags "SF≠OF"}
   "le" {:label "menor ou igual, com sinal" :why "o primeiro não é maior" :flags "ZF=1 ou SF≠OF"}
   "ng" {:label "menor ou igual, com sinal" :why "o primeiro não é maior" :flags "ZF=1 ou SF≠OF"}
   "a" {:label "acima, sem sinal" :why "o primeiro é maior tratando tudo como positivo" :flags "CF=0 e ZF=0"}
   "nbe" {:label "acima, sem sinal" :why "o primeiro é maior tratando tudo como positivo" :flags "CF=0 e ZF=0"}
   "ae" {:label "acima ou igual, sem sinal" :why "a subtração não precisou de borrow" :flags "CF=0"}
   "nb" {:label "acima ou igual, sem sinal" :why "a subtração não precisou de borrow" :flags "CF=0"}
   "b" {:label "abaixo, sem sinal" :why "a subtração precisou de borrow" :flags "CF=1"}
   "nae" {:label "abaixo, sem sinal" :why "a subtração precisou de borrow" :flags "CF=1"}
   "be" {:label "abaixo ou igual, sem sinal" :why "o primeiro não é maior, sem sinal" :flags "CF=1 ou ZF=1"}
   "na" {:label "abaixo ou igual, sem sinal" :why "o primeiro não é maior, sem sinal" :flags "CF=1 ou ZF=1"}
   "s" {:label "negativo" :why "o resultado ficou negativo" :flags "SF=1"}
   "ns" {:label "não negativo" :why "o resultado não ficou negativo" :flags "SF=0"}
   "o" {:label "overflow" :why "a conta com sinal não coube" :flags "OF=1"}
   "no" {:label "sem overflow" :why "a conta com sinal coube" :flags "OF=0"}
   "c" {:label "carry" :why "a conta sem sinal não coube" :flags "CF=1"}
   "nc" {:label "sem carry" :why "a conta sem sinal coube" :flags "CF=0"}
   "p" {:label "paridade par" :why "o byte baixo tem um número par de bits ligados" :flags "PF=1"}
   "pe" {:label "paridade par" :why "o byte baixo tem um número par de bits ligados" :flags "PF=1"}
   "np" {:label "paridade ímpar" :why "o byte baixo tem um número ímpar de bits ligados" :flags "PF=0"}
   "po" {:label "paridade ímpar" :why "o byte baixo tem um número ímpar de bits ligados" :flags "PF=0"}}

  :dir
  {:default "diretiva do montador, uma instrução para a ferramenta e não para o processador"
   "section" "abre uma seção, e o nome logo depois diz qual"
   "segment" "abre uma seção, e o nome logo depois diz qual"
   "global" "deixa o símbolo visível para o ligador"
   "extern" "avisa que o símbolo mora em outro arquivo"
   "bits" "diz ao montador em que modo montar, 16, 32 ou 64"
   "default" "escolhe o endereçamento padrão, relativo ou absoluto"
   "org" "diz em que endereço este trecho vai começar"
   "align" "empurra o próximo item para um endereço redondo"
   "times" "repete a instrução ou o dado a quantidade de vezes pedida"
   "db" "reserva 1 byte para cada valor da lista"
   "dw" "reserva 2 bytes para cada valor da lista"
   "dd" "reserva 4 bytes para cada valor da lista"
   "dq" "reserva 8 bytes para cada valor da lista"
   "dt" "reserva 10 bytes para cada valor da lista"
   "resb" "reserva a quantidade de bytes pedida, sem valor inicial"
   "resw" "reserva palavras de 2 bytes, sem valor inicial"
   "resd" "reserva palavras de 4 bytes, sem valor inicial"
   "resq" "reserva palavras de 8 bytes, sem valor inicial"
   "equ" "dá um nome a uma constante, resolvida na hora de montar"
   "struc" "abre a descrição de uma estrutura"
   "endstruc" "fecha a descrição de uma estrutura"
   :cfi "anotação para o desenrolar de pilha do depurador, não vira instrução nenhuma"
   :loc "marca a linha do código fonte para o depurador"
   ".text" "abre o trecho de código executável"
   ".data" "abre o trecho de dados que já nascem com valor"
   ".bss" "reserva espaço que começa zerado e não ocupa o arquivo"
   ".rodata" "abre o trecho de dados só de leitura"
   ".section" "escolhe em qual seção entra o que vem abaixo"
   ".globl" "deixa o símbolo visível para o ligador"
   ".global" "deixa o símbolo visível para o ligador"
   ".extern" "avisa que o símbolo mora em outro arquivo"
   ".long" "reserva 4 bytes para cada valor da lista"
   ".int" "reserva 4 bytes para cada valor da lista"
   ".quad" "reserva 8 bytes para cada valor da lista"
   ".word" "reserva 2 bytes para cada valor da lista"
   ".short" "reserva 2 bytes para cada valor da lista"
   ".byte" "reserva 1 byte para cada valor da lista"
   ".zero" "reserva a quantidade de bytes pedida, toda zerada"
   ".space" "reserva a quantidade de bytes pedida"
   ".ascii" "grava os caracteres sem terminador"
   ".asciz" "grava os caracteres com um zero no fim"
   ".string" "grava os caracteres com um zero no fim"
   ".align" "empurra o próximo item para um endereço redondo"
   ".p2align" "empurra o próximo item para um endereço múltiplo de uma potência de dois"
   ".type" "diz ao ligador se o símbolo é função ou dado"
   ".size" "registra o tamanho do símbolo"
   ".equ" "dá um nome a uma constante"
   ".set" "dá um nome a uma constante"
   ".intel_syntax" "manda o montador ler o resto no formato Intel"
   ".att_syntax" "manda o montador ler o resto no formato AT&T"}

  :mn
  {"mov" {:gloss "copia a origem para o destino" :template "%1 passa a valer %2"}
   "movabs" {:gloss "copia uma constante de 64 bits" :template "%1 passa a valer %2"}
   "movzx" {:gloss "copia esticando com zeros" :template "%1 recebe %2 completado com zeros à esquerda"}
   "movzbl" {:gloss "copia um byte esticando com zeros" :template "%1 recebe %2 completado com zeros à esquerda"}
   "movsx" {:gloss "copia repetindo o bit de sinal" :template "%1 recebe %2 esticado mantendo o sinal"}
   "movsxd" {:gloss "estica 32 bits para 64 mantendo o sinal" :template "%1 recebe %2 esticado para 64 bits mantendo o sinal"}
   "lea" {:gloss "calcula o endereço sem ler a memória" :template "%1 recebe o endereço %2 como número, e a memória nem é tocada"}
   "xchg" {:gloss "troca os dois valores de lugar" :template "%1 e %2 trocam de valor"}
   "add" {:gloss "soma a origem no destino" :template "%1 passa a valer %1 mais %2"}
   "adc" {:gloss "soma junto com o carry" :template "%1 passa a valer %1 mais %2 mais o carry da conta anterior"}
   "sub" {:gloss "subtrai a origem do destino" :template "%1 passa a valer %1 menos %2"}
   "sbb" {:gloss "subtrai junto com o borrow" :template "%1 passa a valer %1 menos %2 menos o borrow da conta anterior"}
   "inc" {:gloss "soma 1" :template "%1 aumenta 1, e o carry fica como estava"}
   "dec" {:gloss "subtrai 1" :template "%1 diminui 1, e o carry fica como estava"}
   "neg" {:gloss "troca o sinal" :template "%1 passa a valer zero menos %1"}
   "imul" {:gloss "multiplica com sinal" :template "%1 passa a valer %1 vezes %2"}
   "mul" {:gloss "multiplica sem sinal usando RAX" :template "RAX vezes %1, com a parte baixa em RAX e a alta em RDX"}
   "idiv" {:gloss "divide com sinal usando RDX:RAX" :template "divide RDX:RAX por %1, o quociente vai para RAX e o resto para RDX"}
   "div" {:gloss "divide sem sinal usando RDX:RAX" :template "divide RDX:RAX por %1, o quociente vai para RAX e o resto para RDX"}
   "and" {:gloss "mantém só os bits ligados nos dois" :template "%1 fica só com os bits que estão ligados nele e em %2"}
   "or" {:gloss "liga os bits que estão em qualquer um" :template "%1 fica com os bits ligados nele ou em %2"}
   "xor" {:gloss "liga os bits que diferem" :template "%1 fica com os bits que estão ligados em só um dos dois"}
   "not" {:gloss "inverte todos os bits" :template "cada bit de %1 vira o oposto"}
   "test" {:gloss "compara bits sem guardar nada" :template "olha os bits ligados nos dois e só atualiza as flags"}
   "cmp" {:gloss "compara subtraindo sem guardar" :template "calcula %1 menos %2 só para atualizar as flags, e joga o resultado fora"}
   "shl" {:gloss "empurra os bits para a esquerda" :template "%1 anda %2 casas para a esquerda, o que dobra o valor a cada casa"}
   "sal" {:gloss "empurra os bits para a esquerda" :template "%1 anda %2 casas para a esquerda, o que dobra o valor a cada casa"}
   "shr" {:gloss "empurra para a direita entrando zeros" :template "%1 anda %2 casas para a direita, entrando zeros, o que divide por dois a cada casa"}
   "sar" {:gloss "empurra para a direita mantendo o sinal" :template "%1 anda %2 casas para a direita repetindo o bit de sinal, o que divide por dois mesmo se for negativo"}
   "rol" {:gloss "gira os bits para a esquerda" :template "os bits de %1 giram %2 casas para a esquerda, e o que sai de um lado entra no outro"}
   "ror" {:gloss "gira os bits para a direita" :template "os bits de %1 giram %2 casas para a direita, e o que sai de um lado entra no outro"}
   "push" {:gloss "empilha o valor" :template "RSP desce e %1 fica guardado no topo da pilha"}
   "pop" {:gloss "desempilha para o destino" :template "%1 recebe o valor do topo da pilha e RSP sobe"}
   "call" {:gloss "chama e guarda o endereço de volta" :template "empilha o endereço da próxima instrução e desvia para %1"}
   "ret" {:gloss "volta para quem chamou" :template "desempilha o endereço guardado por call e volta para lá"}
   "leave" {:gloss "desmonta o quadro da função" :template "RSP volta para RBP e RBP é desempilhado"}
   "jmp" {:gloss "desvia sempre" :template "a execução continua em %1, sem condição nenhuma"}
   "loop" {:gloss "desconta RCX e volta se sobrou" :template "RCX diminui 1 e a execução volta para %1 enquanto RCX não for zero"}
   "loope" {:gloss "desconta RCX e volta se sobrou e deu igual" :template "RCX diminui 1 e volta para %1 enquanto RCX não zerar e ZF=1"}
   "loopne" {:gloss "desconta RCX e volta se sobrou e deu diferente" :template "RCX diminui 1 e volta para %1 enquanto RCX não zerar e ZF=0"}
   "nop" {:gloss "não faz nada" :template "gasta espaço e tempo de propósito, normalmente para alinhar o código"}
   "hlt" {:gloss "para o processador" :template "o processador para até chegar uma interrupção"}
   "ud2" {:gloss "quebra de propósito" :template "dispara uma falha de instrução inválida de propósito"}
   "endbr64" {:gloss "marca um destino de desvio válido" :template "marca que este ponto pode receber um desvio indireto, uma proteção contra desvio forjado"}
   "syscall" {:gloss "pede um serviço ao sistema" :template "chama o núcleo do sistema, com o número do serviço em RAX e os argumentos em RDI, RSI, RDX"}
   "int" {:gloss "dispara uma interrupção" :template "dispara a interrupção %1"}
   "cdq" {:gloss "estica EAX para EDX:EAX" :template "EDX vira só bits de sinal de EAX, o passo antes de idiv"}
   "cltd" {:gloss "estica EAX para EDX:EAX" :template "EDX vira só bits de sinal de EAX, o passo antes de idivl"}
   "cqo" {:gloss "estica RAX para RDX:RAX" :template "RDX vira só bits de sinal de RAX, o passo antes de idiv"}
   "cqto" {:gloss "estica RAX para RDX:RAX" :template "RDX vira só bits de sinal de RAX, o passo antes de idivq"}
   "cdqe" {:gloss "estica EAX para RAX" :template "RAX recebe EAX esticado mantendo o sinal"}
   "cltq" {:gloss "estica EAX para RAX" :template "RAX recebe EAX esticado mantendo o sinal"}
   "bt" {:gloss "testa um bit" :template "copia o bit %2 de %1 para o carry, sem mudar %1"}
   "bts" {:gloss "testa e liga um bit" :template "copia o bit %2 de %1 para o carry e depois liga esse bit"}
   "btr" {:gloss "testa e desliga um bit" :template "copia o bit %2 de %1 para o carry e depois desliga esse bit"}
   "btc" {:gloss "testa e inverte um bit" :template "copia o bit %2 de %1 para o carry e depois inverte esse bit"}
   "bsf" {:gloss "acha o bit ligado mais baixo" :template "%1 recebe a posição do bit ligado mais baixo de %2, e ZF avisa se %2 era zero"}
   "bsr" {:gloss "acha o bit ligado mais alto" :template "%1 recebe a posição do bit ligado mais alto de %2, e ZF avisa se %2 era zero"}
   "popcnt" {:gloss "conta os bits ligados" :template "%1 recebe quantos bits estão ligados em %2"}
   "bswap" {:gloss "inverte a ordem dos bytes" :template "os bytes de %1 trocam de ponta, que é como se converte entre little endian e big endian"}
   "shld" {:gloss "desloca à esquerda puxando bits do vizinho" :template "%1 anda %3 casas para a esquerda e as vagas são preenchidas com os bits de cima de %2"}
   "shrd" {:gloss "desloca à direita puxando bits do vizinho" :template "%1 anda %3 casas para a direita e as vagas são preenchidas com os bits de baixo de %2"}
   "xadd" {:gloss "troca e soma" :template "%1 e %2 trocam de valor e em seguida %1 recebe a soma dos dois"}
   "cmpxchg" {:gloss "compara e troca se bater" :template "compara RAX com %1; se forem iguais %1 recebe %2, se não RAX recebe %1"}
   "enter" {:gloss "monta o quadro da função" :template "empilha RBP, aponta RBP para o topo e abre %1 bytes de espaço local"}
   "pushf" {:gloss "empilha as flags" :template "o registrador de flags inteiro vai para o topo da pilha"}
   "popf" {:gloss "desempilha as flags" :template "o topo da pilha vira o novo registrador de flags"}
   "movsb" {:gloss "copia um byte de string" :template "copia o byte apontado por RSI para onde RDI aponta e anda os dois, e com rep na frente repete RCX vezes"}
   "stosb" {:gloss "grava AL na string" :template "grava AL onde RDI aponta e anda RDI, e com rep na frente repete RCX vezes"}
   "lodsb" {:gloss "lê um byte da string" :template "AL recebe o byte apontado por RSI e RSI anda"}
   "cmpsb" {:gloss "compara bytes de duas strings" :template "compara o byte apontado por RSI com o apontado por RDI, anda os dois e só guarda as flags"}
   "scasb" {:gloss "procura AL na string" :template "compara AL com o byte apontado por RDI, anda RDI e só guarda as flags"}
   "pause" {:gloss "avisa que isto é espera ocupada" :template "dá uma dica ao processador de que isto é um laço de espera, o que economiza energia e evita punição de ordenação"}
   "cpuid" {:gloss "pergunta o que o processador sabe fazer" :template "com o número da consulta em EAX, devolve em EAX, EBX, ECX e EDX o que este processador suporta"}
   "rdtsc" {:gloss "lê o contador de ciclos" :template "EDX:EAX recebem o contador de ciclos desde que a máquina ligou"}
   "int3" {:gloss "ponto de parada" :template "dispara a interrupção de depuração, que é o byte que o depurador escreve por cima do código para parar o programa"}
   "jrcxz" {:gloss "desvia se RCX for zero" :template "a execução pula para %1 quando RCX está zerado, sem olhar flag nenhuma"}
   "movss" {:gloss "copia um float de 32 bits" :template "%1 recebe %2 como escalar de 32 bits, e o resto do registrador vetorial fica como estava"}
   "movsd" {:gloss "copia um float de 64 bits" :template "%1 recebe %2 como escalar de 64 bits, e o resto do registrador vetorial fica como estava"}
   "movaps" {:gloss "copia o registrador vetorial inteiro" :template "%1 recebe os 16 bytes de %2 de uma vez, e a forma alinhada exige endereço múltiplo de 16"}
   "movdqa" {:gloss "copia 16 bytes de inteiros" :template "%1 recebe os 16 bytes de %2 tratados como inteiros, e a forma alinhada exige endereço múltiplo de 16"}
   "movd" {:gloss "atravessa a fronteira entre comum e vetorial" :template "%1 recebe %2, passando entre registrador de uso geral e registrador vetorial"}
   "addss" {:gloss "soma em float de 32 bits" :template "%1 passa a valer %1 mais %2, em ponto flutuante de 32 bits"}
   "addsd" {:gloss "soma em float de 64 bits" :template "%1 passa a valer %1 mais %2, em ponto flutuante de 64 bits"}
   "subss" {:gloss "subtrai em float de 32 bits" :template "%1 passa a valer %1 menos %2, em ponto flutuante de 32 bits"}
   "subsd" {:gloss "subtrai em float de 64 bits" :template "%1 passa a valer %1 menos %2, em ponto flutuante de 64 bits"}
   "mulss" {:gloss "multiplica em float de 32 bits" :template "%1 passa a valer %1 vezes %2, em ponto flutuante de 32 bits"}
   "mulsd" {:gloss "multiplica em float de 64 bits" :template "%1 passa a valer %1 vezes %2, em ponto flutuante de 64 bits"}
   "divss" {:gloss "divide em float de 32 bits" :template "%1 passa a valer %1 dividido por %2, em ponto flutuante de 32 bits"}
   "divsd" {:gloss "divide em float de 64 bits" :template "%1 passa a valer %1 dividido por %2, em ponto flutuante de 64 bits"}
   "sqrtss" {:gloss "raiz quadrada em float de 32 bits" :template "%1 recebe a raiz quadrada de %2, em ponto flutuante de 32 bits"}
   "sqrtsd" {:gloss "raiz quadrada em float de 64 bits" :template "%1 recebe a raiz quadrada de %2, em ponto flutuante de 64 bits"}
   "ucomiss" {:gloss "compara floats de 32 bits" :template "compara %1 com %2 em ponto flutuante de 32 bits e só mexe nas flags, e um NaN liga ZF, PF e CF ao mesmo tempo"}
   "ucomisd" {:gloss "compara floats de 64 bits" :template "compara %1 com %2 em ponto flutuante de 64 bits e só mexe nas flags, e um NaN liga ZF, PF e CF ao mesmo tempo"}
   "cvtsi2ss" {:gloss "converte inteiro para float de 32 bits" :template "%1 recebe %2 convertido de inteiro para ponto flutuante de 32 bits"}
   "cvtsi2sd" {:gloss "converte inteiro para float de 64 bits" :template "%1 recebe %2 convertido de inteiro para ponto flutuante de 64 bits"}
   "cvttss2si" {:gloss "converte float de 32 bits para inteiro" :template "%1 recebe %2 convertido para inteiro, cortando a parte fracionária em vez de arredondar"}
   "cvttsd2si" {:gloss "converte float de 64 bits para inteiro" :template "%1 recebe %2 convertido para inteiro, cortando a parte fracionária em vez de arredondar"}
   "cvtss2sd" {:gloss "alarga o float de 32 para 64 bits" :template "%1 recebe %2 convertido de ponto flutuante de 32 para 64 bits"}
   "cvtsd2ss" {:gloss "estreita o float de 64 para 32 bits" :template "%1 recebe %2 convertido de ponto flutuante de 64 para 32 bits, e a precisão que não cabe se perde"}
   "pxor" {:gloss "XOR sobre os bits do vetorial" :template "%1 fica com os bits ligados em só um dos dois, e pxor de um registrador com ele mesmo é o jeito curto de zerar"}
   "xorps" {:gloss "XOR sobre floats empacotados" :template "%1 fica com os bits ligados em só um dos dois, e xorps de um registrador com ele mesmo é o jeito curto de zerar"}}})
