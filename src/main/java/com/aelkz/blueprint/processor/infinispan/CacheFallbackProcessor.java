package com.aelkz.blueprint.processor.infinispan;

import com.aelkz.blueprint.model.Beneficiario;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CacheFallbackProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(CacheFallbackProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        Message inMessage = ex.getIn();

        ex.getOut().setBody(null);
    }

}
