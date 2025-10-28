package com.aravindweb.gatewayservice.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Bean
    public WebClient authWebClient(WebClient.Builder webClientBuilder, Tracer tracer) {

        ExchangeFilterFunction tracingInjector = ExchangeFilterFunction.ofRequestProcessor(request -> {
            Span current = tracer.currentSpan();

            // If there is no current span, simply log and pass through
            if (current == null || current.context() == null) {
                log.debug("No current span available when sending request to {}. Headers: {}", request.url(), request.headers());
                return Mono.just(request);
            }

            String traceId = current.context().traceId();
            String spanId = current.context().spanId();

            // sampled: if sampled is available in the context, use it; otherwise assume "1"
            String sampled = "1";
            try {
                Boolean sampledFlag = current.context().sampled();
                if (sampledFlag != null) sampled = sampledFlag ? "1" : "0";
            } catch (Exception ignored) { /* not all bridges expose sampled() */ }

            // Build B3 single header (traceId-spanId-sampled) and x-b3 headers
            String b3Single = traceId + "-" + spanId + "-" + sampled;

            // Build W3C traceparent header: version-format (00-<trace-id>-<parent-id>-01)
            // traceparent expects 32-char trace id and 16-char span id; the tracer usually provides correct lengths.
            String traceparent = String.format("00-%s-%s-01", traceId, spanId);

            ClientRequest newRequest = ClientRequest.from(request)
                    // single header
                    .header("b3", b3Single)
                    // old-style x-b3 headers (many services still use these)
                    .header("X-B3-TraceId", traceId)
                    .header("X-B3-SpanId", spanId)
                    .header("X-B3-Sampled", sampled)
                    // W3C header (for compatibility)
                    .header("traceparent", traceparent)
                    .build();

            log.debug("Injected tracing headers for {} -> b3={}, traceparent={}", request.url(), b3Single, traceparent);
            return Mono.just(newRequest);
        });

        // Optional: log outgoing headers (helpful while debugging).
        ExchangeFilterFunction logger = ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.debug("Outgoing WebClient request headers: {} to {}", req.headers(), req.url());
            return Mono.just(req);
        });

        return webClientBuilder
                .filter(tracingInjector)
                .filter(logger)
                .build();
    }
}
