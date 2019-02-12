package com.aelkz.blueprint.processor.infinispan;

import com.aelkz.blueprint.processor.rest.RestBaseProcessor;
import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.service.BeneficiarioService;
import com.aelkz.blueprint.service.dto.rest.BeneficiarioResponseDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RestEndpointMapResponseProcessor extends RestBaseProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(RestEndpointMapResponseProcessor.class);

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Override
    public void process(Exchange exchange) throws Exception {
        BeneficiarioResponseDTO response = new BeneficiarioResponseDTO();

        Map<Long, Beneficiario> batch = exchange.getIn().getBody(Map.class);

        processMapResponse(response, batch);

        // must return a proper json response
        exchange.getOut().setBody(response);
    }

}
