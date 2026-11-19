# Projeto ED II - Autocomplete e Correcao Ortografica (Fases 1 a 4)

Dicionario com busca exata, sugestoes por prefixo, ranking por frequencia de
uso, cache de sugestoes e correcao ortografica. Java puro, sem bibliotecas
externas (testado com JDK 23 e compilado tambem com `--release 8`).

## Estrutura

```
src/   TrieNode.java  Trie.java  BSTWordRanking.java  Levenshtein.java
       Dicionario.java  Main.java  MainTest.java
data/  palavras.txt  testes.txt
run_tests.sh   run_tests.bat   README.md
.project  .classpath            (projeto Eclipse: File > Import > Existing Projects)
```

- `Trie` (+ `TrieNode`): palavras a-z, `insert`, `search`, `startsWith`,
  `suggest` (DFS em ordem alfabetica) e frequencia no no final
  (`getFrequency`, `updateFrequency`). E a fonte de verdade da frequencia.
- `BSTWordRanking`: BST ordenada lexicograficamente com a frequencia de cada
  palavra: `addOrUpdate`, `getFrequency`, `remove`, `getTopWords`.
- `Dicionario`: integra tudo. `carregar`, `adicionar` (comando `add`),
  `buscar`, `buscarPrefixo`, `registrarUso`, `sugerir` (por frequencia),
  `sugerirComCache` (HashMap, invalidado por prefixo), `corrigir`.
- `Levenshtein`: distancia por programacao dinamica e busca da palavra mais
  proxima, podando candidatas por diferenca de comprimento.
- `MainTest`: suite sem dependencias; cada teste imprime `[PASSED]`/`[FAILED]`,
  mostra o placar e sai com codigo 1 se algo falhar.

## Como executar

Na raiz do projeto (precisa de `javac` e `java` no PATH):

```bash
bash run_tests.sh        # Linux/Mac/Git Bash
run_tests.bat            # Windows
```

Os scripts compilam em `bin/`, rodam `MainTest` e depois processam
`data/testes.txt`; terminam com codigo 0 somente se todos os testes passarem.
Para processar um arquivo de teste manualmente (secao 9.2.4 da especificacao):

```bash
java -cp bin Dicionario data/testes.txt [data/palavras.txt]
java -cp bin Main [data/palavras.txt]      # demonstracao (secao 8 da especificacao)
```

Se o dicionario nao for informado, o programa procura `palavras.txt` ao lado
do arquivo de teste, depois em `data/`.

## Formato de `testes.txt`

Uma linha por comando: `add`, `use`, `search`, `suggest`, `correct`; `#` inicia
comentario; `-> saida` e opcional e valida o resultado (`[PASSED]`/`[FAILED]`).
A numeracao `[n]` conta so os comandos. Em `suggest` com frequencias, palavras
de mesma frequencia podem aparecer em qualquer ordem (a especificacao lista
`compra(0)` antes de `companhia(0)`); sem frequencias, a ordem e alfabetica.

## Decisoes de projeto

- Alfabeto a-z: palavras vazias ou com acento, digito ou hifen sao recusadas
  pela Trie (e, portanto, pela BST). `carregar` conta essas linhas em
  `getPalavrasIgnoradas()` e o programa as informa.
- Arquivos de palavras e de teste sao lidos como UTF-8, com ou sem BOM e com
  fim de linha LF ou CRLF (arquivos salvos pelo Bloco de Notas funcionam).
- `corrigir(p)` aceita distancia ate 2 (`DISTANCIA_MAXIMA_PADRAO`) e devolve
  `null` se nada for parecido (`corrigir(p, max)` devolve `Optional`); empates
  de distancia saem em ordem alfabetica. A lista de palavras usada na correcao
  e guardada e refeita so quando entra palavra nova.
- `carregar` embaralha as palavras (semente fixa) antes de inserir: dicionarios
  ordenados degeneram a BST em lista (O(n^2) e estouro de pilha com ~50 mil
  palavras). O conteudo final das estruturas nao muda.
- A invalidacao do cache remove todos os prefixos da palavra (O(k^2)).

## Limitacoes conhecidas

- Sem acentos (a Trie tem 26 posicoes por no).
- A correcao compara com todas as palavras de comprimento compativel
  (O(N*a*b)); nao usa a Trie para podar ramos (otimizacao opcional).
- A distancia pura nao distingue palavras igualmente proximas (ex.: com
  dicionarios grandes "caza" empata entre "cama", "casa", ...).
