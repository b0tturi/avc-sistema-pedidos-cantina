package br.com.avc.avc_cantina.algoritmos;

import br.com.avc.avc_cantina.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgoritmosProdutoTest {

    private List<Produto> listaOrdenadaPorId;
    private List<Produto> listaDesordenadaPorPreco;

    private Produto criarProduto(Long id, String nome, BigDecimal preco) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome(nome);
        p.setPreco(preco);
        p.setEstoqueDisponivel(10);
        return p;
    }

    @BeforeEach
    void setUp() {
        listaOrdenadaPorId = new ArrayList<>();
        listaOrdenadaPorId.add(criarProduto(1L, "Café", new BigDecimal("4.00")));
        listaOrdenadaPorId.add(criarProduto(2L, "Pão de Queijo", new BigDecimal("6.50")));
        listaOrdenadaPorId.add(criarProduto(5L, "Suco Natural", new BigDecimal("8.00")));
        listaOrdenadaPorId.add(criarProduto(9L, "Salgado Especial", new BigDecimal("10.00")));

        listaDesordenadaPorPreco = new ArrayList<>();
        listaDesordenadaPorPreco.add(criarProduto(1L, "Suco Natural", new BigDecimal("8.00")));
        listaDesordenadaPorPreco.add(criarProduto(2L, "Salgado Especial", new BigDecimal("10.00")));
        listaDesordenadaPorPreco.add(criarProduto(3L, "Café", new BigDecimal("4.00")));
        listaDesordenadaPorPreco.add(criarProduto(4L, "Pão de Queijo", new BigDecimal("6.50")));
    }

    @Test
    @DisplayName("Busca linear deve encontrar produto existente pelo ID")
    void buscaLinearDeveAcharIdExistente() {
        Produto encontrado = AlgoritmosProduto.buscaLinear(listaOrdenadaPorId, 5L);

        assertNotNull(encontrado);
        assertEquals(5L, encontrado.getId());
        assertEquals("Suco Natural", encontrado.getNome());
    }

    @Test
    @DisplayName("Busca linear deve retornar null para ID inexistente")
    void buscaLinearNaoDeveAcharIdInexistente() {
        Produto encontrado = AlgoritmosProduto.buscaLinear(listaOrdenadaPorId, 99L);
        assertNull(encontrado);
    }

    @Test
    @DisplayName("Busca binária deve encontrar produto existente pelo ID")
    void buscaBinariaDeveAcharIdExistente() {
        Produto encontrado = AlgoritmosProduto.buscaBinaria(listaOrdenadaPorId, 2L);

        assertNotNull(encontrado);
        assertEquals(2L, encontrado.getId());
        assertEquals("Pão de Queijo", encontrado.getNome());
    }

    @Test
    @DisplayName("Busca binária deve retornar null para ID inexistente")
    void buscaBinariaNaoDeveAcharIdInexistente() {
        Produto encontrado = AlgoritmosProduto.buscaBinaria(listaOrdenadaPorId, 7L);
        assertNull(encontrado);
    }

    @Test
    @DisplayName("Insertion Sort deve ordenar produtos por preço em ordem crescente")
    void deveOrdenarPorPrecoEConferirOrdem() {
        AlgoritmosProduto.ordenarPorPreco(listaDesordenadaPorPreco);

        assertEquals(new BigDecimal("4.00"), listaDesordenadaPorPreco.get(0).getPreco());
        assertEquals("Café", listaDesordenadaPorPreco.get(0).getNome());

        assertEquals(new BigDecimal("6.50"), listaDesordenadaPorPreco.get(1).getPreco());
        assertEquals("Pão de Queijo", listaDesordenadaPorPreco.get(1).getNome());

        assertEquals(new BigDecimal("8.00"), listaDesordenadaPorPreco.get(2).getPreco());
        assertEquals("Suco Natural", listaDesordenadaPorPreco.get(2).getNome());

        assertEquals(new BigDecimal("10.00"), listaDesordenadaPorPreco.get(3).getPreco());
        assertEquals("Salgado Especial", listaDesordenadaPorPreco.get(3).getNome());
    }
}