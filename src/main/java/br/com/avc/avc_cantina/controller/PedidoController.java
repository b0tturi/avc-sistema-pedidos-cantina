
package br.com.avc.avc_cantina.controller;

import br.com.avc.avc_cantina.model.Pedido;
import br.com.avc.avc_cantina.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listarTodos(
            @RequestParam(required = false) String situacao) {

        if (situacao != null && !situacao.isBlank()) {
            return pedidoService.listarPorSituacao(situacao.toUpperCase());
        }

        return pedidoService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido cadastrar(@RequestBody NovoPedido dados) {
        return pedidoService.cadastrar(
                dados.clienteId(),
                dados.produtoId()
        );
    }

    @PatchMapping("/{id}/cancelamento")
    public Pedido cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
    }

    public record NovoPedido(Long clienteId, Long produtoId) {
    }
}
