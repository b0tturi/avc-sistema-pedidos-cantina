
package br.com.avc.avc_cantina.service;

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
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado: " + id));
    }

    @Transactional
    public Produto cadastrar(Produto produto) {
        return produtoRepository.save(produto);
    }
}
