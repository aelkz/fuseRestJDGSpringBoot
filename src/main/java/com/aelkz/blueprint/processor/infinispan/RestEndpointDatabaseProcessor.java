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

package com.aelkz.blueprint.processor.infinispan;

import com.aelkz.blueprint.processor.rest.RestBaseProcessor;
import com.aelkz.blueprint.exception.BusinessExceptionEnum;
import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.route.RestEnum;
import com.aelkz.blueprint.service.BeneficiarioService;
import com.aelkz.blueprint.service.dto.rest.BeneficiarioResponseDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RestEndpointDatabaseProcessor extends RestBaseProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(RestEndpointDatabaseProcessor.class);

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Override
    public void process(Exchange exchange) throws Exception {
        Message inMessage = exchange.getIn();

        Map headers = inMessage.getHeaders();

        String restResource = (String) headers.get("CamelServletContextPath");
        String httpMethod = (String) headers.get("CamelHttpMethod");

        BeneficiarioResponseDTO response = new BeneficiarioResponseDTO();
        List<Beneficiario> result = new ArrayList();

        logger.info(httpMethod + " " +restResource);

        if (restResource.equals(RestEnum.GET_BENEFICIARIO_CACHE.getResourcePath())) {
            String handleParameter = (String) headers.get("handle");
            Long handle = 0L;

            try {
                handle = Long.valueOf(handleParameter);

                if (handle > 0) {
                    // should use findOne method because handle is a primary key.
                    Beneficiario b = beneficiarioService.findOne(handle);
                    if (b != null) {
                        result.add(b);
                    }
                }

                exchange.getOut().setHeader("recordFound", result.size() > 0 ? true : false);
                exchange.getOut().setHeader("size", result.size());
                exchange.getOut().setBody(result); // must return entity array to be splitted

            } catch (NumberFormatException e) {
                handleFailure(response, BusinessExceptionEnum.HANDLE);
                exchange.getOut().setBody(response);
            }

        }

    }

}
