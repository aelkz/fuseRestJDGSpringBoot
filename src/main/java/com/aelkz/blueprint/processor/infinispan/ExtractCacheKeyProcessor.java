package com.aelkz.blueprint.processor.infinispan;

import com.aelkz.blueprint.exception.BusinessException;
import com.aelkz.blueprint.exception.BusinessExceptionEnum;
import com.aelkz.blueprint.model.Beneficiario;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtractCacheKeyProcessor implements Processor {

    private static final transient Logger logger = LoggerFactory.getLogger(ExtractCacheKeyProcessor.class);

    @Override
    public void process(Exchange ex) throws Exception {
        Map<Long, Beneficiario> batch = ex.getIn().getBody(Map.class);

        if (batch != null) {
            if (batch.size() > 1) {
                throw new BusinessException(BusinessExceptionEnum.MAP_SIZE);
            }

            for (Map.Entry<Long, Beneficiario> entry : batch.entrySet()) {
                ex.getOut().setHeader("objKey", entry.getKey());
                ex.getOut().setBody(batch); // must forward body to the next processor
                // ex.getOut().setBody(entry.getValue());
                logger.info("infinispan obj with key={"+entry.getKey()+"} extracted.");
            }

        }

    }

}
