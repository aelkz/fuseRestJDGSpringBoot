package com.aelkz.blueprint.service.dto;

import java.util.Date;

public class BeneficiarioDTO {

    private Long handle;
    private Long familia;
    private String nome;
    private String email;
    private String cpf;
    private String cartao;
    private String contrato;
    private Date dataAdesao;

    public BeneficiarioDTO() { }

    public BeneficiarioDTO(Long handle, Long familia, String nome, String email, String cpf, String cartao, String contrato, Date dataAdesao) {
        this.handle = handle;
        this.familia = familia;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.cartao = cartao;
        this.contrato = contrato;
        this.dataAdesao = dataAdesao;
    }

    public Long getHandle() {
        return handle;
    }

    public void setHandle(Long handle) {
        this.handle = handle;
    }

    public Long getFamilia() {
        return familia;
    }

    public void setFamilia(Long familia) {
        this.familia = familia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCartao() {
        return cartao;
    }

    public void setCartao(String cartao) {
        this.cartao = cartao;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public Date getDataAdesao() {
        return dataAdesao;
    }

    public void setDataAdesao(Date dataAdesao) {
        this.dataAdesao = dataAdesao;
    }
}
