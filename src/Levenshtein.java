import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Distancia de Levenshtein (programacao dinamica) e busca da palavra mais
 * parecida em um vocabulario (Fase 3).
 */
public final class Levenshtein {

    private Levenshtein() {
    }

    /**
     * Numero minimo de insercoes, remocoes e substituicoes para transformar
     * uma palavra na outra, sem diferenciar maiusculas de minusculas.
     * Tempo O(m*n); usa duas linhas da matriz, entao memoria O(min(m,n)).
     */
    public static int levenshtein(String a, String b) {
        Objects.requireNonNull(a, "a primeira palavra nao pode ser nula");
        Objects.requireNonNull(b, "a segunda palavra nao pode ser nula");

        String menor = a.toLowerCase(Locale.ROOT);
        String maior = b.toLowerCase(Locale.ROOT);
        if (menor.length() > maior.length()) {
            String tmp = menor;
            menor = maior;
            maior = tmp;
        }

        int[] anterior = new int[menor.length() + 1];
        int[] atual = new int[menor.length() + 1];
        for (int j = 0; j <= menor.length(); j++) {
            anterior[j] = j;
        }

        for (int i = 1; i <= maior.length(); i++) {
            atual[0] = i;
            for (int j = 1; j <= menor.length(); j++) {
                if (maior.charAt(i - 1) == menor.charAt(j - 1)) {
                    atual[j] = anterior[j - 1];
                } else {
                    atual[j] = 1 + Math.min(anterior[j - 1], Math.min(anterior[j], atual[j - 1]));
                }
            }
            int[] tmp = anterior;
            anterior = atual;
            atual = tmp;
        }
        return anterior[menor.length()];
    }

    /** Sinonimo de {@link #levenshtein(String, String)}. */
    public static int calcularDistancia(String a, String b) {
        return levenshtein(a, b);
    }

    /**
     * Retorna a palavra do vocabulario de menor distancia, desde que a
     * distancia seja no maximo {@code maxDistance}; empates saem em ordem
     * alfabetica. Candidatas cujo comprimento difere mais que maxDistance sao
     * podadas sem calcular a distancia (a distancia nunca e menor que a
     * diferenca de comprimentos). Vazio se nenhuma candidata servir.
     */
    public static Optional<String> encontrarMaisSemelhante(String palavra, List<String> vocabulario, int maxDistance) {
        Objects.requireNonNull(palavra, "a palavra consultada nao pode ser nula");
        Objects.requireNonNull(vocabulario, "o vocabulario nao pode ser nulo");

        String consulta = palavra.toLowerCase(Locale.ROOT);
        String melhor = null;
        int limite = maxDistance; // melhor distancia aceitavel ate agora

        for (String candidata : vocabulario) {
            String c = candidata.toLowerCase(Locale.ROOT);
            if (Math.abs(c.length() - consulta.length()) > limite) {
                continue; // poda: nem calcula a distancia
            }
            int d = levenshtein(consulta, c);
            if (d < limite || (d == limite && (melhor == null || c.compareTo(melhor) < 0))) {
                limite = d;
                melhor = c;
            }
        }
        return Optional.ofNullable(melhor);
    }
}
