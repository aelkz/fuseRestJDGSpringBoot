/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.blueprint.model;

import de.cronn.reflection.util.PropertyUtils;
import de.cronn.reflection.util.TypedPropertyGetter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="BENEFICIARIO", schema = "CONVENIO")
public class Beneficiario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BENEFICIARIO_SEQ")
    @SequenceGenerator(sequenceName = "beneficiario_seq", allocationSize = 1, name = "BENEFICIARIO_SEQ")
    public Long handle;

    @Column(name = "familia", nullable = false, precision = 0)
    public Long familia;

    @Column(name = "nome", length = 250, nullable = false, precision = 0)
    public String nome;

    @Column(name = "email", length = 250, nullable = false)
    public String email;

    @Column(name = "nu_cpf", length = 11, nullable = false)
    public String cpf;

    @Column(name = "nu_cartao", length = 20, nullable = false)
    public String cartao;

    @Column(name = "nu_contrato", length = 20, nullable = false)
    public String contrato;

    @Column(name = "dt_adesao", nullable = true)
    public Date dataAdesao;

    public Long getHandle() {
        return handle;
    }

    public void setHandle(Long handle) {
        this.handle = handle;
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

    public Date getDataAdesao() {
        return dataAdesao;
    }

    public void setDataAdesao(Date dataAdesao) {
        this.dataAdesao = dataAdesao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public Long getFamilia() { return familia; }

    public void setFamilia(Long familia) { this.familia = familia; }

    public String getCartao() { return cartao; }

    public void setCartao(String cartao) { this.cartao = cartao; }

    public Beneficiario() { }

    public Beneficiario(Long familia, String nome, String email, String cpf, String cartao, String contrato, Date dataAdesao) {
        this.familia = familia;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.cartao = cartao;
        this.contrato = contrato;
        this.dataAdesao = dataAdesao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beneficiario that = (Beneficiario) o;
        return getHandle().equals(that.getHandle()) &&
                Objects.equals(getFamilia(), that.getFamilia()) &&
                getCpf().equals(that.getCpf()) &&
                Objects.equals(getCartao(), that.getCartao()) &&
                getContrato().equals(that.getContrato());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHandle(), getFamilia(), getCpf(), getCartao(), getContrato());
    }

    // Utility method to retrieve entity property names (using java reflection)
    // https://github.com/cronn-de/reflection-util

    @Transient
    public static String getJPAColumnName(TypedPropertyGetter<Beneficiario,?> typedProperty) {
        return PropertyUtils.getPropertyName(Beneficiario.class, typedProperty);
    }

}
