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

import com.aelkz.blueprint.exception.BusinessException;
import com.aelkz.blueprint.exception.BusinessExceptionEnum;
import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.processor.rest.RestBaseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtractCacheKeyProcessor extends RestBaseProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(ExtractCacheKeyProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        Map<Long, Beneficiario> batch = ex.getIn().getBody(Map.class);

        if (batch != null) {
            if (batch.size() > 1) {
                throw new BusinessException(BusinessExceptionEnum.MAP_SIZE);
            }

            for (Map.Entry<Long, Beneficiario> entry : batch.entrySet()) {
                ex.getOut().setHeader("objKey", new Long(entry.getKey()));
                //ex.getOut().setBody(convertEntityToDTO(entry.getValue()).getEmail());
                //ex.getIn().setBody(convertEntityToDTO(entry.getValue()).getEmail());
                ex.getOut().setBody(entry.getValue());
                ex.getIn().setBody(entry.getValue());
                logger.info("infinispan obj with key={"+entry.getKey()+"} extracted.");
            }

        }

    }

}
