package com.aelkz.blueprint.processor;

import com.aelkz.blueprint.processor.rest.RestBaseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ClearHeaderProcessor extends RestBaseProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(ClearHeaderProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Message inMessage = exchange.getIn();

        exchange.getOut().setHeaders(new HashMap<>());
        exchange.getIn().setHeaders(new HashMap<>());
    }

}
