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
public class KeyValueParserProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(KeyValueParserProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        Message inMessage = ex.getIn();

        Map headers = inMessage.getHeaders();

        Map<Long, Beneficiario> batch = new HashMap<>();

        // TODO - implementar a recuperação do valor do infinispan recém retornado.

        // batch.put((Long) headers.get(InfinispanConstants.KEY), (Beneficiario) headers.get(InfinispanConstants.VALUE));

        ex.getOut().setBody(batch);
    }

}
