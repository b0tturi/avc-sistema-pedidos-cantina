
package br.com.avc.avc_cantina.service;

import br.com.avc.avc_cantina.model.Cliente;
import br.com.avc.avc_cantina.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cliente não encontrado: " + id));
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        if (clienteRepository.existsByEmailIgnoreCase(cliente.getEmail())) {
            throw new IllegalArgumentException(
                    "Já existe um cliente cadastrado com este e-mail.");
        }

        return clienteRepository.save(cliente);
    }
}
