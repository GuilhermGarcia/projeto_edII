import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * Suite de testes automatizados, sem bibliotecas externas (secao 15 da
 * especificacao). Cada teste imprime [PASSED] ou [FAILED]; no fim mostra o
 * placar e o programa termina com codigo 1 se algum teste falhar.
 * Executar na raiz do projeto (usa data/palavras.txt e data/testes.txt).
 */
public class MainTest {

    private static int aprovados = 0;
    private static int total = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== INICIANDO TESTES AUTOMATIZADOS (FASES 1 A 4) ===");

        testTrieInsercaoEBusca();
        testTrieBordas();
        testTriePrefixoESugestao();
        testTrieFrequencia();
        testBSTBasico();
        testBSTRemocao();
        testBSTTopWords();
        testDicionarioCarregar();
        testCarregarBomEUtf8();
        testDicionarioRanking();
        testDicionarioSincronia();
        testLevenshtein();
        testCorrecaoOrtografica();
        testCacheSugestoes();
        testCacheInvalidacao();
        testArquivoTestes();

        System.out.println("\n=== RESULTADO: " + aprovados + "/" + total + " testes PASSED ===");
        if (aprovados != total) {
            System.exit(1);
        }
    }

    /** Registra o resultado de um teste e imprime [PASSED] ou [FAILED]. */
    private static void resultado(String nome, boolean passou, String detalhe) {
        total++;
        if (passou) {
            aprovados++;
        }
        System.out.println("[TESTE] " + nome + "\n  " + (passou ? "[PASSED]" : "[FAILED] " + detalhe));
    }

    private static Dicionario dicionarioPadrao() {
        Dicionario dic = new Dicionario();
        dic.carregar("data/palavras.txt");
        return dic;
    }

    // ---------------------------------------------------------------- Trie

    private static void testTrieInsercaoEBusca() {
        Trie trie = new Trie();
        for (String w : new String[] {"casa", "carro", "cachorro", "cadeira", "computador", "compra"}) {
            trie.insert(w);
        }
        boolean ok = trie.search("casa") && trie.search("carro") && trie.search("cadeira")
                && trie.search("computador") && !trie.search("xyz")
                && !trie.search("cade") && !trie.search("ca")   // prefixo nao e palavra
                && !trie.search("casas")                        // maior que a palavra
                && trie.search("CASA");                         // sem diferenciar caixa
        resultado("Trie: insercao e busca exata", ok, "search retornou valor inesperado");

        Trie t2 = new Trie();
        t2.insert("casa");
        t2.insert("casaco");
        resultado("Trie: palavra que e prefixo de outra",
                t2.search("casa") && t2.search("casaco") && !t2.search("casac"), "casa/casaco");
    }

    private static void testTrieBordas() {
        Trie trie = new Trie();
        trie.insert(null);
        trie.insert("");
        trie.insert("ab1c");
        trie.insert("a\u00e7\u00e3o");
        boolean ok = trie.listarPalavras().isEmpty() && !trie.search("") && !trie.search("abc")
                && !trie.search("ao") && !trie.search(null) && !trie.startsWith(null)
                && trie.suggest(null).isEmpty() && !trie.startsWith("");
        resultado("Trie: entradas invalidas (null, vazia, digito, acento) nao criam palavras", ok,
                "palavras=" + trie.listarPalavras());
    }

    private static void testTriePrefixoESugestao() {
        Trie trie = new Trie();
        for (String w : new String[] {"casa", "carro", "cachorro", "cadeira", "computador", "compra"}) {
            trie.insert(w);
        }
        List<String> ca = trie.suggest("ca");
        boolean ok = trie.startsWith("cade") && trie.startsWith("ca") && trie.startsWith("casa")
                && !trie.startsWith("xyz") && !trie.startsWith("cadeiras")
                && ca.equals(Arrays.asList("cachorro", "cadeira", "carro", "casa"))
                && trie.suggest("xyz").isEmpty()
                && trie.suggest("casa").equals(Arrays.asList("casa"))
                && trie.suggest("CA").equals(ca)
                && trie.suggest("").size() == 6;
        trie.insert("casa"); // duplicada nao repete a sugestao
        ok = ok && trie.suggest("ca").size() == 4;
        resultado("Trie: busca por prefixo e sugestoes em ordem alfabetica", ok, "suggest(ca)=" + ca);
    }

    private static void testTrieFrequencia() {
        Trie trie = new Trie();
        trie.insert("casa");
        boolean ok = trie.getFrequency("casa") == 0;
        trie.updateFrequency("casa", 7);
        trie.updateFrequency("cas", 5);  // nao e palavra: ignorado
        trie.updateFrequency("xyz", 5);  // inexistente: ignorado
        trie.insert("casa");             // reinsercao preserva a frequencia
        ok = ok && trie.getFrequency("casa") == 7 && trie.getFrequency("cas") == 0
                && trie.getFrequency("xyz") == 0 && !trie.search("cas");
        resultado("Trie: frequencia no no final (getFrequency / updateFrequency)", ok,
                "freq(casa)=" + trie.getFrequency("casa"));
    }

    // ----------------------------------------------------------------- BST

    private static BSTWordRanking bstExemplo() {
        BSTWordRanking b = new BSTWordRanking();
        for (String w : new String[] {"m", "f", "t", "c", "h", "p", "w"}) {
            b.addOrUpdate(w, w.charAt(0) - 'a');
        }
        return b;
    }

    private static void testBSTBasico() {
        BSTWordRanking vazia = new BSTWordRanking();
        boolean ok = vazia.getFrequency("x") == 0 && vazia.getTopWords(5).isEmpty();
        vazia.remove("x"); // remover de arvore vazia nao falha

        BSTWordRanking b = bstExemplo();
        ok = ok && b.getFrequency("m") == 12 && b.getFrequency("w") == 22 && b.getFrequency("z") == 0;
        b.addOrUpdate("m", 99); // atualiza sem duplicar
        ok = ok && b.getFrequency("m") == 99 && b.getTopWords(100).size() == 7;
        resultado("BST: insercao, busca e atualizacao de frequencia (sem duplicar no)", ok, "");
    }

    private static void testBSTRemocao() {
        BSTWordRanking b = bstExemplo();
        b.remove("c"); // folha
        boolean ok = b.getFrequency("c") == 0 && b.getTopWords(100).size() == 6;
        b.addOrUpdate("g", 6);
        b.remove("f"); // um filho (g)
        ok = ok && b.getTopWords(100).size() == 6 && b.getFrequency("g") == 6;
        b.remove("m"); // raiz com dois filhos
        List<String> top = b.getTopWords(100);
        ok = ok && !top.contains("m") && top.size() == 5 && b.getFrequency("t") == 19
                && b.getFrequency("p") == 15 && b.getFrequency("w") == 22 && b.getFrequency("h") == 7;
        b.remove("inexistente");
        ok = ok && b.getTopWords(100).size() == 5;
        resultado("BST: remocao (folha, um filho, dois filhos, inexistente)", ok, "top=" + top);
    }

    private static void testBSTTopWords() {
        BSTWordRanking b = bstExemplo();
        b.addOrUpdate("m", 99);
        boolean ok = b.getTopWords(3).equals(Arrays.asList("m", "w", "t"))
                && b.getTopWords(0).isEmpty() && b.getTopWords(-1).isEmpty()
                && b.getTopWords(50).size() == 7;
        BSTWordRanking empate = new BSTWordRanking();
        for (String w : new String[] {"b", "a", "d", "c"}) {
            empate.addOrUpdate(w, 1);
        }
        ok = ok && empate.getTopWords(4).equals(Arrays.asList("a", "b", "c", "d"));

        // arvore degenerada (insercao em ordem) nao pode estourar a pilha
        BSTWordRanking lista = new BSTWordRanking();
        for (int i = 0; i < 20000; i++) {
            lista.addOrUpdate(String.format("w%06d", i), i);
        }
        ok = ok && lista.getFrequency("w019999") == 19999;
        resultado("BST: getTopWords (ordem decrescente, n=0, n<0, n>tamanho, empates) e arvore degenerada", ok,
                "top3=" + b.getTopWords(3));
    }

    // ----------------------------------------------------------- Dicionario

    private static void testDicionarioCarregar() throws IOException {
        File f = File.createTempFile("dic", ".txt");
        f.deleteOnExit();
        Files.write(f.toPath(), Arrays.asList("casa", "  CARRO  ", "", "cachorro", "casa", "Cadeira", "   ",
                "computador", "caf\u00e9", "ab1"), StandardCharsets.UTF_8);
        Dicionario dic = new Dicionario();
        int n = dic.carregar(f.getPath());
        boolean ok = n == 5 && dic.getPalavrasIgnoradas() == 2 && dic.buscar("carro") && dic.buscar("cadeira") && dic.sugerir("c").size() == 5
                && dic.sugerir("").size() == 5;
        Dicionario inexistente = new Dicionario();
        PrintStream err = System.err;
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        int zero = inexistente.carregar("nao_existe.txt");
        System.setErr(err);
        ok = ok && zero == 0 && inexistente.sugerir("").isEmpty();

        Dicionario padrao = dicionarioPadrao();
        ok = ok && padrao.sugerir("").size() == 8 && padrao.buscar("compartilhar");
        resultado("Dicionario: carregar (linhas vazias, caixa, duplicatas, invalidas, arquivo inexistente)", ok,
                "n=" + n);
    }

    /** Arquivo salvo por editores do Windows (UTF-8 com BOM, CRLF) deve carregar sem perder a 1a palavra. */
    private static void testCarregarBomEUtf8() throws IOException {
        File f = File.createTempFile("bom", ".txt");
        f.deleteOnExit();
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        String crlf = new String(new char[] {13, 10});
        String acao = "a" + (char) 231 + (char) 227 + "o"; // palavra com cedilha e til (codigos Unicode)
        byte[] corpo = ("casa" + crlf + "carro" + crlf + acao + crlf).getBytes(StandardCharsets.UTF_8);
        byte[] tudo = new byte[bom.length + corpo.length];
        System.arraycopy(bom, 0, tudo, 0, bom.length);
        System.arraycopy(corpo, 0, tudo, bom.length, corpo.length);
        Files.write(f.toPath(), tudo);
        Dicionario dic = new Dicionario();
        int n = dic.carregar(f.getPath());
        boolean ok = n == 2 && dic.buscar("casa") && dic.buscar("carro") && dic.getPalavrasIgnoradas() == 1
                && !dic.buscar("ao");
        resultado("Dicionario: carregar arquivo UTF-8 com BOM e CRLF (1a palavra preservada, acentuada ignorada)", ok,
                "n=" + n + " casa=" + dic.buscar("casa") + " ignoradas=" + dic.getPalavrasIgnoradas());
    }

    private static void testDicionarioRanking() {
        Dicionario dic = dicionarioPadrao();
        boolean ok = dic.sugerir("comp").equals(Arrays.asList("companhia", "compartilhar", "compra", "computador"));
        dic.registrarUso("computador");
        dic.registrarUso("computador");
        dic.registrarUso("casa");
        ok = ok && dic.getFrequencia("computador") == 2 && dic.getFrequencia("casa") == 1
                && dic.sugerir("comp").equals(Arrays.asList("computador", "companhia", "compartilhar", "compra"));
        for (int i = 0; i < 3; i++) {
            dic.registrarUso("compra");
        }
        ok = ok && dic.sugerir("comp").equals(Arrays.asList("compra", "computador", "companhia", "compartilhar"));
        dic.registrarUso("Casa"); // ignora caixa
        ok = ok && dic.getFrequencia("casa") == 2
                && dic.maisFrequentes(3).equals(Arrays.asList("compra", "casa", "computador")); // casa e computador empatam (2)
        resultado("Dicionario: ranking por frequencia (Trie + BST)", ok, "comp=" + dic.sugerir("comp"));
    }

    private static void testDicionarioSincronia() {
        Dicionario dic = dicionarioPadrao();
        dic.registrarUso("palavranova"); // inexistente: e inserida com freq 1
        boolean ok = dic.buscar("palavranova") && dic.getFrequencia("palavranova") == 1
                && dic.maisFrequentes(1).equals(Arrays.asList("palavranova"));
        dic.registrarUso(null);
        dic.registrarUso("");
        dic.registrarUso("caf\u00e9"); // invalida: nao pode entrar so na BST
        ok = ok && dic.sugerir("").size() == 9 && dic.maisFrequentes(100).size() == 9
                && !dic.buscar("") && !dic.buscarPrefixo("caf");
        dic.adicionar("zebra");        // add: freq 0, e preserva freq se ja existe
        dic.registrarUso("zebra");
        dic.adicionar("zebra");
        ok = ok && dic.getFrequencia("zebra") == 1 && dic.buscarPrefixo("zeb") && !dic.adicionar("ab1")
                && !dic.adicionar("") && !dic.adicionar(null);
        resultado("Dicionario: sincronia Trie x BST, add, entradas invalidas", ok, "");
    }

    // ---------------------------------------------------------- Levenshtein

    private static void testLevenshtein() {
        String[][] pares = {
            {"kitten", "sitting", "3"}, {"casa", "casa", "0"}, {"", "abc", "3"}, {"abc", "", "3"},
            {"", "", "0"}, {"computadro", "computador", "2"}, {"caza", "casa", "1"},
            {"cadiera", "cadeira", "2"}, {"caro", "carro", "1"}, {"flaw", "lawn", "2"}, {"Casa", "cASA", "0"}
        };
        boolean ok = true;
        for (String[] p : pares) {
            int esperado = Integer.parseInt(p[2]);
            ok = ok && Levenshtein.levenshtein(p[0], p[1]) == esperado
                    && Levenshtein.levenshtein(p[1], p[0]) == esperado;
        }
        boolean nulo = false;
        try {
            Levenshtein.levenshtein(null, "a");
        } catch (NullPointerException e) {
            nulo = true;
        }
        resultado("Levenshtein: distancias conhecidas, simetria, caixa, vazias, null", ok && nulo, "");
    }

    private static void testCorrecaoOrtografica() {
        Dicionario dic = dicionarioPadrao();
        boolean ok = "computador".equals(dic.corrigir("computadro"))
                && "casa".equals(dic.corrigir("caza"))
                && "cadeira".equals(dic.corrigir("cadiera"))
                && "carro".equals(dic.corrigir("caro"))
                && "casa".equals(dic.corrigir("casa"))                 // ja correta
                && "computador".equals(dic.corrigir("COMPUTADRO"))     // caixa
                && dic.corrigir("xyz") == null                         // nada parecido
                && dic.corrigir(null) == null
                && dic.corrigir("xyz", 10).isPresent();                // limite maior aceita
        Dicionario vazio = new Dicionario();
        ok = ok && vazio.corrigir("abc") == null && !vazio.corrigir("abc", 5).isPresent();

        Dicionario empate = new Dicionario();
        empate.adicionar("bala");
        empate.adicionar("cala");
        empate.adicionar("mala");
        ok = ok && "bala".equals(empate.corrigir("sala")); // empate: ordem alfabetica

        // o vocabulario guardado para a correcao precisa acompanhar palavras novas
        empate.adicionar("sala");
        ok = ok && "sala".equals(empate.corrigir("sala"));
        empate.registrarUso("salsa");
        ok = ok && "salsa".equals(empate.corrigir("salsa"));
        resultado("Correcao ortografica (Levenshtein + limite de distancia)", ok,
                "caza=" + dic.corrigir("caza") + " xyz=" + dic.corrigir("xyz"));
    }

    // ---------------------------------------------------------------- Cache

    private static void testCacheSugestoes() {
        Dicionario dic = dicionarioPadrao();
        boolean ok = !dic.estaNoCache("comp");
        List<String> primeira = dic.sugerirComCache("comp");
        ok = ok && dic.estaNoCache("comp") && primeira.equals(dic.sugerir("comp"));
        primeira.clear(); // copia defensiva: nao pode afetar o cache
        ok = ok && dic.sugerirComCache("comp").size() == 4 && dic.sugerirComCache("COMP").size() == 4
                && dic.sugerirComCache(null).isEmpty() && dic.sugerirComCache("xyz").isEmpty();
        resultado("Cache: preenchido na 1a consulta, copia defensiva, chave sem caixa", ok, "");
    }

    private static void testCacheInvalidacao() {
        Dicionario dic = dicionarioPadrao();
        dic.sugerirComCache("c");
        dic.sugerirComCache("co");
        dic.sugerirComCache("comp");
        dic.sugerirComCache("ca");
        dic.sugerirComCache("x");
        dic.registrarUso("computador"); // invalida c, co, com, comp, ..., computador
        boolean ok = !dic.estaNoCache("c") && !dic.estaNoCache("co") && !dic.estaNoCache("comp")
                && dic.estaNoCache("ca") && dic.estaNoCache("x")
                && dic.sugerirComCache("comp").get(0).equals("computador");

        dic.sugerirComCache("nova");
        dic.adicionar("novapalavra");
        ok = ok && dic.sugerirComCache("nova").contains("novapalavra");

        dic.sugerirComCache("");
        dic.registrarUso("zebra");
        ok = ok && dic.sugerirComCache("").contains("zebra");
        resultado("Cache: invalidacao dos prefixos da palavra usada ou adicionada", ok, "");
    }

    // -------------------------------------------------------- testes.txt

    private static void testArquivoTestes() {
        Dicionario dic = dicionarioPadrao();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int[] r = dic.processarArquivo("data/testes.txt", new PrintStream(buf));
        resultado("Integracao: data/testes.txt (add, use, search, suggest, correct)",
                r[1] >= 12 && r[0] == r[1], r[0] + "/" + r[1] + "\n" + buf);

        // um teste que deve ser reprovado de proposito: o validador precisa detectar a falha
        File f;
        try {
            f = File.createTempFile("falha", ".txt");
            f.deleteOnExit();
            Files.write(f.toPath(), Arrays.asList("search casa -> false", "suggest ca -> [casa]",
                    "correct caza -> carro", "xyz casa -> true"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            resultado("Integracao: validador detecta saidas erradas", false, e.toString());
            return;
        }
        int[] f2 = dicionarioPadrao().processarArquivo(f.getPath(), new PrintStream(new ByteArrayOutputStream()));
        resultado("Integracao: validador detecta saidas erradas e comando desconhecido", f2[0] == 0 && f2[1] == 4,
                f2[0] + "/" + f2[1]);
    }
}
