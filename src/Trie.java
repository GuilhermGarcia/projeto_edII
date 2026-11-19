import java.util.ArrayList;
import java.util.List;

/**
 * Arvore de prefixos (Trie) para palavras formadas pelas letras 'a' a 'z'.
 * Alem da busca exata, permite localizar o no de um prefixo e enumerar, por
 * DFS, todas as palavras abaixo dele. Tambem guarda a frequencia de uso de
 * cada palavra no seu no final (Fase 2): a Trie e a fonte de verdade da
 * frequencia.
 *
 * Todas as operacoes ignoram diferenca entre maiusculas e minusculas.
 * Palavras vazias ou com caracteres fora de 'a'-'z' (acentos, digitos,
 * hifen) nao sao armazenadas.
 */
public class Trie {

    private TrieNode root;

    /** Cria a raiz vazia da Trie. A raiz nao representa nenhuma letra. */
    public Trie() {
        root = new TrieNode();
    }

    /** Indica se a palavra (ja em minusculas) e nao vazia e usa so 'a'-'z'. */
    static boolean palavraValida(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c < 'a' || c > 'z') {
                return false;
            }
        }
        return true;
    }

    /**
     * Insere uma palavra, criando os nos que ainda nao existem e marcando
     * isEndOfWord no ultimo. Palavra invalida e ignorada. Inserir uma palavra
     * que ja existe nao altera sua frequencia. Custo: O(k).
     */
    public void insert(String word) {
        if (word == null) {
            return;
        }
        word = word.toLowerCase();
        if (!palavraValida(word)) {
            return;
        }
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (current.children[idx] == null) {
                current.children[idx] = new TrieNode();
            }
            current = current.children[idx];
        }
        current.isEndOfWord = true;
    }

    /** Retorna true somente se o caminho existe e termina uma palavra. O(k). */
    public boolean search(String word) {
        TrieNode node = traverseTo(word);
        return node != null && node.isEndOfWord;
    }

    /** Indica se existe alguma palavra com o prefixo informado. O(k). */
    public boolean startsWith(String prefix) {
        TrieNode node = traverseTo(prefix);
        if (node == null) {
            return false;
        }
        // Todo no abaixo da raiz leva a uma palavra; so a raiz de uma Trie vazia nao.
        if (node.isEndOfWord) {
            return true;
        }
        for (int i = 0; i < TrieNode.ALPHABET_SIZE; i++) {
            if (node.children[i] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna todas as palavras que comecam com o prefixo, em ordem
     * alfabetica (os filhos sao visitados de 'a' a 'z'). Prefixo vazio devolve
     * todo o vocabulario. Custo: O(k + m), m = nos da subarvore.
     */
    public List<String> suggest(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = traverseTo(prefix);
        if (node != null) {
            collectWords(node, new StringBuilder(prefix.toLowerCase()), results);
        }
        return results;
    }

    /** Retorna todas as palavras armazenadas, em ordem alfabetica. */
    public List<String> listarPalavras() {
        List<String> palavras = new ArrayList<>();
        collectWords(root, new StringBuilder(), palavras);
        return palavras;
    }

    /**
     * DFS que acumula as letras em um StringBuilder (reutilizado: cada chamada
     * desfaz o caractere acrescentado antes de voltar) e adiciona o resultado
     * sempre que encontra um no de fim de palavra.
     */
    private void collectWords(TrieNode node, StringBuilder prefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(prefix.toString());
        }
        for (int i = 0; i < TrieNode.ALPHABET_SIZE; i++) {
            if (node.children[i] != null) {
                prefix.append((char) ('a' + i));
                collectWords(node.children[i], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    /** Retorna a frequencia da palavra (0 se nao existir ou nao for final). */
    public int getFrequency(String word) {
        TrieNode node = traverseTo(word);
        return (node != null && node.isEndOfWord) ? node.frequency : 0;
    }

    /** Atualiza a frequencia de uma palavra existente (ignora as demais). */
    public void updateFrequency(String word, int frequency) {
        TrieNode node = traverseTo(word);
        if (node != null && node.isEndOfWord) {
            node.frequency = frequency;
        }
    }

    /**
     * Percorre a Trie ate o no correspondente ao texto, sem verificar se ele
     * e final de palavra (responsabilidade dos chamadores). Retorna null se o
     * caminho nao existir, se o texto for null ou tiver caractere fora de a-z.
     * O texto vazio leva a raiz.
     */
    private TrieNode traverseTo(String word) {
        if (word == null) {
            return null;
        }
        word = word.toLowerCase();
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (idx < 0 || idx >= TrieNode.ALPHABET_SIZE || current.children[idx] == null) {
                return null;
            }
            current = current.children[idx];
        }
        return current;
    }
}
