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
public class ExtractBeneficiarioProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(ExtractBeneficiarioProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        Message inMessage = ex.getIn();

        Beneficiario entryValue = ex.getIn().getBody(Beneficiario.class);

        Map<Long,Beneficiario> batch = new HashMap<>();

        batch.put(entryValue.getHandle(), entryValue);

        ex.getOut().setBody(batch);
    }

}
