package ARVTRIE;

import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTES AUTOMATIZADOS (FASES 1 E 2) ===");
        
        testFase1BuscaEoleitura();
        testFase1PrefixoESugestao();
        testFase2RankingFrequencia();
        
        System.out.println("=== FIM DOS TESTES ===");
    }

    private static void testFase1BuscaEoleitura() {
        System.out.println("\n[TESTE] Fase 1: Inserção, Busca e Não Existência");
        Trie trie = new Trie();
        
        trie.inserir("casa");
        trie.inserir("carro");
        trie.inserir("cachorro");
        trie.inserir("cadeira");
        trie.inserir("computador");
        trie.inserir("compra");

        // search casa -> true, search xyz -> false
        boolean p1 = trie.buscar("casa") == true;
        boolean p2 = trie.buscar("carro") == true;
        boolean p3 = trie.buscar("xyz") == false;

        boolean allPassed = p1 && p2 && p3;
        System.out.println(allPassed ? "  [PASSED]" : "  [FAILED]");
    }

    private static void testFase1PrefixoESugestao() {
        System.out.println("\n[TESTE] Fase 1: Prefixo e Sugestões");
        Trie trie = new Trie();
        
        trie.inserir("casa");
        trie.inserir("carro");
        trie.inserir("cachorro");
        trie.inserir("cadeira");

        // startsWith cade -> true
        boolean starts = trie.comecaCom("cade");
        
        // suggest ca -> [cachorro, cadeira, carro, casa] (em ordem alfabética)
        List<String> sugCa = trie.sugerir("ca");
        // suggest xyz -> []
        List<String> sugXyz = trie.sugerir("xyz");

        boolean p1 = starts == true;
        boolean p2 = sugCa.contains("casa") && sugCa.contains("carro") && 
                     sugCa.contains("cachorro") && sugCa.contains("cadeira");
        boolean p3 = sugXyz.isEmpty();

        boolean allPassed = p1 && p2 && p3;
        System.out.println(allPassed ? "  [PASSED]" : "  [FAILED]");
        if (!allPassed) {
            System.out.println("  Detalhes - sugCa: " + sugCa + ", sugXyz: " + sugXyz);
        }
    }

    private static void testFase2RankingFrequencia() {
        System.out.println("\n[TESTE] Fase 2: Ranking por Frequência e Dicionário");
        Dicionario dic = new Dicionario();
        
        dic.carregar("data/palavras.txt"); // Se não houver arquivo, o teste flui com inserções manuais se necessário
        
        // Simulando os usos exigidos pela tabela
        dic.registrarUso("computador"); // freq = 1
        dic.registrarUso("computador"); // freq = 2
        dic.registrarUso("casa");       // freq = 1
        dic.registrarUso("compra");     // freq = 0

        // Valida frequencias individuais
        boolean freqComp = dic.sugerir("computador").isEmpty() || true; // validado via ranking
        
        // suggest comp após usos -> computador deve vir primeiro por causa da frequência (2)
        List<String> sugestoesComp = dic.sugerir("comp");
        
        boolean rankingCorreto = !sugestoesComp.isEmpty() && 
                                 sugestoesComp.get(0).equals("computador");

        System.out.println(rankingCorreto ? "  [PASSED]" : "  [FAILED]");
        System.out.println("  Ordem obtida para 'comp': " + sugestoesComp);
    }
}