package com.aravindweb.gatewayservice.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.micrometer.tracing.Tracer;

@Component
@Order(-1)
public class TraceIdResponseFilter implements GlobalFilter {

    private final Tracer tracer;

    public TraceIdResponseFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();

        // Attach the header *before* the response is committed
        response.beforeCommit(() -> {
            if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
                String traceId = tracer.currentSpan().context().traceId();
                response.getHeaders().set("X-Trace-Id", traceId);
            }
            return Mono.empty();
        });
    
        return chain.filter(exchange);
    }
}
