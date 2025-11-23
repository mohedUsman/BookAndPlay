package com.booknplay.gateway.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class OwnerRoleGuardFilter {

    // Use same HS256 secret as User Service
    @Value("${security.jwt.secret}")
    private String secret;

    private boolean isOwner(List<String> roles) {
        if (roles == null) return false;
        return roles.stream().anyMatch(r -> "ROLE_OWNER".equalsIgnoreCase(r));
    }

    private List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    @Bean
    public GlobalFilter ownerWriteGuard() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            HttpMethod method = exchange.getRequest().getMethod();

            // Apply guard only for write ops to /api/turfs/**
            boolean writeOp = method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE;
            boolean isTurfApi = path.startsWith("/api/turfs/") || "/api/turfs".equals(path);

            if (isTurfApi && writeOp) {
                String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
                if (auth == null || !auth.startsWith("Bearer ")) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                String token = auth.substring(7);
                try {
                    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
                    List<String> roles = extractRoles(claims);

                    if (!isOwner(roles)) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                    // CHANGES: Allow request to proceed if owner
                    return chain.filter(exchange);
                } catch (Exception e) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }
}
