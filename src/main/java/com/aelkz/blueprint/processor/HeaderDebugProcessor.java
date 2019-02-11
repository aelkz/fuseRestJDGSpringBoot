package com.aelkz.blueprint.processor;

import com.aelkz.blueprint.processor.rest.RestBaseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HeaderDebugProcessor extends RestBaseProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(HeaderDebugProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Message inMessage = exchange.getIn();

        Map headers = inMessage.getHeaders();

        // print all request headers on console.
        System.out.println("--- camel-headers/ ---");
        headers.forEach((k,v)->System.out.println("Key: " + k + ", Value: " + v + ", Type: " + ((v != null && v.getClass() != null) ? v.getClass().getName() : "null")));
        System.out.println("--- /camel-headers ---");
    }

}
