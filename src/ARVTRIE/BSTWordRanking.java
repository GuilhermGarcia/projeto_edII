package ARVTRIE;

import java.util.ArrayList;
import java.util.List;

/** Mantem as palavras em uma BST lexicografica e associa cada uma a sua frequencia. */
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

    /** Insere uma palavra ou substitui sua frequencia quando ela ja existe. */
    public void addOrUpdate(String word, int newFrequency) {
        root = addOrUpdateRec(root, word, newFrequency);
    }

    private Node addOrUpdateRec(Node node, String word, int newFrequency) {
        if (node == null) {
            return new Node(word, newFrequency);
        }

        int cmp = word.compareTo(node.word);
        if (cmp < 0) {
            node.left = addOrUpdateRec(node.left, word, newFrequency);
        } else if (cmp > 0) {
            node.right = addOrUpdateRec(node.right, word, newFrequency);
        } else {
            node.frequency = newFrequency;
        }
        return node;
    }

    /** Retorna a frequencia da palavra; zero representa ausencia ou frequencia zero. */
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

    /** Remove uma palavra e recompõe a BST preservando sua ordenacao. */
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

    /** Retorna ate n palavras, ordenadas por frequencia decrescente. */
    public List<String> getTopWords(int n) {
        List<Node> allNodes = new ArrayList<>();
        collectAll(root, allNodes);

        allNodes.sort((a, b) -> Integer.compare(b.frequency, a.frequency));

        List<String> topWords = new ArrayList<>();
        for (int i = 0; i < Math.min(n, allNodes.size()); i++) {
            topWords.add(allNodes.get(i).word);
        }
        return topWords;
    }

    /** Faz percurso em ordem para transferir todos os nos para uma lista. */
    private void collectAll(Node node, List<Node> out) {
        if (node != null) {
            collectAll(node.left, out);
            out.add(node);
            collectAll(node.right, out);
        }
    }
}