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
import com.redhat.blueprint.exception.BusinessExceptionEnum;
import com.redhat.blueprint.processor.rest.RestBaseProcessor;
import com.redhat.blueprint.service.dto.BeneficiarioDTO;
import com.redhat.blueprint.service.dto.rest.BeneficiarioResponseDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtractCPFProcessor extends RestBaseProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(ExtractCPFProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        BeneficiarioResponseDTO response = new BeneficiarioResponseDTO();

        try {
            Map<String,Object> headers = ex.getIn().getHeaders();
            BeneficiarioDTO jsonData = ex.getIn().getBody(BeneficiarioDTO.class);

            // do a simple validation before going further
            if (jsonData.getCpf() == null || jsonData.getCpf().length() != 11) {
                throw new BusinessException(BusinessExceptionEnum.CPF);
            }

            headers.put("objKey", jsonData.getCpf());
            ex.getOut().setBody(null); // clear body for infinispan result
            ex.getOut().setHeaders(headers); // must forward the header to the next processor
        } catch (BusinessException e) {
            handleFailure(response, e.getType());
            ex.getOut().setBody(response);
            ex.getOut().setFault(true); // stop route processing
        }

    }

}
