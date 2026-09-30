package ARVTRIE;

/**
 * No utilizado pela {@link Trie}.
 *
 * <p>Cada posicao de {@code criancas} representa uma letra de 'a' a 'z'.
 * Um no pode fazer parte do caminho de uma palavra sem ser, por si so, o
 * final de uma palavra; por isso {@code fimDaPalavra} e necessario.</p>
 */
public class NodeTrie{

    /** Filhos indexados pela letra: 'a' ocupa 0 e 'z' ocupa 25. */
    public NodeTrie[] criancas;

    /** Indica se o caminho ate este no forma uma palavra completa. */
    public boolean fimDaPalavra;

    /** Quantidade de usos registrada para a palavra que termina neste no. */
    public int frequencia;

    /** Cria um no vazio, sem filhos e sem palavra terminando nele. */
    public NodeTrie(){
        this.criancas = new NodeTrie[26];
        this.fimDaPalavra = false;
        this.frequencia = 0;
    }
}