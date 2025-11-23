package com.booknplay.gateway.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
public class PublicPathBypassFilter {

    private final GatewayPublicPathsProperties props;
    private final AntPathMatcher matcher = new AntPathMatcher();

    // Inject properties bean instead of @Value split
    public PublicPathBypassFilter(GatewayPublicPathsProperties props) {
        this.props = props;
    }

    @Bean
    public GlobalFilter publicPathBypassGlobalFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Simple check – if path matches public list, just forward
            boolean isPublic = props.getPublicPaths().stream().anyMatch(p -> matcher.match(p, path));

            // Always forward Authorization header if present (downstream services may need it)
            var mutatedRequest = exchange.getRequest().mutate().headers(h -> {
                String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
                if (auth != null && !auth.isBlank()) {
                    h.set("Authorization", auth);
                }
            }).build();

            var updatedExchange = exchange.mutate().request(mutatedRequest).build();

            return chain.filter(updatedExchange);
        };
    }
}
