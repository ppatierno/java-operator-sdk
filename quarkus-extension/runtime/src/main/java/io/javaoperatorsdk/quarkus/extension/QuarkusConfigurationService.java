package io.javaoperatorsdk.quarkus.extension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.CustomResource;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.config.ConfigurationService;
import io.javaoperatorsdk.operator.api.config.ControllerConfiguration;
import io.quarkus.arc.DefaultBean;

@Singleton
@DefaultBean
public class QuarkusConfigurationService implements ConfigurationService {
    @Inject
    io.fabric8.kubernetes.client.KubernetesClient client;
    
    private final Map<String, ControllerConfiguration> controllerConfigurations;
    
    public QuarkusConfigurationService(List<ControllerConfiguration> configurations) {
        if (configurations != null && !configurations.isEmpty()) {
            controllerConfigurations = new ConcurrentHashMap<>(configurations.size());
            configurations.forEach(c -> controllerConfigurations.put(c.getName(), c));
        } else {
            controllerConfigurations = Collections.emptyMap();
        }
    }
    
    @Override
    public <R extends CustomResource> ControllerConfiguration<R> getConfigurationFor(ResourceController<R> controller) {
        return controllerConfigurations.get(controller.getName());
    }
    
    @Override
    public Config getClientConfiguration() {
        return client.getConfiguration();
    }
    
    @DefaultBean
    @Singleton
    @Produces
    public Operator operator() {
        return new Operator(client, this);
    }
}
