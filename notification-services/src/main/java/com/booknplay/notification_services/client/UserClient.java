package com.booknplay.notification_services.client;

import com.booknplay.notification_services.config.FeignClientConfig;
import com.booknplay.notification_services.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", configuration = FeignClientConfig.class, path = "/api/users")
public interface UserClient {
    @GetMapping("/email/{email}")
    UserDto getUserByEmail(@PathVariable String email);
}