package ARVTRIE;

import ARVBIN.BSTWordRanking;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

//BGR

public class Dicionario {
    
    private Trie trie;
    private BSTWordRanking ranking;

    public Dicionario() {
        trie = new Trie();
        ranking = new BSTWordRanking();
    }

    // ==========================================================
    // MÉTODOS DA FASE 1: LEITURA E BUSCA
    // ==========================================================
    
    public void carregar(String arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim().toLowerCase();
                if (!linha.isEmpty()) {
                    // Insere na Trie com frequencia 0 (padrao do construtor NodeTrie)
                    trie.inserir(linha);
                    // Registra na BST com frequencia 0
                    ranking.addOrUpdate(linha, 0);
                }
            }
            System.out.println("Dicionario carregado com sucesso a partir de: " + arquivo);
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public boolean buscar(String palavra) {
        return trie.buscar(palavra);
    }

    // ==========================================================
    // MÉTODOS DA FASE 2: RANKING E ATUALIZAÇÃO
    // ==========================================================
    
    public void registrarUso(String palavra) {
        palavra = palavra.toLowerCase();
        
        // 1. Descobre a frequencia atual (a partir da Trie)
        int freqAtual = trie.getFrequency(palavra);
        
        // 2. Se a palavra nao existe, insere com frequencia 0
        if (freqAtual == 0 && !trie.buscar(palavra)) {
            trie.inserir(palavra);
        }
        
        // 3. Incrementa e propaga para as duas estruturas
        int novaFreq = freqAtual + 1;
        trie.updateFrequency(palavra, novaFreq);
        ranking.addOrUpdate(palavra, novaFreq);
    }

    public List<String> sugerir(String prefixo) {
        // Pega todas as sugestões da Trie (neste ponto, estão em ordem alfabética)
        List<String> sugestoes = trie.sugerir(prefixo);
        
        // Ordena a lista de sugestões com base na frequência usando a BST
        sugestoes.sort((p1, p2) -> {
            int freq1 = ranking.getFrequency(p1);
            int freq2 = ranking.getFrequency(p2);
            // Ordem decrescente (do mais frequente para o menos frequente)
            return Integer.compare(freq2, freq1);
        });
        
        return sugestoes;
    }
}