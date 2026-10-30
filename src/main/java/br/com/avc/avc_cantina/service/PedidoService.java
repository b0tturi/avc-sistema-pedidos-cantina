
package br.com.avc.avc_cantina.service;

import br.com.avc.avc_cantina.model.Cliente;
import br.com.avc.avc_cantina.model.Pedido;
import br.com.avc.avc_cantina.model.Produto;
import br.com.avc.avc_cantina.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteService clienteService,
            ProdutoService produtoService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorSituacao(String situacao) {
        return pedidoRepository.findBySituacao(situacao);
    }

    @Transactional
    public Pedido cadastrar(Long clienteId, Long produtoId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        Produto produto = produtoService.buscarPorId(produtoId);

        if (produto.getEstoqueDisponivel() <= 0) {
            throw new IllegalArgumentException(
                    "Produto sem estoque disponível.");
        }

        produto.setEstoqueDisponivel(
                produto.getEstoqueDisponivel() - 1);

        produtoService.cadastrar(produto);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProduto(produto);
        pedido.setPrecoRegistrado(produto.getPreco());
        pedido.setDataRegistro(LocalDateTime.now());
        pedido.setSituacao("ATIVO");

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pedido não encontrado: " + pedidoId));

        if ("CANCELADO".equals(pedido.getSituacao())) {
            throw new IllegalArgumentException(
                    "Este pedido já foi cancelado.");
        }

        Produto produto = pedido.getProduto();
        produto.setEstoqueDisponivel(
                produto.getEstoqueDisponivel() + 1);

        produtoService.cadastrar(produto);

        pedido.setSituacao("CANCELADO");

        return pedidoRepository.save(pedido);
    }
}

