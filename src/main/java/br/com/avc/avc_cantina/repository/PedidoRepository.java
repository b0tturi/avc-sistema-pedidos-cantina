
package br.com.avc.avc_cantina.repository;

import br.com.avc.avc_cantina.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findBySituacao(String situacao);

}
