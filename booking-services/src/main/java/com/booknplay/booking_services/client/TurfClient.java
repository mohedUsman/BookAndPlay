package com.booknplay.booking_services.client;


import com.booknplay.booking_services.config.FeignClientConfig;
import com.booknplay.booking_services.dto.TurfDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "TURF-SERVICE", configuration = FeignClientConfig.class, path = "/api/turfs")
public interface TurfClient {
    @GetMapping("/{turfId}")
    TurfDto getTurfById(@PathVariable Long turfId);
}