package org.qubership.integration.platform.runtime.catalog.service.diagnostic.validations.builtin;

import org.qubership.integration.platform.catalog.model.diagnostic.ValidationAlert;
import org.qubership.integration.platform.catalog.persistence.configs.entity.chain.element.ChainElement;
import org.qubership.integration.platform.catalog.persistence.configs.entity.diagnostic.ValidationChainAlert;
import org.qubership.integration.platform.catalog.persistence.configs.repository.chain.ElementRepository;
import org.qubership.integration.platform.runtime.catalog.model.diagnostic.ValidationImplementationType;
import org.qubership.integration.platform.runtime.catalog.service.diagnostic.ValidationEntityType;
import org.qubership.integration.platform.runtime.catalog.service.diagnostic.ValidationSeverity;
import org.qubership.integration.platform.runtime.catalog.service.diagnostic.validations.DiagnosticValidationUnexpectedException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MultipleAsyncConsumersValidation extends BuiltinValidation {
    private final ElementRepository elementRepository;

    public MultipleAsyncConsumersValidation(ElementRepository elementRepository) {
        super(
                "multiple-async-consumers-found-in-chain_5WS1CT0Z",
                "Multiple async consumers found in the chain",
                "Rule allows to find duplicate consumers in chains",
                "Duplicate consumers in different chains may result in unpredictable behavior and trigger additional errors."
                        + " Make sure that only unique consumers are retained",
                ValidationEntityType.CHAIN_ELEMENT,
                ValidationImplementationType.BUILT_IN,
                ValidationSeverity.WARNING
        );
        this.elementRepository = elementRepository;
    }

    @Override
    public Collection<? extends ValidationAlert> validate() throws DiagnosticValidationUnexpectedException {
        List<ChainElement> elements = elementRepository.findAllByTypeInAndChainNotNull(
                List.of("kafka-trigger-2", "async-api-trigger", "rabbitmq-trigger-2")
        );
        Map<String, Long> occurrences = elements.stream().collect(Collectors.collectingAndThen(
                Collectors.groupingBy(
                        this::getGroupingString, Collectors.counting()
                ),
                map -> {
                    map.entrySet().removeIf(entry -> entry.getValue() < 2);
                    return map;
                }
        ));

        List<ChainElement> duplicates = elements.stream()
                .filter(element -> occurrences.containsKey(getGroupingString(element)))
                .collect(Collectors.toList());


        return duplicates.stream()
                .map(element -> ValidationChainAlert.builder()
                        .validationId(getId())
                        .chain(element.getChain())
                        .element(element)
                        .build())
                .toList();
    }

    private String getGroupingString(ChainElement chainElement) {
        Map<String, Object> properties = chainElement.getProperties();
        StringBuilder stringBuilder = new StringBuilder();
        switch (chainElement.getType()) {
            case "kafka-trigger-2":
                if ("maas".equals(properties.get("connectionSourceType"))) {
                    stringBuilder.append(properties.get("topicsClassifierName"));
                    stringBuilder.append(properties.get("maasClassifierNamespace"));
                } else {
                    stringBuilder.append(properties.get("topics"));
                }
                stringBuilder.append(properties.get("groupId"));
                break;
            case "rabbitmq-trigger-2":
                stringBuilder.append(properties.get("vhostClassifierName"));
                stringBuilder.append(properties.get("maasClassifierNamespace"));
                stringBuilder.append(properties.get("queues"));
                break;
            case "async-api-trigger":
                Object asyncProperties = properties.get("integrationOperationAsyncProperties");
                Map<String, Object> asyncPropertiesMap =
                        (asyncProperties instanceof Map<?, ?>) ? (Map<String, Object>) asyncProperties : Collections.emptyMap();
                stringBuilder.append(asyncPropertiesMap.get("maas.classifier.name"));
                stringBuilder.append(asyncPropertiesMap.get("maas.classifier.namespace"));
                if ("kafka".equals(properties.get("integrationOperationProtocolType"))) {
                    stringBuilder.append(properties.get("integrationOperationPath"));
                    stringBuilder.append(asyncPropertiesMap.get("groupId"));
                } else if ("amqp".equals(properties.get("integrationOperationProtocolType"))) {
                    stringBuilder.append(asyncPropertiesMap.get("queues"));
                }
        }
        return stringBuilder.toString();
    }
}
