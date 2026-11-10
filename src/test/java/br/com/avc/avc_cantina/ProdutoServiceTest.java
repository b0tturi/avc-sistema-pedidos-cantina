package br.com.avc.avc_cantina;

import br.com.avc.avc_cantina.exception.ConflitoException;
import br.com.avc.avc_cantina.model.Produto;
import br.com.avc.avc_cantina.repository.PedidoRepository;
import br.com.avc.avc_cantina.repository.ProdutoRepository;
import br.com.avc.avc_cantina.service.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve lançar ConflitoException ao tentar excluir produto com histórico de pedidos")
    void deveLancarExcecaoAoExcluirProdutoComHistorico() {
        Long produtoId = 1L;
        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setNome("Salgado");
        produto.setPreco(new BigDecimal("8.50"));
        produto.setEstoqueDisponivel(10);

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(pedidoRepository.existsByProdutoId(produtoId)).thenReturn(true);

        assertThrows(ConflitoException.class, () -> produtoService.excluir(produtoId));

        verify(produtoRepository, never()).deleteById(anyLong());
    }
}