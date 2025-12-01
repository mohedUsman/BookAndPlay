package com.booknplay.gateway.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
public class PublicPathBypassFilter {

    private final GatewayPublicPathsProperties props;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public PublicPathBypassFilter(GatewayPublicPathsProperties props) {
        this.props = props;
    }

    @Bean
    public GlobalFilter publicPathBypassGlobalFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            boolean isPublic = props.getPublicPaths().stream().anyMatch(p -> matcher.match(p, path));

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
