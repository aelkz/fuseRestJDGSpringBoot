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

package com.redhat.blueprint.processor.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhat.blueprint.exception.BusinessExceptionEnum;
import com.redhat.blueprint.model.Beneficiario;
import com.redhat.blueprint.service.BeneficiarioService;
import com.redhat.blueprint.service.dto.BeneficiarioDTO;
import com.redhat.blueprint.service.dto.rest.BeneficiarioResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RestBaseProcessor {

    private static final transient Logger logger = LoggerFactory.getLogger(RestBaseProcessor.class);

    @Autowired
    private BeneficiarioService beneficiarioService;

    public void processMapResponse(BeneficiarioResponseDTO response, Beneficiario b) {
        if (b != null) {
            // iterate over Map and create a new List
            List<Beneficiario> result = new ArrayList();
            result.add(b);

            response.setTimestamp(LocalDateTime.now());
            response.setHttpStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setItems(convertEntityToDTO(result));
        } else {
            response.setTimestamp(LocalDateTime.now());
            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setHttpStatus(HttpStatus.NOT_FOUND.value());
            response.setItems(new ArrayList<>());
        }
    }

    public void processSingleResponse(BeneficiarioResponseDTO response, Beneficiario b) {
        if (b != null) {
            List<BeneficiarioDTO> items = new ArrayList<>();
            BeneficiarioDTO dto = new BeneficiarioDTO();
            BeanUtils.copyProperties(b, dto);
            items.add(dto);

            response.setTimestamp(LocalDateTime.now());
            response.setHttpStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setItems(items);
        } else {
            response.setTimestamp(LocalDateTime.now());
            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setHttpStatus(HttpStatus.NOT_FOUND.value());
            response.setItems(new ArrayList<>());
        }
    }

    /**
     * Utility method to handle exceptions to provide proper json result.
     * @param response
     * @param error
     */
    public void handleFailure(BeneficiarioResponseDTO response, BusinessExceptionEnum error) {
        response.setMessage(error.getMessage());
        response.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setTimestamp(LocalDateTime.now());
        response.setItems(new ArrayList<>());
    }

    public List<BeneficiarioDTO> convertEntityToDTO(List<Beneficiario> jpaResult) {
        List<BeneficiarioDTO> dtos = new ArrayList<>();

        for (Beneficiario b : jpaResult) {
            BeneficiarioDTO dto = new BeneficiarioDTO();
            BeanUtils.copyProperties(b, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    public String convertListToJson(List<Beneficiario> jpaResult) {
        if (jpaResult.size() > 0) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            return gson.toJson(jpaResult);
        }
        return "{}";
    }

    public BeneficiarioService getBeneficiarioService() {
        return beneficiarioService;
    }

    public void setBeneficiarioService(BeneficiarioService beneficiarioService) {
        this.beneficiarioService = beneficiarioService;
    }
}
