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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheRecordFoundProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(CacheRecordFoundProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        Message inMessage = ex.getIn();

        ex.getOut().setHeader("recordFound", true);
        ex.getOut().setBody(ex.getIn().getBody()); // forward the body to the next processor
    }

}
