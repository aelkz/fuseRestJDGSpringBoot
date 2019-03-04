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

package com.redhat.blueprint.configuration.infinispan;

import java.util.Objects;
import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class InfinispanConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String CAMEL_URI = "infinispan:default?cacheContainer=#remoteCacheContainer";
    public static final String CAMEL_LOG_URI = "log:org.apache.camel.component.infinispan?level=INFO&showAll=true&multiline=true";

    @Value("${infinispan.service}")
    private String service;

    @Value("${infinispan.cacheName}")
    private String cacheName;

    @Value("${infinispan.host}")
    private String host;

    @Value("${infinispan.port}")
    private String port;

    @Value("${infinispan.maxRetries}")
    private String maxRetries;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BasicCacheContainer remoteCacheContainer(Environment environment) {

        String serviceBaseName = service.toUpperCase().replace("-", "_");
        Objects.requireNonNull(host, "Infinispan service host not found in the environment");
        Objects.requireNonNull(port, "Infinispan service port not found in the environment");

        String hostPort = host + ":" + port;
        logger.info("Connecting to the Infinispan service at {}", hostPort);

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .forceReturnValues(true)
                .addServers(hostPort)
                .maxRetries(Integer.valueOf(maxRetries))
                .connectionTimeout(3000)
                ;

        return new RemoteCacheManager(builder.create(), false);
    }

    @Bean
    public InfinispanIdempotentRepository infinispanRepository(BasicCacheContainer cacheContainer) {
        return InfinispanIdempotentRepository.infinispanIdempotentRepository(cacheContainer, cacheName);
    }

}