package com.aelkz.blueprint.processor.exception;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestBusinessExceptionProcessor implements Processor {

    Logger logger = LoggerFactory.getLogger(RestBusinessExceptionProcessor.class);

    public void process(Exchange exchange) throws Exception {
        logger.debug("hello");

        // able to do rollback or cleanup operations
    }
}

