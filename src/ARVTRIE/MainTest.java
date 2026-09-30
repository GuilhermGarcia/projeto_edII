package ARVTRIE;

import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        // Os testes sao executados diretamente pelo script, sem framework externo.
        System.out.println("=== INICIANDO TESTES AUTOMATIZADOS (FASES 1 E 2) ===");
        
        testTrieInsercaoEbusca();
        testTriePrefixoESugestao();
        testRankingeFrequencia();
        
        System.out.println("=== FIM DOS TESTES ===");
    }

    private static void testTrieInsercaoEbusca() {
        System.out.println("\n[TESTE] Inserção e Busca na Trie");
        Trie trie = new Trie();
        
        trie.inserir("casa");
        trie.inserir("carro");
        trie.inserir("computador");

        // Valida tanto palavras existentes quanto uma palavra ausente.
        boolean passed1 = trie.buscar("casa") == true;
        boolean passed2 = trie.buscar("carro") == true;
        boolean passed3 = trie.buscar("computador") == true;
        boolean passed4 = trie.buscar("cadeira") == false;

        boolean allPassed = passed1 && passed2 && passed3 && passed4;
        System.out.println(allPassed ? "  [PASSED]" : "  [FAILED]");
    }

    private static void testTriePrefixoESugestao() {
        System.out.println("\n[TESTE] Busca por Prefixo e Sugestões");
        Trie trie = new Trie();
        
        trie.inserir("casa");
        trie.inserir("carro");
        trie.inserir("cachorro");
        trie.inserir("cadeira");

        boolean prefixoExiste = trie.comecaCom("ca");
        List<String> sugestoes = trie.sugerir("ca");

        boolean passedPrefix = prefixoExiste == true;
        boolean passedSuggest = sugestoes.contains("casa") && 
                                sugestoes.contains("carro") && 
                                sugestoes.contains("cachorro") && 
                                sugestoes.contains("cadeira");

        boolean allPassed = passedPrefix && passedSuggest;
        System.out.println(allPassed ? "  [PASSED]" : "  [FAILED]");
        if (!allPassed) {
            System.out.println("  Sugestões retornadas: " + sugestoes);
        }
    }

    private static void testRankingeFrequencia() {
        System.out.println("\n[TESTE] Ranking por Frequência (Integração Dicionário)");
        Dicionario dic = new Dicionario();
        
        // Cada uso atualiza a Trie e a BST por meio da fachada Dicionario.
        dic.registrarUso("computador");
        dic.registrarUso("computador"); // frequencia 2
        dic.registrarUso("casa");       // frequencia 1
        dic.registrarUso("compra");     // frequencia 0

        List<String> sugestoes = dic.sugerir("comp");
        
        // A primeira sugestão deve ser "computador" por ter maior frequência (2)
        boolean passedRanking = !sugestoes.isEmpty() && sugestoes.get(0).equals("computador");

        System.out.println(passedRanking ? "  [PASSED]" : "  [FAILED]");
        System.out.println("  Ordem obtida para 'comp': " + sugestoes);
    }
}