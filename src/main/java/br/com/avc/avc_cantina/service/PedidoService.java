package br.com.avc.avc_cantina.service;

import br.com.avc.avc_cantina.exception.ConflitoException;
import br.com.avc.avc_cantina.exception.RecursoNaoEncontradoException;
import br.com.avc.avc_cantina.exception.RegraNegocioException;
import br.com.avc.avc_cantina.model.Cliente;
import br.com.avc.avc_cantina.model.Pedido;
import br.com.avc.avc_cantina.model.Produto;
import br.com.avc.avc_cantina.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PedidoService {

    private static final String ATIVO = "ATIVO";
    private static final String CANCELADO = "CANCELADO";
    private static final Set<String> SITUACOES_VALIDAS = Set.of(ATIVO, CANCELADO);

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService, ProdutoService produtoService) {
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
        String situacaoNormalizada = situacao.trim().toUpperCase(Locale.ROOT);
        if (!SITUACOES_VALIDAS.contains(situacaoNormalizada)) {
            throw new RegraNegocioException("Situação inválida. Utilize ATIVO ou CANCELADO.");
        }
        return pedidoRepository.findBySituacao(situacaoNormalizada);
    }

    @Transactional
    public Pedido cadastrar(Long clienteId, Long produtoId) {
        if (clienteId == null || clienteId <= 0 || produtoId == null || produtoId <= 0) {
            throw new RegraNegocioException("Identificador de cliente e produto são obrigatórios.");
        }

        Cliente cliente = clienteService.buscarPorId(clienteId);
        Produto produto = produtoService.buscarPorIdComBloqueio(produtoId);

        if (produto.getEstoqueDisponivel() <= 0) {
            throw new ConflitoException("Produto sem estoque disponível.");
        }

        produto.setEstoqueDisponivel(produto.getEstoqueDisponivel() - 1);
        produtoService.salvarAlteracoes(produto);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProduto(produto);
        pedido.setPrecoRegistrado(produto.getPreco());
        pedido.setDataRegistro(LocalDateTime.now());
        pedido.setSituacao(ATIVO);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelar(Long pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorIdComBloqueio(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + pedidoId));

        if (CANCELADO.equals(pedido.getSituacao())) {
            throw new ConflitoException("Pedido já se encontra cancelado.");
        }

        Produto produto = produtoService.buscarPorIdComBloqueio(pedido.getProduto().getId());
        produto.setEstoqueDisponivel(produto.getEstoqueDisponivel() + 1);
        produtoService.salvarAlteracoes(produto);

        pedido.setSituacao(CANCELADO);
        return pedidoRepository.save(pedido);
    }
}
