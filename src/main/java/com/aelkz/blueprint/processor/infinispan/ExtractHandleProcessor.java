package com.aelkz.blueprint.processor.infinispan;

import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.processor.rest.RestBaseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtractHandleProcessor extends RestBaseProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(ExtractHandleProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        Map headers = ex.getIn().getHeaders();

        String handle = (String) ex.getIn().getHeader("handle");
        headers.put("objKey", new Long(handle));

        ex.getOut().setHeaders(headers);
    }

}
