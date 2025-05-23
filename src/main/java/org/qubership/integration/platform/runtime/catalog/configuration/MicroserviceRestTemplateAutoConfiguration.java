/*
 * Copyright 2024-2025 NetCracker Technology Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qubership.integration.platform.runtime.catalog.configuration;

import org.jetbrains.annotations.NotNull;
import org.qubership.integration.platform.runtime.catalog.rest.handler.RestTemplateMsResponseErrorHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@AutoConfiguration
public class MicroserviceRestTemplateAutoConfiguration {

    @Bean("restTemplateMS")
    @ConditionalOnMissingBean(name = "restTemplateMS")
    public RestTemplate restTemplateMSDev(
            RestTemplateBuilder builder,
            RestTemplateMsResponseErrorHandler restTemplateMsResponseErrorHandler
    ) {
        return builder
                .requestFactory(getClientHttpRequestFactorySupplier())
                .errorHandler(restTemplateMsResponseErrorHandler)
                .setConnectTimeout(Duration.ofMillis(60_000))
                .setReadTimeout(Duration.ofMillis(60_000))
                .build();
    }

    private static @NotNull Supplier<ClientHttpRequestFactory> getClientHttpRequestFactorySupplier() {
        return () -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
    }
}
