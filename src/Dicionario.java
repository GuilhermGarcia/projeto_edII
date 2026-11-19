import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Classe principal (Fase 4): integra a Trie (palavras, busca por prefixo e
 * frequencia), a BST (ranking por frequencia), o HashMap (cache de sugestoes)
 * e a distancia de Levenshtein (correcao ortografica).
 *
 * Uso na linha de comando (secao 9.2.4 da especificacao):
 * {@code java -cp . Dicionario testes.txt [palavras.txt]}
 */
public class Dicionario {

    /** Distancia maxima aceita por {@link #corrigir(String)}. */
    public static final int DISTANCIA_MAXIMA_PADRAO = 2;

    private Trie trie;
    private BSTWordRanking ranking;
    private Map<String, List<String>> cache;

    /** Quantas linhas nao vazias do ultimo arquivo carregado foram recusadas (caractere fora de a-z). */
    private int palavrasIgnoradas;

    /** Vocabulario em ordem alfabetica usado por corrigir(); null quando precisa ser refeito. */
    private List<String> vocabulario;

    public Dicionario() {
        trie = new Trie();
        ranking = new BSTWordRanking();
        cache = new HashMap<>();
    }

    // ------------------------------------------------------------------
    // Fase 1: carga, busca e sugestao
    // ------------------------------------------------------------------

    /**
     * Le um arquivo (UTF-8, com ou sem BOM) com uma palavra por linha e carrega
     * cada palavra na Trie e na BST com frequencia 0. Linhas vazias e repetidas
     * sao ignoradas; linhas com caracteres fora de 'a'-'z' (acentos, digitos,
     * hifen) sao recusadas e contadas em {@link #getPalavrasIgnoradas()}.
     * Retorna quantas palavras distintas foram carregadas (0 e uma mensagem em
     * System.err se o arquivo nao puder ser lido).
     */
    public int carregar(String arquivo) {
        Set<String> palavras = new LinkedHashSet<>();
        palavrasIgnoradas = 0;
        try (BufferedReader br = abrirLeitor(arquivo)) {
            String linha;
            boolean primeira = true;
            while ((linha = br.readLine()) != null) {
                linha = semBom(linha, primeira).trim().toLowerCase();
                primeira = false;
                if (linha.isEmpty()) {
                    continue;
                }
                if (Trie.palavraValida(linha)) {
                    palavras.add(linha);
                } else {
                    palavrasIgnoradas++;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return 0;
        }

        // Dicionarios costumam vir em ordem alfabetica; inserir assim numa BST sem
        // balanceamento a transforma em lista (O(n^2) na carga). Embaralhar com
        // semente fixa mantem a altura proxima de log n e o resultado reprodutivel;
        // o conteudo final da Trie e da BST e o mesmo.
        List<String> ordem = new ArrayList<>(palavras);
        Collections.shuffle(ordem, new Random(42));
        for (String palavra : ordem) {
            adicionar(palavra);
        }
        return ordem.size();
    }

    /** Quantas linhas do ultimo arquivo carregado foram recusadas por terem caracteres fora de a-z. */
    public int getPalavrasIgnoradas() {
        return palavrasIgnoradas;
    }

    /** Abre um arquivo texto como UTF-8 (independente da configuracao do sistema). */
    private static BufferedReader abrirLeitor(String arquivo) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8));
    }

    /** Remove a marca BOM (U+FEFF) que alguns editores gravam no inicio de arquivos UTF-8. */
    private static String semBom(String linha, boolean primeiraLinha) {
        return (primeiraLinha && !linha.isEmpty() && linha.charAt(0) == '\uFEFF') ? linha.substring(1) : linha;
    }

    /**
     * Insere a palavra no dicionario com frequencia 0 (comando "add"). Se ela ja
     * existir, a frequencia atual e preservada. Retorna false (sem inserir)
     * se a palavra for vazia ou tiver caractere fora de 'a'-'z'.
     */
    public boolean adicionar(String palavra) {
        if (palavra == null) {
            return false;
        }
        palavra = palavra.trim().toLowerCase();
        if (!Trie.palavraValida(palavra)) {
            return false;
        }
        if (!trie.search(palavra)) {
            vocabulario = null; // palavra nova: a lista usada na correcao fica desatualizada
        }
        trie.insert(palavra);
        ranking.addOrUpdate(palavra, trie.getFrequency(palavra));
        invalidateCacheFor(palavra);
        return true;
    }

    /** Indica se a palavra existe no dicionario. */
    public boolean buscar(String palavra) {
        return trie.search(palavra);
    }

    /** Indica se existe alguma palavra com o prefixo. */
    public boolean buscarPrefixo(String prefixo) {
        return trie.startsWith(prefixo);
    }

    // ------------------------------------------------------------------
    // Fase 2: frequencia e ranking
    // ------------------------------------------------------------------

    /**
     * Registra o uso de uma palavra: incrementa a frequencia na Trie (fonte de
     * verdade) e propaga para a BST; palavra inexistente e inserida antes.
     * Palavra invalida (null, vazia, fora de 'a'-'z') e ignorada. Invalida o
     * cache dos prefixos da palavra.
     */
    public void registrarUso(String palavra) {
        if (palavra == null) {
            return;
        }
        palavra = palavra.trim().toLowerCase();

        // 1. Descobre a frequencia atual (a partir da Trie)
        int freqAtual = trie.getFrequency(palavra);

        // 2. Se a palavra nao existe, insere (frequencia 0)
        if (freqAtual == 0 && !trie.search(palavra)) {
            trie.insert(palavra);
            if (!trie.search(palavra)) {
                return; // palavra invalida: nao entra na Trie, entao nao entra na BST
            }
            vocabulario = null; // palavra nova
        }

        // 3. Incrementa e propaga para as duas estruturas
        int novaFreq = freqAtual + 1;
        trie.updateFrequency(palavra, novaFreq);
        ranking.addOrUpdate(palavra, novaFreq);

        // 4. Invalida o cache de todos os prefixos da palavra
        invalidateCacheFor(palavra);
    }

    /** Frequencia de uso da palavra (0 se nao existir). */
    public int getFrequencia(String palavra) {
        return trie.getFrequency(palavra);
    }

    /**
     * Sugestoes para o prefixo, da palavra mais usada para a menos usada.
     * A Trie encontra as palavras; a frequencia vem da BST. Empates ficam em
     * ordem alfabetica (a ordenacao e estavel).
     */
    public List<String> sugerir(String prefixo) {
        List<String> sugestoes = trie.suggest(prefixo);
        final Map<String, Integer> freq = new HashMap<>();
        for (String palavra : sugestoes) {
            freq.put(palavra, ranking.getFrequency(palavra));
        }
        sugestoes.sort((p1, p2) -> Integer.compare(freq.get(p2), freq.get(p1)));
        return sugestoes;
    }

    /** As n palavras mais usadas de todo o dicionario (via BST). */
    public List<String> maisFrequentes(int n) {
        return ranking.getTopWords(n);
    }

    // ------------------------------------------------------------------
    // Fase 3: cache e correcao ortografica
    // ------------------------------------------------------------------

    /**
     * Como {@link #sugerir(String)}, mas guarda o resultado em um HashMap; a
     * proxima consulta do mesmo prefixo e O(1) em media. Devolve sempre uma
     * copia, para o chamador nao alterar o cache.
     */
    public List<String> sugerirComCache(String prefixo) {
        if (prefixo == null) {
            return sugerir(null);
        }
        String chave = prefixo.toLowerCase(Locale.ROOT);
        List<String> sugestoes = cache.get(chave);
        if (sugestoes == null) {
            sugestoes = sugerir(chave);
            cache.put(chave, new ArrayList<>(sugestoes));
        }
        return new ArrayList<>(sugestoes);
    }

    /** Indica se ha resultado em cache para o prefixo (usado nos testes). */
    public boolean estaNoCache(String prefixo) {
        return prefixo != null && cache.containsKey(prefixo.toLowerCase(Locale.ROOT));
    }

    /**
     * Palavra do dicionario mais parecida com a digitada (Levenshtein), a
     * distancia de ate {@link #DISTANCIA_MAXIMA_PADRAO}; null se nao houver
     * nenhuma tao parecida (ou se a entrada for null).
     */
    public String corrigir(String palavra) {
        return corrigir(palavra, DISTANCIA_MAXIMA_PADRAO).orElse(null);
    }

    /** Palavra mais parecida a distancia de no maximo maxDistance, se existir. */
    public Optional<String> corrigir(String palavra, int maxDistance) {
        if (palavra == null) {
            return Optional.empty();
        }
        if (vocabulario == null) {
            vocabulario = trie.listarPalavras(); // DFS na Trie, refeita so quando entra palavra nova
        }
        return Levenshtein.encontrarMaisSemelhante(palavra.trim(), vocabulario, maxDistance);
    }

    /** Remove do cache todos os prefixos da palavra (o prefixo vazio tambem). */
    private void invalidateCacheFor(String palavra) {
        cache.remove("");
        for (int i = 1; i <= palavra.length(); i++) {
            cache.remove(palavra.substring(0, i));
        }
    }

    // ------------------------------------------------------------------
    // Fase 4: processamento do arquivo de testes (testes.txt)
    // ------------------------------------------------------------------

    /** Formata sugestoes: "[a, b]"; com frequencias "[a(2), b(0)]" se alguma for maior que 0. */
    private String formatarSugestoes(List<String> lista) {
        boolean comFreq = false;
        for (String p : lista) {
            if (getFrequencia(p) > 0) {
                comFreq = true;
            }
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(lista.get(i));
            if (comFreq) {
                sb.append('(').append(getFrequencia(lista.get(i))).append(')');
            }
        }
        return sb.append(']').toString();
    }

    /** Quebra "[a(2), b]" em itens "a(2)" e "b". */
    private static List<String> itens(String lista) {
        String s = lista.trim();
        if (s.startsWith("[")) {
            s = s.substring(1);
        }
        if (s.endsWith("]")) {
            s = s.substring(0, s.length() - 1);
        }
        List<String> r = new ArrayList<>();
        for (String parte : s.split(",")) {
            if (!parte.trim().isEmpty()) {
                r.add(parte.trim().replace(" ", ""));
            }
        }
        return r;
    }

    /**
     * Compara a lista de sugestoes obtida com a esperada. Sem frequencias na
     * esperada: mesma lista, na mesma ordem. Com frequencias: mesmos itens
     * palavra(freq) e ordem de frequencia decrescente; palavras de mesma
     * frequencia podem sair em qualquer ordem (a propria especificacao lista
     * compra(0) antes de companhia(0), o que nao e alfabetico).
     */
    private boolean sugestoesConferem(List<String> obtida, String esperado) {
        List<String> esp = itens(esperado);
        boolean esperadoComFreq = esperado.contains("(");
        List<String> palavras = new ArrayList<>();
        List<String> comFreq = new ArrayList<>();
        for (String p : obtida) {
            palavras.add(p);
            comFreq.add(p + "(" + getFrequencia(p) + ")");
        }
        if (!esperadoComFreq) {
            return palavras.equals(esp);
        }
        List<String> a = new ArrayList<>(comFreq);
        List<String> b = new ArrayList<>(esp);
        Collections.sort(a);
        Collections.sort(b);
        if (!a.equals(b)) {
            return false;
        }
        for (int i = 1; i < obtida.size(); i++) {
            if (getFrequencia(obtida.get(i - 1)) < getFrequencia(obtida.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executa os comandos de um arquivo de testes (add, use, search, suggest,
     * correct; "# comentario"; "-> saida_esperada" opcional) e escreve o
     * resultado de cada linha em {@code out}. Retorna {aprovados, total}, onde
     * total conta as linhas com saida esperada (e comandos desconhecidos).
     */
    public int[] processarArquivo(String arquivo, PrintStream out) {
        int numero = 0, aprovados = 0, total = 0;
        out.println("=== Processando arquivo de teste: " + arquivo + " ===");
        try (BufferedReader br = abrirLeitor(arquivo)) {
            String linha;
            boolean primeira = true;
            while ((linha = br.readLine()) != null) {
                linha = semBom(linha, primeira);
                primeira = false;
                int h = linha.indexOf('#');
                if (h >= 0) {
                    linha = linha.substring(0, h);
                }
                linha = linha.trim();
                if (linha.isEmpty()) {
                    continue;
                }
                numero++;

                String esperado = null;
                int seta = linha.indexOf("->");
                if (seta >= 0) {
                    esperado = linha.substring(seta + 2).trim();
                    linha = linha.substring(0, seta).trim();
                }
                String[] partes = linha.split("\\s+", 2);
                String cmd = partes[0].toLowerCase();
                String arg = partes.length > 1 ? partes[1].trim() : "";

                String resultado;
                boolean confere = true;
                switch (cmd) {
                    case "add":
                        resultado = adicionar(arg) ? "OK" : "PALAVRA INVALIDA";
                        break;
                    case "use":
                        registrarUso(arg);
                        resultado = buscar(arg) ? "OK (freq: " + getFrequencia(arg) + ")" : "PALAVRA INVALIDA";
                        break;
                    case "search":
                        resultado = String.valueOf(buscar(arg));
                        break;
                    case "suggest": {
                        List<String> sug = sugerirComCache(arg);
                        resultado = formatarSugestoes(sug);
                        if (esperado != null) {
                            confere = sugestoesConferem(sug, esperado);
                        }
                        break;
                    }
                    case "correct": {
                        String c = corrigir(arg);
                        resultado = (c == null) ? "(nenhuma)" : c;
                        break;
                    }
                    default:
                        resultado = "COMANDO DESCONHECIDO";
                        esperado = (esperado == null) ? "" : esperado;
                        confere = false;
                }
                if (esperado != null && !cmd.equals("suggest") && confere) {
                    confere = resultado.equalsIgnoreCase(esperado);
                }

                String rotulo = "[" + numero + "] " + cmd + (arg.isEmpty() ? "" : " " + arg);
                if (esperado == null) {
                    out.println(rotulo + " -> " + resultado);
                } else {
                    total++;
                    if (confere) {
                        aprovados++;
                    }
                    out.println(rotulo + " -> " + resultado + (confere ? " [PASSED]" : " [FAILED] (esperado: " + esperado + ")"));
                }
            }
        } catch (IOException e) {
            out.println("Erro ao ler o arquivo de teste: " + e.getMessage());
            return new int[] {0, 0};
        }
        out.println();
        out.println("=== RESULTADO: " + aprovados + "/" + total + " testes PASSED ===");
        return new int[] {aprovados, total};
    }

    /** Procura palavras.txt ao lado do arquivo de teste, em data/ e no diretorio atual. */
    private static String localizarDicionario(String testes) {
        File pai = new File(testes).getAbsoluteFile().getParentFile();
        String[] candidatos = {
            new File(pai, "palavras.txt").getPath(),
            "data/palavras.txt",
            "palavras.txt"
        };
        for (String c : candidatos) {
            if (new File(c).isFile()) {
                return c;
            }
        }
        return candidatos[0];
    }

    /** {@code java -cp . Dicionario testes.txt [palavras.txt]}; saida 0 se todos passarem. */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java -cp . Dicionario <testes.txt> [palavras.txt]");
            System.exit(2);
            return;
        }
        String palavras = args.length > 1 ? args[1] : localizarDicionario(args[0]);
        if (!new File(args[0]).isFile() || !new File(palavras).isFile()) {
            System.out.println("Erro: arquivo nao encontrado: " + (!new File(args[0]).isFile() ? args[0] : palavras));
            System.exit(2);
            return;
        }
        Dicionario dic = new Dicionario();
        int n = dic.carregar(palavras);
        System.out.println("=== Dicionario Carregado ===");
        System.out.println("Arquivo: " + palavras);
        System.out.println("Palavras carregadas: " + n);
        if (dic.getPalavrasIgnoradas() > 0) {
            System.out.println("Linhas ignoradas (caracteres fora de a-z): " + dic.getPalavrasIgnoradas());
        }
        System.out.println();

        int[] r = dic.processarArquivo(args[0], System.out);
        System.exit(r[1] > 0 && r[0] == r[1] ? 0 : 1);
    }
}
