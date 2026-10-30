
package br.com.avc.avc_cantina.repository;

import br.com.avc.avc_cantina.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmailIgnoreCase(String email);

}
