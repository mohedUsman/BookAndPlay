package com.booknplay.payment_services.client;

import com.booknplay.payment_services.config.FeignClientConfig;
import com.booknplay.payment_services.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", configuration = FeignClientConfig.class, path = "/api/users")
public interface UserClient {
    @GetMapping("/email/{email}")
    UserDto getUserByEmail(@PathVariable String email);

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable Long id);
}