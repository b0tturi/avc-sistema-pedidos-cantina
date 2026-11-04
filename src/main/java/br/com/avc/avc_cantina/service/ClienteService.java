package br.com.avc.avc_cantina.service;

import br.com.avc.avc_cantina.exception.ConflitoException;
import br.com.avc.avc_cantina.exception.RecursoNaoEncontradoException;
import br.com.avc.avc_cantina.model.Cliente;
import br.com.avc.avc_cantina.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        String emailNormalizado = cliente.getEmail().trim().toLowerCase(Locale.ROOT);
        if (clienteRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new ConflitoException("Já existe um cliente cadastrado com este e-mail.");
        }
        cliente.setNome(cliente.getNome().trim());
        cliente.setEmail(emailNormalizado);
        return clienteRepository.save(cliente);
    }
}
