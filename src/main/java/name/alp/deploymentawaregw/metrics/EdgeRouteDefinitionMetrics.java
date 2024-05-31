package name.alp.deploymentawaregw.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionMetrics;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class EdgeRouteDefinitionMetrics  {

    private final String routeDefinitionCount;

    public EdgeRouteDefinitionMetrics(MeterRegistry meterRegistry) {
        routeDefinitionCount = "{inffff}";

        meterRegistry.gauge("rally_app_info",0) ;

//        meterRegistry.find("rally_app_info")
    }

}
