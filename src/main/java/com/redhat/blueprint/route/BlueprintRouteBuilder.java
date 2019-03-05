/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.blueprint.route;

import com.redhat.blueprint.exception.BusinessException;
import com.redhat.blueprint.model.Beneficiario;
import com.redhat.blueprint.repository.BeneficiarioRepository;
import com.redhat.blueprint.service.BeneficiarioService;
import com.redhat.blueprint.processor.infinispan.*;
import com.redhat.blueprint.service.dto.BeneficiarioDTO;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.ws.rs.core.MediaType;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

@Component
public class BlueprintRouteBuilder extends RouteBuilder {

    @Autowired
    private Environment env;

    @Value("${api.version}")
    private String apiVersion;

    @Value("${infinispan.entry.lifespanTime}")
    private String infinispanEntryTime;

    @Value("${infinispan.entry.lifespanTimeUnit}")
    private String infinispanEntryTimeUnit;

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private RestEndpointDatabaseProcessor restEndpointDatabaseProcessor;

    @Autowired
    private CacheRecordFoundProcessor cacheRecordFoundProcessor;

    @Autowired
    private RestEndpointMapResponseProcessor restEndpointMapResponseProcessor;

    @Autowired
    private ExtractCacheKeyProcessor extractCacheKeyProcessor;

    @Autowired
    private ExtractCPFProcessor extractCPFProcessor;

    @Autowired
    private CacheFallbackProcessor cacheFallbackProcessor;

    @Override
    public void configure() throws Exception {

        onException(BusinessException.class)
                .handled(true)
                .marshal().json(JsonLibrary.Jackson)
                .log(LoggingLevel.ERROR, "Business validation error.");

        onException(ConnectException.class)
                .handled(true)
                .marshal().json(JsonLibrary.Jackson)
                .log(LoggingLevel.ERROR, "Fail to connect infinispan.");

        // /--------------------------------------------------\
        // | Expose route w/ REST endpoint                    |
        // \--------------------------------------------------/

        restConfiguration()
                //.contextPath("/api/v" + apiVersion) => not working as expected.
                .apiContextPath("/api-docs")
                .apiProperty("api.title", "Beneficiarios API")
                .apiProperty("api.version", apiVersion)
                //.apiProperty("cors", "true")
                .dataFormatProperty("prettyPrint", "true")
                .port(env.getProperty("server.port", "8080"))
                .bindingMode(RestBindingMode.json);

        rest("/").id("beneficiario-api")
              .consumes(MediaType.APPLICATION_JSON).produces(MediaType.APPLICATION_JSON)
                .post(RestEnum.POST_BENEFICIARIO.getResourcePath())
                    .description(RestEnum.POST_BENEFICIARIO.getDescription())
                    .type(BeneficiarioDTO.class) // must add POST class type
                    .route()
                    .routeId(RestEnum.POST_BENEFICIARIO.getRouteId())
                    .to(RouteEnum.DIRECT_API.getUri())
                    .endRest();

        // /--------------------------------------------------\
        // | Configure and expose infinispan routes           |
        // \--------------------------------------------------/

        from(RouteEnum.DIRECT_API.getUri())
            .id(RouteEnum.DIRECT_API.getId())
            .description(RouteEnum.DIRECT_API.getDescription())
            .streamCaching()
            .to(RouteEnum.DIRECT_FETCH_DATA.getUri());

        from(RouteEnum.DIRECT_FETCH_DATA.getUri())
            .log("preparing to call infinispan with GET")
            .process(extractCPFProcessor)
            .setHeader(InfinispanConstants.OPERATION, constant(InfinispanOperation.GET))
            .setHeader(InfinispanConstants.KEY, constant("${header.objKey}"))
            .hystrix()
                .to(RouteEnum.INFINISPAN.getUri())
                .to(RouteEnum.DIRECT_CACHE_CHECK_RESPONSE.getUri())
            .onFallback()
                .to(RouteEnum.DIRECT_DATABASE.getUri())
            .end();

        from(RouteEnum.DIRECT_DATABASE.getUri())
            .log("infinispan entry not found.")
            .process(restEndpointDatabaseProcessor)
            .choice()
            .when(simple("${header.recordFound} == true"))
                .log(LoggingLevel.INFO, "${header.size} record(s) found.")
                .wireTap(RouteEnum.DIRECT_CACHE_PUT.getUri())
                .process(restEndpointMapResponseProcessor)
            .endChoice()
            .otherwise()
                .process(restEndpointMapResponseProcessor)
            .end();

        from(RouteEnum.DIRECT_CACHE_CHECK_RESPONSE.getUri())
            .choice().when(simple("${body} != null"))
                .to(RouteEnum.DIRECT_CACHE_ENTRY_FOUND.getUri())
            .otherwise()
                .to(RouteEnum.DIRECT_DATABASE.getUri())
            .endChoice()
            .end();

        from(RouteEnum.DIRECT_CACHE_ENTRY_FOUND.getUri())
            .log("infinispan entry found!")
            .to(RouteEnum.INFINISPAN_LOG.getUri())
            .process(cacheRecordFoundProcessor)
            .process(restEndpointMapResponseProcessor)
            .end();

        from(RouteEnum.DIRECT_CACHE_PUT.getUri())
            .id(RouteEnum.DIRECT_CACHE_PUT.getId())
            .description(RouteEnum.DIRECT_CACHE_PUT.getDescription())
            .streamCaching()
            .split().body().streaming()
            .log("key/value object: ${body}")
            .process(extractCacheKeyProcessor)
            .setHeader(InfinispanConstants.KEY, constant("${header.objKey}"))
            .setHeader(InfinispanConstants.VALUE, simple("${body}", Beneficiario.class))
            .setHeader(InfinispanConstants.OPERATION, constant(InfinispanOperation.PUT))
            .setHeader(InfinispanConstants.LIFESPAN_TIME_UNIT, constant(
                    // Check if environment variable has HOURS, otherwise will be set as MINUTES.
                    (infinispanEntryTimeUnit != null && "HOURS".equals(infinispanEntryTimeUnit)) ? TimeUnit.HOURS : TimeUnit.MINUTES
            ))
            .setHeader(InfinispanConstants.LIFESPAN_TIME, constant(
                    // Convert environment variable to Integer (it will represent HOURS or MINUTES accordingly to the previous environment variable)
                    Integer.valueOf(infinispanEntryTime)
            ))
            .log(LoggingLevel.INFO, "infinispan PUT operation called.")
            .hystrix()
                .to(RouteEnum.INFINISPAN.getUri())
                .to(RouteEnum.INFINISPAN_LOG.getUri())
            .onFallback()
                .process(cacheFallbackProcessor)
                .log(LoggingLevel.ERROR, "infinispan cache put operation failed.")
            .end();
    }

}
