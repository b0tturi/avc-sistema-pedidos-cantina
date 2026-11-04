
package br.com.avc.avc_cantina.controller;

import br.com.avc.avc_cantina.model.Pedido;
import br.com.avc.avc_cantina.service.PedidoService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

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
            return pedidoService.listarPorSituacao(situacao.toUpperCase(Locale.ROOT));
        }

        return pedidoService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido cadastrar(@Valid @RequestBody NovoPedido dados) {
        return pedidoService.cadastrar(
                dados.clienteId(),
                dados.produtoId()
        );
    }

    @PatchMapping("/{id}/cancelamento")
    public Pedido cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
    }

    public record NovoPedido(
            @NotNull(message = "O ID do cliente é obrigatório") @Positive(message = "O ID do cliente deve ser positivo") Long clienteId,
            @NotNull(message = "O ID do produto é obrigatório") @Positive(message = "O ID do produto deve ser positivo") Long produtoId) {
    }
}
