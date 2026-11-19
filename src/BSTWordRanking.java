import java.util.ArrayList;
import java.util.List;

/**
 * Arvore Binaria de Busca (Fase 2) que guarda cada palavra unica com sua
 * frequencia. A ordenacao da arvore e lexicografica (compareTo), o que permite
 * busca e atualizacao em O(h); a ordem por frequencia so e montada na extracao
 * do ranking (getTopWords).
 */
public class BSTWordRanking {

    private Node root;

    /** No da BST; a chave e a palavra e o valor associado e a frequencia. */
    private class Node {
        String word;
        int frequency;
        Node left, right;

        Node(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }
    }

    /**
     * Insere uma palavra nova OU atualiza a frequencia se ela ja existir
     * (nunca duplica o no). Versao iterativa: nao estoura a pilha mesmo se a
     * arvore degenerar em lista. O(h).
     */
    public void addOrUpdate(String word, int newFrequency) {
        if (root == null) {
            root = new Node(word, newFrequency);
            return;
        }
        Node current = root;
        while (true) {
            int cmp = word.compareTo(current.word);
            if (cmp == 0) {
                current.frequency = newFrequency;
                return;
            }
            if (cmp < 0) {
                if (current.left == null) {
                    current.left = new Node(word, newFrequency);
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(word, newFrequency);
                    return;
                }
                current = current.right;
            }
        }
    }

    /** Retorna a frequencia da palavra (0 se nao existir). O(h). */
    public int getFrequency(String word) {
        Node current = root;
        while (current != null) {
            int cmp = word.compareTo(current.word);
            if (cmp == 0) {
                return current.frequency;
            }
            current = (cmp < 0) ? current.left : current.right;
        }
        return 0;
    }

    /** Remove uma palavra (zero, um ou dois filhos), preservando a ordenacao. */
    public void remove(String word) {
        root = removeRec(root, word);
    }

    private Node removeRec(Node node, String word) {
        if (node == null) {
            return null;
        }
        int cmp = word.compareTo(node.word);
        if (cmp < 0) {
            node.left = removeRec(node.left, word);
        } else if (cmp > 0) {
            node.right = removeRec(node.right, word);
        } else {
            // Casos de zero ou um filho
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            // Dois filhos: copia o sucessor (menor da subarvore direita) e o remove
            Node successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.word = successor.word;
            node.frequency = successor.frequency;
            node.right = removeRec(node.right, successor.word);
        }
        return node;
    }

    /**
     * Retorna ate n palavras, da mais para a menos frequente. Percorre toda a
     * arvore em ordem e ordena a lista por frequencia (ordenacao estavel:
     * empates ficam em ordem alfabetica). O(n log n).
     */
    public List<String> getTopWords(int n) {
        List<Node> all = new ArrayList<>();
        collectAll(root, all);
        all.sort((a, b) -> Integer.compare(b.frequency, a.frequency));

        List<String> top = new ArrayList<>();
        for (int i = 0; i < Math.min(n, all.size()); i++) {
            top.add(all.get(i).word);
        }
        return top;
    }

    /** Coleta todos os nos em ordem lexicografica (auxiliar). */
    private void collectAll(Node node, List<Node> out) {
        if (node != null) {
            collectAll(node.left, out);
            out.add(node);
            collectAll(node.right, out);
        }
    }
}
