package ARVTRIE;

public class NodeTrie{

    public NodeTrie[] criancas; // guarda o alfabeto

    public boolean fimDaPalavra; // indica o fim da palavra

    //guarda qnts vezes a palavra foi usada
    public int frequencia;

    //Construtor do no
    public NodeTrie(){
        //aloca 26 posicoes para letras no construtor
        this.criancas = new NodeTrie[26];
        this.fimDaPalavra = false;
        this.frequencia = 0;
    }
}