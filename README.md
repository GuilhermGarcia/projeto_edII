# Projeto ED II

Implementacao de um dicionario com busca exata, sugestoes por prefixo e
ordenacao por frequencia de uso.

## Como funciona

- `Trie`: armazena o caminho de cada palavra caractere a caractere. A busca e
	as sugestoes percorrem somente os nos correspondentes ao prefixo.
- `BSTWordRanking`: guarda as palavras em ordem lexicografica e associa uma
	frequencia a cada uma. As sugestoes da Trie sao reordenadas por essa
	frequencia.
- `Dicionario`: coordena as duas estruturas. Ao registrar um uso, incrementa
	a frequencia na Trie e atualiza a mesma palavra na BST.
- `MainTest`: executa testes simples de insercao, busca, prefixo e integracao
	do ranking.

## Execucao

Na raiz do projeto:

```bash
bash run_tests.sh
```

O script compila os arquivos de `src/ARVTRIE` na pasta `bin` e executa
`ARVTRIE.MainTest`.