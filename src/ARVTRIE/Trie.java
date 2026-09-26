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
    }
