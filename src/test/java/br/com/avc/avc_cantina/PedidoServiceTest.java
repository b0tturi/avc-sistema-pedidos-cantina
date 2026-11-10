package br.com.avc.avc_cantina;

import br.com.avc.avc_cantina.exception.ConflitoException;
import br.com.avc.avc_cantina.exception.RegraNegocioException;
import br.com.avc.avc_cantina.model.Cliente;
import br.com.avc.avc_cantina.model.Pedido;
import br.com.avc.avc_cantina.model.Produto;
import br.com.avc.avc_cantina.repository.PedidoRepository;
import br.com.avc.avc_cantina.service.ClienteService;
import br.com.avc.avc_cantina.service.PedidoService;
import br.com.avc.avc_cantina.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProdutoService produtoService;

    private PedidoService pedidoService;

    @BeforeEach
    void configurar() {
        pedidoService = new PedidoService(pedidoRepository, clienteService, produtoService);
    }

    @Test
    void deveCriarPedidoEDecrementarEstoque() {
        Cliente cliente = new Cliente("Ana Lima", "ana@example.com");
        Produto produto = new Produto("Cafe", new BigDecimal("4.50"), 10);
        when(clienteService.buscarPorId(1L)).thenReturn(cliente);
        when(produtoService.buscarPorIdComBloqueio(1L)).thenReturn(produto);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Pedido resultado = pedidoService.cadastrar(1L, 1L);

        assertEquals(9, produto.getEstoqueDisponivel());
        assertEquals(new BigDecimal("4.50"), resultado.getPrecoRegistrado());
        assertEquals("ATIVO", resultado.getSituacao());
        verify(produtoService).salvarAlteracoes(produto);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void naoDeveCriarPedidoSemEstoque() {
        Cliente cliente = new Cliente("Ana Lima", "ana@example.com");
        Produto produto = new Produto("Bolo", new BigDecimal("8.50"), 0);
        when(clienteService.buscarPorId(1L)).thenReturn(cliente);
        when(produtoService.buscarPorIdComBloqueio(1L)).thenReturn(produto);

        assertThrows(ConflitoException.class, () -> pedidoService.cadastrar(1L, 1L));
        verify(produtoService, never()).salvarAlteracoes(any(Produto.class));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveCancelarPedidoEDevolverEstoque() {
        Produto produto = new Produto("Cafe", new BigDecimal("4.50"), 9);
        produto.setId(1L);
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setProduto(produto);
        pedido.setSituacao("ATIVO");
        when(pedidoRepository.buscarPorIdComBloqueio(5L)).thenReturn(Optional.of(pedido));
        when(produtoService.buscarPorIdComBloqueio(any())).thenReturn(produto);
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido resultado = pedidoService.cancelar(5L);

        assertEquals("CANCELADO", resultado.getSituacao());
        assertEquals(10, produto.getEstoqueDisponivel());
        verify(produtoService).salvarAlteracoes(produto);
    }

    @Test
    void naoDeveCancelarPedidoDuasVezes() {
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setSituacao("CANCELADO");
        when(pedidoRepository.buscarPorIdComBloqueio(5L)).thenReturn(Optional.of(pedido));

        assertThrows(ConflitoException.class, () -> pedidoService.cancelar(5L));
        verify(produtoService, never()).buscarPorIdComBloqueio(any());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveRejeitarSituacaoDesconhecida() {
        assertThrows(RegraNegocioException.class, () -> pedidoService.listarPorSituacao("PENDENTE"));
        verify(pedidoRepository, never()).findBySituacao(anyString());
    }
}
