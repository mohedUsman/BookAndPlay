package com.booknplay.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Configuration
public class UserIdentityRelayFilterConfig {

    // CHANGE: Internal signing secret (distinct from JWT secret)
    @Value("${gateway.internal.secret}")
    private String internalSecret;

    private String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(internalSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute HMAC", e);
        }
    }

    @Bean
    public GlobalFilter userIdentityRelayFilter() {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .defaultIfEmpty(null)
                .flatMap(auth -> {
                    String path = exchange.getRequest().getURI().getPath();
                    String timestamp = String.valueOf(Instant.now().getEpochSecond());

                    ServerHttpRequest.Builder reqBuilder = exchange.getRequest().mutate()
                            .header("X-Internal-Caller", "gateway")
                            .header("X-Internal-Timestamp", timestamp);

                    String email = null;
                    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                        email = jwt.getSubject();
                        reqBuilder.header("X-User-Email", email);
                    }

                    // CHANGE: Sign path + timestamp + email (if present)
                    String payloadToSign = path + "|" + timestamp + "|" + (email != null ? email : "");
                    String signature = hmacSha256(payloadToSign);
                    reqBuilder.header("X-Internal-Signature", signature);

                    ServerWebExchange mutated = exchange.mutate().request(reqBuilder.build()).build();
                    return chain.filter(mutated);
                });
    }
}