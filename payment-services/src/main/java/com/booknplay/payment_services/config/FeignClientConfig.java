package com.booknplay.payment_services.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.println("ehldmdd-----------------------j");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth){
            String token = jwtAuth.getToken().getTokenValue();
            System.out.println("token=============="+ token);
            requestTemplate.header("Authorization", "Bearer "+token);
        }
        else {
            System.out.println("No authentication or token found");
        }

    }
}
