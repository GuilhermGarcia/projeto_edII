package  ARVTRIE;

import java.util.ArrayList;
import java.util.List;

/**
 * Arvore de prefixos para armazenar palavras formadas pelas letras de 'a' a
 * 'z'. Alem da busca exata, permite localizar rapidamente o no de um prefixo
 * e enumerar todas as palavras abaixo dele.
 */
public class Trie{

    private NodeTrie raiz;

    /** Cria a raiz vazia da Trie. A raiz nao representa nenhuma letra. */
    public Trie(){
        this.raiz = new NodeTrie();
    }

    /**
     * Insere uma palavra, criando os nos que ainda nao existem.
     * Caracteres fora de 'a' a 'z' nao possuem uma posicao na estrutura e sao
     * ignorados durante a montagem do caminho.
     */
    public void inserir (String palavra){
        if(palavra == null){
            return;
        }

        NodeTrie atual = raiz;
        palavra = palavra.toLowerCase(); //deixa td minusculo

        // Cada nivel representa uma letra do caminho da palavra.
        for (int i =0; i< palavra.length();i++){
         char letra = palavra.charAt(i);
         int indice=letra- 'a';

         if(indice <0 || indice >=26){
            continue;
         }

         if (atual.criancas[indice] == null){
             atual.criancas[indice] = new NodeTrie();
         }
         atual = atual.criancas[indice];
        }

        // Somente o no final recebe a marca de palavra completa.
        atual.fimDaPalavra=true;
        }

    /** Retorna true somente quando todo o caminho existe e termina uma palavra. */
    public  boolean buscar(String palavra){
        if (palavra == null){
            return false;
        }

        NodeTrie atual = raiz;
        palavra=palavra.toLowerCase();

        for (int i =0; i<palavra.length();i++){
            char letra = palavra.charAt(i);
            int indice = letra -'a';

        if (indice <0 || indice >=26 || atual.criancas[indice]==null){
            return false;
        }

        atual = atual.criancas[indice];
    }
    return atual.fimDaPalavra;
}

    /** Indica se existe ao menos uma palavra cujo inicio e o prefixo informado. */
    public boolean comecaCom(String prefixo){
        if (prefixo==null){
            return  false;
        }

        NodeTrie atual =raiz;
        prefixo = prefixo.toLowerCase();

        for (int i = 0; i < prefixo.length(); i++) {
            char letra = prefixo.charAt(i);
            int indice = letra - 'a';

            if (indice < 0 || indice >= 26 || atual.criancas[indice] == null) {
                return false;
            }

            atual = atual.criancas[indice];
        }

        return true;
    }

    /**
     * Percorre a subarvore em ordem alfabetica e acrescenta suas palavras a
     * lista. O StringBuilder e reutilizado: cada chamada desfaz o caractere
     * acrescentado antes de voltar ao nivel anterior da recursao.
     */
    void coletarPalavras(NodeTrie atual, StringBuilder prefixo, List<String> sugestoes){
    	if(atual.fimDaPalavra) {
    		sugestoes.add(prefixo.toString());
    	}
    	
    	for (int i = 0; i < atual.criancas.length; i++) {
    		 if (atual.criancas[i] != null) {
	   			char letra = (char) ('a' + i);
	   			
		        prefixo.append(letra);
		      
		        coletarPalavras(atual.criancas[i], prefixo, sugestoes);
		        
		        prefixo.deleteCharAt(prefixo.length() - 1);
			}
    	}
    }

    /** Retorna todas as palavras que comecam com o prefixo, em ordem alfabetica. */
    public List<String> sugerir(String prefixo){
    	List<String> sugestoes =  new ArrayList<>();
    	
    	if (prefixo==null){
            return sugestoes;
        }
    	
    	NodeTrie atual = raiz;
        prefixo = prefixo.toLowerCase();
        
        for (int i = 0; i < prefixo.length(); i++) {
            char letra = prefixo.charAt(i);
            int indice = letra - 'a';
            
            if (indice < 0 ||indice >= 26 ||atual.criancas[indice] == null) {
                return sugestoes;
            }
            atual = atual.criancas[indice];
        }
        
        coletarPalavras(atual,new StringBuilder(prefixo),sugestoes);

        return sugestoes;
        
    }

    /** Retorna a frequencia da palavra, ou zero se ela nao existir. */
    public int getFrequency(String word) {
        NodeTrie node = traverseTo(word);
        return (node != null && node.fimDaPalavra) ? node.frequencia : 0;
    }

    /** Atualiza a frequencia somente se o caminho representar uma palavra. */
    public void updateFrequency(String word, int frequency) {
        NodeTrie node = traverseTo(word);
        if (node != null && node.fimDaPalavra) {
            node.frequencia = frequency;
        }
    }

    /** Localiza o no final do caminho de uma palavra, sem exigir que seja final. */
    private NodeTrie traverseTo(String word) {
        if (word == null) return null;
        
        NodeTrie atual = raiz;
        word = word.toLowerCase();

        for (int i = 0; i < word.length(); i++) {
            char letra = word.charAt(i);
            int indice = letra - 'a';

            if (indice < 0 || indice >= 26 || atual.criancas[indice] == null) {
                return null;
            }
            atual = atual.criancas[indice];
        }
        return atual;
    }
}