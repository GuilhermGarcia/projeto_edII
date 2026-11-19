import java.util.List;

/**
 * Demonstracao do sistema integrado (secao 8 da especificacao): carrega um
 * dicionario, mostra sugestoes, ranking, correcao e o efeito do cache.
 * Uso: {@code java -cp bin Main [palavras.txt]} (padrao: data/palavras.txt).
 * Os "usos" abaixo sao simulados apenas para demonstrar o ranking.
 */
public class Main {

    public static void main(String[] args) {
        String arquivo = args.length > 0 ? args[0] : "data/palavras.txt";
        Dicionario dic = new Dicionario();
        int n = dic.carregar(arquivo);
        System.out.println("=== Dicionario Carregado ===");
        System.out.println("Palavras carregadas: " + n);

        System.out.println("\n=== Sugestao Simples ===");
        System.out.println("Prefixo: \"comp\"");
        System.out.println("Sugestoes: " + dic.sugerir("comp"));

        // Usos simulados: a 1a sugestao 3 vezes, a 2a 2 vezes, a 3a 1 vez
        List<String> base = dic.sugerir("comp");
        for (int i = 0; i < Math.min(3, base.size()); i++) {
            for (int k = 0; k < 3 - i; k++) {
                dic.registrarUso(base.get(i));
            }
        }
        System.out.println("\n=== Sugestao com Ranking (apos usos simulados) ===");
        System.out.println("Prefixo: \"comp\"");
        for (String p : dic.sugerir("comp")) {
            System.out.println(p + " (freq: " + dic.getFrequencia(p) + ")");
        }

        System.out.println("\n=== Correcao Ortografica ===");
        for (String digitada : new String[] {"computadro", "caza", "cadiera", "xyz"}) {
            String c = dic.corrigir(digitada);
            System.out.println("Palavra digitada: \"" + digitada + "\"");
            if (c == null) {
                System.out.println("Correcao: (nenhuma a distancia <= " + Dicionario.DISTANCIA_MAXIMA_PADRAO + ")");
            } else {
                System.out.println("Correcao: " + c + "  Distancia: " + Levenshtein.levenshtein(digitada, c));
            }
        }

        System.out.println("\n=== Cache de Sugestoes ===");
        long t0 = System.nanoTime();
        dic.sugerirComCache("c");
        long t1 = System.nanoTime();
        dic.sugerirComCache("c");
        long t2 = System.nanoTime();
        System.out.printf("Primeira consulta: %.3f ms%n", (t1 - t0) / 1e6);
        System.out.printf("Segunda consulta (cache): %.3f ms%n", (t2 - t1) / 1e6);
    }
}
