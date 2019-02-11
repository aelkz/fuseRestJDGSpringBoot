package com.aelkz.blueprint.service;

import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.service.dto.BeneficiarioDTO;

import java.util.List;

public interface BeneficiarioService {

    List<Beneficiario> findAll();

    List<Beneficiario> findAll(BeneficiarioDTO filter);

    Beneficiario findOne(Long handle);

}
