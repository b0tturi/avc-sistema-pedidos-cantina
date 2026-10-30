
package br.com.avc.avc_cantina.repository;

import br.com.avc.avc_cantina.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}
