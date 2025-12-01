package com.booknplay.booking_services;

import com.booknplay.booking_services.client.NotificationClient;
import com.booknplay.booking_services.event.BookingEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class BookingServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingServicesApplication.class, args);
	}


    @Bean // NEW: Register async listener
    public BookingEventListener bookingEventListener() {
        return new BookingEventListener();
    }
}
