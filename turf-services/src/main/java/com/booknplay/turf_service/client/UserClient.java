package com.booknplay.turf_service.client;

import com.booknplay.turf_service.config.FeignClientConfig;
import com.booknplay.turf_service.dto.UserDto;
import jakarta.ws.rs.PATCH;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", configuration = FeignClientConfig.class, path = "/api/users")
public interface UserClient {
    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable Long id);

    @GetMapping("/email/{email}")
    UserDto getUserByEmail(@PathVariable String email);

}
