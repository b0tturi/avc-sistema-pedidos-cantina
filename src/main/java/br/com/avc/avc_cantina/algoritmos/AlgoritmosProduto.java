package br.com.avc.avc_cantina.algoritmos;

import br.com.avc.avc_cantina.model.Produto;
import java.util.List;

public class AlgoritmosProduto {

    // Busca linear por ID: O(n)
    public static Produto buscaLinear(List<Produto> produtos, long id) {
        if (produtos == null) return null;
        for (Produto p : produtos) {
            if (p != null && p.getId() != null && p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Busca binária por ID: O(log n). A lista precisa estar previamente ordenada por ID.
    public static Produto buscaBinaria(List<Produto> produtosOrdenadosPorId, long id) {
        if (produtosOrdenadosPorId == null || produtosOrdenadosPorId.isEmpty()) return null;

        int ini = 0;
        int fim = produtosOrdenadosPorId.size() - 1;

        while (ini <= fim) {
            int meio = ini + (fim - ini) / 2;
            Produto pMeio = produtosOrdenadosPorId.get(meio);

            if (pMeio == null || pMeio.getId() == null) {
                return null;
            }

            long idMeio = pMeio.getId();

            if (idMeio == id) {
                return pMeio;
            }
            if (idMeio < id) {
                ini = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return null;
    }

    // Ordenação por preço (Insertion Sort): O(n²) no pior caso
    public static void ordenarPorPreco(List<Produto> produtos) {
        if (produtos == null || produtos.size() <= 1) return;

        for (int i = 1; i < produtos.size(); i++) {
            Produto chave = produtos.get(i);
            int j = i - 1;

            while (j >= 0 && produtos.get(j).getPreco().compareTo(chave.getPreco()) > 0) {
                produtos.set(j + 1, produtos.get(j));
                j--;
            }
            produtos.set(j + 1, chave);
        }
    }
}