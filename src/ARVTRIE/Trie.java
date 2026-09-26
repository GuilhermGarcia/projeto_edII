package  ARVBIN;

private class Trie{

    private NodeTrie raiz;

    public Trie(){ // construtor
        this.raiz = new NodeTrie();
    }
    //iserir palavra na trie
    public void inserir (string palavra){
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
             atual.crianca[indice] = new NodeTrie();
            
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
        if (indice <0 || indice >=26 || atual.crianca[indice]==null){
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
}
