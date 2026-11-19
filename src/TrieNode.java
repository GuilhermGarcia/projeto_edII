/**
 * No da Trie (Fase 1). Cada posicao de {@code children} representa uma letra
 * de 'a' a 'z'. Um no pode fazer parte do caminho de uma palavra sem ser o
 * final dela; por isso a flag {@code isEndOfWord} e necessaria.
 */
class TrieNode {

    /** Tamanho do alfabeto: letras de 'a' a 'z'. */
    static final int ALPHABET_SIZE = 26;

    /** Array de filhos (um por letra): 'a' ocupa o indice 0 e 'z' o 25. */
    TrieNode[] children;

    /** Marca o final de uma palavra. */
    boolean isEndOfWord;

    /** Frequencia de uso da palavra que termina neste no (Fase 2). */
    int frequency;

    /** Cria um no vazio, sem filhos e sem palavra terminando nele. */
    public TrieNode() {
        children = new TrieNode[ALPHABET_SIZE];
        isEndOfWord = false;
        frequency = 0;
    }
}
