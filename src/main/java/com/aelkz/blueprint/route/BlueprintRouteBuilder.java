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

package com.aelkz.blueprint.route;

import com.aelkz.blueprint.model.Beneficiario;
import com.aelkz.blueprint.processor.exception.RestBusinessExceptionProcessor;
import com.aelkz.blueprint.processor.infinispan.*;
import com.aelkz.blueprint.processor.rest.RestEndpointProcessor;
import com.aelkz.blueprint.repository.BeneficiarioRepository;
import com.aelkz.blueprint.exception.BusinessException;
import com.aelkz.blueprint.processor.ClearHeaderProcessor;
import com.aelkz.blueprint.processor.HeaderDebugProcessor;
import com.aelkz.blueprint.service.BeneficiarioService;
import com.aelkz.blueprint.service.dto.BeneficiarioDTO;
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

@Component
public class BlueprintRouteBuilder extends RouteBuilder {

    @Autowired
    private Environment env;

    @Value("${api.version}")
    private String apiVersion;

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private RestEndpointProcessor restEndpointProcessor;

    @Autowired
    private RestEndpointDatabaseProcessor restEndpointDatabaseProcessor;

    @Autowired
    private KeyValueBuilderProcessor keyValueBuilderProcessor;

    @Autowired
    private ExtractBeneficiarioProcessor extractBeneficiarioProcessor;

    @Autowired
    private RestEndpointMapResponseProcessor restEndpointMapResponseProcessor;

    @Autowired
    private ExtractCacheKeyProcessor extractCacheKeyProcessor;

    @Autowired
    private ExtractHandleProcessor extractHandleProcessor;

    @Autowired
    private HeaderDebugProcessor headerDebugProcessor;

    @Autowired
    private ClearHeaderProcessor clearHeaderProcessor;

    @Autowired
    private CacheFallbackProcessor cacheFallbackProcessor;

    @Override
    public void configure() throws Exception {

        onException(BusinessException.class)
                .handled(true)
                .process(new RestBusinessExceptionProcessor())
                .marshal().json(JsonLibrary.Jackson)
                .log(LoggingLevel.ERROR, "Fail to validate business rules.");

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

        rest("/").id("beneficiarios-api")
              .consumes(MediaType.APPLICATION_JSON).produces(MediaType.APPLICATION_JSON)
                .get(RestEnum.GET_BENEFICIARIOS.getResourcePath())
                    .description(RestEnum.GET_BENEFICIARIOS.getDescription())
                    .route()
                    .routeId(RestEnum.GET_BENEFICIARIOS.getRouteId())
                    .process(restEndpointProcessor)
                    .endRest()
                .get(RestEnum.GET_BENEFICIARIO.getResourcePath())
                    .description(RestEnum.GET_BENEFICIARIO.getDescription())
                    .route()
                    .routeId(RestEnum.GET_BENEFICIARIO.getRouteId())
                    .process(restEndpointProcessor)
                    .endRest()
                // ----- START-INFINISPAN API ----- \\
                .get(RestEnum.GET_BENEFICIARIO_CACHE.getResourcePath())
                    .description(RestEnum.GET_BENEFICIARIO_CACHE.getDescription())
                    .route()
                    .routeId(RestEnum.GET_BENEFICIARIO_CACHE.getRouteId())
                    .to(RouteEnum.DIRECT_API.getUri())
                    .endRest()
                // ----- END-INFINISPAN API ----- \\
                .post(RestEnum.POST_BENEFICIARIOS.getResourcePath())
                    .type(BeneficiarioDTO.class)
                    .description(RestEnum.POST_BENEFICIARIOS.getDescription())
                    .route()
                    .routeId(RestEnum.POST_BENEFICIARIOS.getRouteId())
                    .process(restEndpointProcessor)
                    .endRest();

        // /--------------------------------------------------\
        // | Configure and expose infinispan routes           |
        // \--------------------------------------------------/

        /**
         * streamCaching: This ensures that Camel will deal with large streaming payloads in a manner where Camel can
         * automatic spool big streams to temporary disk space to avoid taking up memory. The stream caching in Apache
         * Camel is fully configurable and you can setup thresholds that are based on payload size, memory left in the
         * JVM etc to trigger when to spool to disk. However the default settings are often sufficient.
         */

        from(RouteEnum.DIRECT_API.getUri())
            .id(RouteEnum.DIRECT_API.getId())
            .description(RouteEnum.DIRECT_API.getDescription())
            .streamCaching()
            .setBody(simple("hello"))
            .to(RouteEnum.DIRECT_FETCH_DATA.getUri());

        from(RouteEnum.DIRECT_FETCH_DATA.getUri())
            .streamCaching()
            .log("preparing to call infinispan with handle=${header.handle}")
            .process(extractHandleProcessor)
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
                .process(keyValueBuilderProcessor)
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
            .process(extractBeneficiarioProcessor)
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
