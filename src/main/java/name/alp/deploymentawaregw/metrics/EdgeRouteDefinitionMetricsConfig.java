package name.alp.deploymentawaregw.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionMetrics;


public class EdgeRouteDefinitionMetrics extends RouteDefinitionMetrics {
    public EdgeRouteDefinitionMetrics(MeterRegistry meterRegistry, RouteDefinitionLocator routeLocator, String metricsPrefix) {
        super(meterRegistry, routeLocator, metricsPrefix);
    }
}
