
package br.com.avc.avc_cantina.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pedido {

    private Long id;

    private Cliente cliente;

    private Produto produto;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal precoRegistrado;

    @NotNull
    private LocalDateTime dataRegistro;

    @NotNull
    private String situacao = "ATIVO";

    public Pedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getPrecoRegistrado() {
        return precoRegistrado;
    }

    public void setPrecoRegistrado(BigDecimal precoRegistrado) {
        this.precoRegistrado = precoRegistrado;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
