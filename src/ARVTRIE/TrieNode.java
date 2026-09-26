package ARVBIN;

public class TrieNode{

    public NoTrie[] criancas; // guarda o alfabeto

    public boolean fimDaPalavra; // indica o fim da palavra

    //guarda qnts vezes a palavra foi usada
    public int frequencia;

    //Construtor do no
    public NoTrie(){
        //aloca 26 posicoes para letras no construtor
        this.criancas = new NoTrie[26];
        this.fimDaPalavra = false;
        this.frequencia = 0;
    }
}