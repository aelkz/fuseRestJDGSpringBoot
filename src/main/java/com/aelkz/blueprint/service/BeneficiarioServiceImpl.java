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

package com.aelkz.blueprint.service;

import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.repository.BeneficiarioRepository;
import com.aelkz.blueprint.service.dto.BeneficiarioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("beneficiarioService")
@Transactional
public class BeneficiarioServiceImpl implements BeneficiarioService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficiarioServiceImpl.class);

    @Autowired
    private BeneficiarioRepository repository;

    public List<Beneficiario> findAll() {

        List<Beneficiario> result = new ArrayList<>();
        repository.findAll().forEach(result::add);

       return result.size() > 0 ? result : Collections.EMPTY_LIST;
    }

    /**
     * PS. Validation of parameters could be implemented directly in a upper layer, i.e CxfEndpointProcessor.validateRequest method.
     * @param filter
     * @return
     */
    @Override
    public List<Beneficiario> findAll(BeneficiarioDTO filter) {

        List<Beneficiario> list = repository.findAll(new Specification<Beneficiario>() {

            @Override
            public Predicate toPredicate(Root<Beneficiario> root, CriteriaQuery< ?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (filter.getHandle() != null && filter.getHandle() > 0) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getHandle)), filter.getHandle()));
                }

                if (filter.getFamilia() != null && filter.getFamilia() > 0) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getFamilia)), filter.getFamilia()));
                }

                if (filter.getNome() != null && filter.getNome().length() > 0) {
                    predicates.add(cb.like(cb.lower(root.get(Beneficiario.getJPAColumnName(Beneficiario::getNome))),
                            "%" + filter.getNome().toLowerCase() + "%"));
                }

                if (filter.getEmail() != null && filter.getEmail().length() > 0) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getEmail)), filter.getEmail()));
                }

                if (filter.getCpf() != null && filter.getCpf().length() > 0) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getCpf)), filter.getCpf()));
                }

                if (filter.getCartao() != null && filter.getCartao().length() > 0) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getCartao)), filter.getCartao()));
                }

                if (filter.getContrato() != null && filter.getContrato().length() > 0) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getContrato)), filter.getContrato()));
                }

                if (filter.getDataAdesao() != null) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getDataAdesao)), filter.getDataAdesao()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        });

        return list.size() > 0 ? list : Collections.EMPTY_LIST;
    }

    @Override
    public Beneficiario findOne(Long handle) {
        return repository.findOne(handle);
    }

}
