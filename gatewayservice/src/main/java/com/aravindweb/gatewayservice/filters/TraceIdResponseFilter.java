package com.aravindweb.gatewayservice.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

@Component
@Order(-1)
public class TraceIdResponseFilter implements GlobalFilter {

    private static final String TRACE_ID_ATTR = "X_TRACE_ID_ATTR";

    private final Tracer tracer;

    public TraceIdResponseFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Capture trace id *now* while tracer.currentSpan() is likely available
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            exchange.getAttributes().put(TRACE_ID_ATTR, traceId);
        }

        // Add header right before commit, reading from exchange attribute
        exchange.getResponse().beforeCommit(() -> {
            String traceId = exchange.getAttribute(TRACE_ID_ATTR);
            if (traceId != null) {
                exchange.getResponse().getHeaders().set("X-Trace-Id", traceId);
            }
            return Mono.empty();
        });

        return chain.filter(exchange);
    }
}
