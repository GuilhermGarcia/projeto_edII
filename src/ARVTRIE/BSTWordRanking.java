package ARVTRIE;

import java.util.ArrayList;
import java.util.List;

public class BSTWordRanking {
    
    private Node root;

    // Classe interna do Nó conforme exigido no enunciado
    private class Node {
        String word;
        int frequency;
        Node left, right;

        Node(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }
    }

    // Insere palavra nova OU atualiza frequência se já existir
    public void addOrUpdate(String word, int newFrequency) {
        root = addOrUpdateRec(root, word, newFrequency);
    }

    private Node addOrUpdateRec(Node node, String word, int newFrequency) {
        if (node == null) {
            return new Node(word, newFrequency);
        }

        // Ordenação lexicográfica (alfabética) para busca O(h)
        int cmp = word.compareTo(node.word);
        if (cmp < 0) {
            node.left = addOrUpdateRec(node.left, word, newFrequency);
        } else if (cmp > 0) {
            node.right = addOrUpdateRec(node.right, word, newFrequency);
        } else {
            // A palavra já existe, apenas atualiza a frequência no próprio nó
            node.frequency = newFrequency;
        }
        return node;
    }

    // Retorna a frequência de uma palavra (0 se não existir)
    public int getFrequency(String word) {
        Node node = searchRec(root, word);
        return (node != null) ? node.frequency : 0;
    }

    private Node searchRec(Node node, String word) {
        if (node == null || node.word.equals(word)) {
            return node;
        }
        if (word.compareTo(node.word) < 0) {
            return searchRec(node.left, word);
        }
        return searchRec(node.right, word);
    }

    // Remove uma palavra (usado internamente caso precise)
    public void remove(String word) {
        root = removeRec(root, word);
    }

    private Node removeRec(Node root, String word) {
        if (root == null) return root;

        int cmp = word.compareTo(root.word);
        if (cmp < 0) {
            root.left = removeRec(root.left, word);
        } else if (cmp > 0) {
            root.right = removeRec(root.right, word);
        } else {
            // Caso 1 e 2: Um ou nenhum filho
            if (root.left == null) return root.right;
            else if (root.right == null) return root.left;

            // Caso 3: Dois filhos (Pega o menor valor da subárvore direita)
            Node successor = minValueNode(root.right);
            root.word = successor.word;
            root.frequency = successor.frequency;
            root.right = removeRec(root.right, successor.word);
        }
        return root;
    }

    private Node minValueNode(Node root) {
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // Retorna as N palavras mais frequentes (ordem decrescente)
    public List<String> getTopWords(int n) {
        List<Node> allNodes = new ArrayList<>();
        collectAll(root, allNodes);

        // Ordena a lista inteira por frequência usando lambda (O(n log n))
        allNodes.sort((a, b) -> Integer.compare(b.frequency, a.frequency));

        List<String> topWords = new ArrayList<>();
        // Pega apenas as 'n' primeiras palavras (ou o máximo que tivermos)
        for (int i = 0; i < Math.min(n, allNodes.size()); i++) {
            topWords.add(allNodes.get(i).word);
        }
        return topWords;
    }

    // Coleta todas as palavras em ordem para a lista (auxiliar)
    private void collectAll(Node node, List<Node> out) {
        if (node != null) {
            collectAll(node.left, out);
            out.add(node);
            collectAll(node.right, out);
        }
    }
}