package br.com.avc.avc_cantina.service;

import br.com.avc.avc_cantina.exception.RecursoNaoEncontradoException;
import br.com.avc.avc_cantina.model.Produto;
import br.com.avc.avc_cantina.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    @Transactional
    public Produto cadastrar(Produto produto) {
        produto.setNome(produto.getNome().trim());
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto buscarPorIdComBloqueio(Long id) {
        return produtoRepository.buscarPorIdComBloqueio(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    @Transactional
    public Produto salvarAlteracoes(Produto produto) {
        return produtoRepository.save(produto);
    }
    
    @Transactional
    public Produto atualizar(Long id, String novoNome, java.math.BigDecimal novoPreco) {
        Produto produto = buscarPorId(id);
        produto.setNome(novoNome.trim());
        produto.setPreco(novoPreco);
        return produtoRepository.save(produto);
    }
}
