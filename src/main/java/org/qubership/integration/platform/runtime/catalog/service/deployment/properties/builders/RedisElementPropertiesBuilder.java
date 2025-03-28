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

package org.qubership.integration.platform.runtime.catalog.service.deployment.properties.builders;

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.qubership.integration.platform.catalog.model.constant.CamelNames;
import org.qubership.integration.platform.catalog.persistence.configs.entity.chain.element.ChainElement;
import org.qubership.integration.platform.runtime.catalog.service.deployment.properties.ElementPropertiesBuilder;
import org.springframework.stereotype.Component;

@Component
public class RedisElementPropertiesBuilder implements ElementPropertiesBuilder {
    @Override
    public boolean applicableTo(ChainElement element) {
        return List.of(
                CamelNames.REDIS_TRIGGER_COMPONENT,
                CamelNames.REDIS_SENDER_COMPONENT
        ).contains(element.getType());
    }

    @Override
    public Map<String, String> build(ChainElement element) {
        return element.getProperties().entrySet().stream()
                .filter(e -> nonNull(e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
    }

}
