package com.aelkz.blueprint.repository;

import com.aelkz.blueprint.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiarioRepository extends CrudRepository<Beneficiario, Long>, JpaSpecificationExecutor { }
