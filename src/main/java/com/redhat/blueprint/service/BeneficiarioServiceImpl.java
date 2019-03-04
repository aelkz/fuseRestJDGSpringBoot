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

package com.redhat.blueprint.service;

import com.redhat.blueprint.model.Beneficiario;
import com.redhat.blueprint.repository.BeneficiarioRepository;
import com.redhat.blueprint.service.dto.BeneficiarioDTO;
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

    @Override
    public Beneficiario findByCpf(BeneficiarioDTO filter) {

        if (filter.getCpf() == null && filter.getCpf().length() == 11) {
            return null;
        }

        List<Beneficiario> list = repository.findAll(new Specification<Beneficiario>() {

            @Override
            public Predicate toPredicate(Root<Beneficiario> root, CriteriaQuery< ?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (filter.getCpf() != null) {
                    predicates.add(cb.equal(root.get(Beneficiario.getJPAColumnName(Beneficiario::getCpf)), filter.getCpf()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        });

        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

}
