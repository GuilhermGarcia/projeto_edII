package  ARVTRIE;

import java.util.ArrayList;
import java.util.List;

public class Trie{

    private NodeTrie raiz;

    public Trie(){ // construtor
        this.raiz = new NodeTrie();
    }
    //iserir palavra na trie
    public void inserir (String palavra){
        if(palavra == null){//caso vazio
            return;
        }

        NodeTrie atual = raiz;
        palavra = palavra.toLowerCase(); //deixa td minusculo

        //laço para percorre a palavra
        for (int i =0; i< palavra.length();i++){
        //charAt()= metodo para retornar o i
         char letra = palavra.charAt(i);//extrai o caracter i
         int indice=letra- 'a';// descobre possição (0 a 25)

         if(indice <0 || indice >=26){ // impede valor inválido
            continue;
         }

         //caso não exista nó para essa letra, cria outro
         if (atual.criancas[indice] == null){
             atual.criancas[indice] = new NodeTrie();
            
         }
         //avanca ate o no da letra que queremos
         atual = atual.criancas[indice];

        }
        //marca o ultimo nó que forma a palavra
        atual.fimDaPalavra=true;

        }

    public  boolean buscar(String palavra){
        if (palavra == null){ // se palavra vazia
            return false;
        }

        NodeTrie atual = raiz;
        palavra=palavra.toLowerCase();

        //percorre a trie buscando a letra e a posição
        for (int i =0; i<palavra.length();i++){
            char letra = palavra.charAt(i);
            int indice = letra -'a';


        // se o caminho da letra nao existir, 
        if (indice <0 || indice >=26 || atual.criancas[indice]==null){
            return false;
        }

        //Avanca para o nó da letra
        atual = atual.criancas[indice];
    }
    return atual.fimDaPalavra; //retorna true se for o fim
}


    public boolean comecaCom(String prefixo){
        if (prefixo==null){
            return  false;
        }

        NodeTrie atual =raiz;
        prefixo = prefixo.toLowerCase();

        for (int i = 0; i < prefixo.length(); i++) {
            char letra = prefixo.charAt(i);
            int indice = letra - 'a';

            // Se o caminho do prefixo quebrar, nao existe nenhuma palavra com esse inicio
            if (indice < 0 || indice >= 26 || atual.criancas[indice] == null) {
                return false;
            }

            // Avança para o nó do prefixo
            atual = atual.criancas[indice];
        }

        // Se conseguiu percorrer todas as letras do prefixo até o fim, retorna true
        return true;
    }
    
    void coletarPalavras(NodeTrie atual, StringBuilder prefixo, List<String> sugestoes){
    	//se letra tiver a flag fimDaPalavra, adicionar a palavra a lista de sugestões
    	if(atual.fimDaPalavra) {
    		sugestoes.add(prefixo.toString());
    	}
    	
    	//para cada letra no nó chamar, a função coletarPalavras
    	for (int i = 0; i < atual.criancas.length; i++) {
    		 if (atual.criancas[i] != null) {
	    		// encontrar a letra correspondente no ASCII
	   			char letra = (char) ('a' + i);
	   			
		        prefixo.append(letra);
		      
		        coletarPalavras(atual.criancas[i], prefixo, sugestoes);
		        
		        // resetar prefixo para proxima iteração do loop
		        prefixo.deleteCharAt(prefixo.length() - 1);
			}
    	}
    }
    
    public List<String> sugerir(String prefixo){
    	// inicializar lista responsavel por guardar as palavras sugeridas
    	List<String> sugestoes =  new ArrayList<>();
    	
    	//caso o prefixo seja nulo, retornar lista vazia
    	if (prefixo==null){
            return sugestoes;
        }
    	
    	NodeTrie atual = raiz;
        prefixo = prefixo.toLowerCase();
        
        // loop for responsavel por posicionar o nó no mesmo ponto que o prefixo
        for (int i = 0; i < prefixo.length(); i++) {
            char letra = prefixo.charAt(i);
            
            //determinar o indice corresponde a letra
            int indice = letra - 'a';
            
            // caso o indice corresponda a um caractere invalido retornar lista vazia
            if (indice < 0 ||indice >= 26 ||atual.criancas[indice] == null) {
                return sugestoes;
            }
            //mover no atual para a proxima letra no prefixo
            atual = atual.criancas[indice];
        }
        
        //chamar função responsavel por montar lista de sugestoes
        coletarPalavras(atual,new StringBuilder(prefixo),sugestoes);

        return sugestoes;
        
    }
}