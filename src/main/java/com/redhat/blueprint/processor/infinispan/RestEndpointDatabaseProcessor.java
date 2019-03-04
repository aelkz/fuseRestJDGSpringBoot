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

package com.redhat.blueprint.processor.infinispan;

import com.redhat.blueprint.exception.BusinessException;
import com.redhat.blueprint.processor.rest.RestBaseProcessor;
import com.redhat.blueprint.exception.BusinessExceptionEnum;
import com.redhat.blueprint.model.Beneficiario;
import com.redhat.blueprint.route.RestEnum;
import com.redhat.blueprint.service.BeneficiarioService;
import com.redhat.blueprint.service.dto.BeneficiarioDTO;
import com.redhat.blueprint.service.dto.rest.BeneficiarioResponseDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

        logger.info(httpMethod + " " +restResource);

        if (restResource.equals(RestEnum.POST_BENEFICIARIO.getResourcePath())) {
            String cpf = (String) headers.get("objKey");

            // should use findOne method because cpf is a primary key.
            BeneficiarioDTO dto = new BeneficiarioDTO();
            dto.setCpf(cpf);
            Beneficiario b = beneficiarioService.findByCpf(dto);

            exchange.getOut().setHeader("recordFound", (b != null && b.getHandle().intValue() > 0) ? true : false);
            exchange.getOut().setBody(b); // must return entity array to be splitted
        }

    }

}
