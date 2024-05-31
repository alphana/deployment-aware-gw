package name.alp.deploymentawaregw.metrics.custom;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AdditionalMetricsFilter implements GlobalFilter {
    private final MeterRegistry meterRegistry;

    @Autowired
    public AdditionalMetricsFilter(MeterRegistry meterRegistry){
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Counter.builder("gateway.requests.total")
                .description("The total received requests")
                .tag("method", exchange.getRequest().getMethod().name())
                .tag("path", exchange.getRequest().getPath().toString())
                .register(this.meterRegistry)
                .increment();


        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {

                    if(exchange.getResponse().getStatusCode() != null){
                        int statusCode = exchange.getResponse().getStatusCode().value();

                        Counter.builder("gateway.responses.total")
                                .description("The total handled responses")
                                .tag("status", Integer.toString(statusCode))
                                .register(this.meterRegistry)
                                .increment();
                    }
                }));
    }
}
