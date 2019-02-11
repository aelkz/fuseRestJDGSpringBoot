package com.aelkz.blueprint.processor.infinispan;

import com.aelkz.blueprint.model.Beneficiario;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeyValueBuilderProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(KeyValueBuilderProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        List<Beneficiario> array = ex.getIn().getBody(ArrayList.class);

        Map<Long, Beneficiario> batch = new HashMap<>();

        array.forEach(beneficiario -> batch.put(beneficiario.getHandle(), beneficiario));

        ex.getOut().setBody(batch);
    }

}
