package name.alp.deploymentawaregw.metrics;

import io.micrometer.core.instrument.*;
import name.alp.deploymentawaregw.deploymentinfo.DeploymentInfoProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@Configuration
public class EdgeRouteDefinitionMetricsConfig {

//    @Bean
//    public RouteDefinitionMetrics routeDefinitionMetrics (MeterRegistry meterRegistry) {
//        return new EdgeRouteDefinitionMetrics(meterRegistry);
//    }

    @Bean

    MeterRegistryCustomizer<MeterRegistry> appInfo(DeploymentInfoProperties deploymentInfoProperties) {



        return (registry) -> {
            registry.config().commonTags(deploymentInfoProperties.getCommonTags())
                    ;
        };
    }
}
