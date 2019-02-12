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

package com.aelkz.blueprint.processor.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.aelkz.blueprint.exception.BusinessExceptionEnum;
import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.service.BeneficiarioService;
import com.aelkz.blueprint.service.dto.BeneficiarioDTO;
import com.aelkz.blueprint.service.dto.rest.BeneficiarioResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestBaseProcessor {

    private static final transient Logger logger = LoggerFactory.getLogger(RestBaseProcessor.class);

    @Autowired
    private BeneficiarioService beneficiarioService;

    /**
     * Utility method to handle output for a collection of Beneficiario entity
     * @param response
     * @param batch
     */
    public void processMapResponse(BeneficiarioResponseDTO response, Map<Long,Beneficiario> batch) {
        if (batch != null && batch.size() > 0) {
            // iterate over Map and create a new List
            List<Beneficiario> result = new ArrayList();

            for (Map.Entry<Long, Beneficiario> entry : batch.entrySet()) {
                result.add(entry.getValue());
            }

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

    /**
     * Utility method to handle output for a collection of Beneficiario entity
     * @param response
     * @param result
     */
    public void processListResponse(BeneficiarioResponseDTO response, List<Beneficiario> result) {
        if (result.size() > 0) {
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

    /**
     * Utility method to query only a single result by {handler}
     * @param response
     * @param result
     * @param handleParameter
     */
    public void processSingleResponse(BeneficiarioResponseDTO response, List<Beneficiario> result, String handleParameter) {
        Long handle;
        try {
            handle = Long.valueOf(handleParameter);

            if (handle > 0) {
                // should use findOne method because handle is a primary key.
                Beneficiario b = beneficiarioService.findOne(handle);
                if (b != null) {
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

        } catch (NumberFormatException e) {
            handleFailure(response, BusinessExceptionEnum.HANDLE);
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
            dto.setHandle(b.getHandle());
            dto.setFamilia(b.getFamilia());
            dto.setNome(b.getNome());
            dto.setEmail(b.getEmail());
            dto.setCpf(b.getCpf());
            dto.setCartao(b.getCartao());
            dto.setContrato(b.getContrato());
            dto.setDataAdesao(b.getDataAdesao());
            dtos.add(dto);
        }
        return dtos;
    }

    public BeneficiarioDTO convertEntityToDTO(Beneficiario entry) {
        BeneficiarioDTO dto = new BeneficiarioDTO();
        dto.setHandle(entry.getHandle());
        dto.setFamilia(entry.getFamilia());
        dto.setNome(entry.getNome());
        dto.setEmail(entry.getEmail());
        dto.setCpf(entry.getCpf());
        dto.setCartao(entry.getCartao());
        dto.setContrato(entry.getContrato());
        dto.setDataAdesao(entry.getDataAdesao());
        return dto;
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
