package com.booknplay.payment_services;

import com.booknplay.payment_services.client.NotificationClient;
import com.booknplay.payment_services.event.PaymentEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PaymentServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServicesApplication.class, args);
	}

    @Bean // NEW: Register async listener
    public PaymentEventListener paymentEventListener() {
        return new PaymentEventListener();
    }
}
