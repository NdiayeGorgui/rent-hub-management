package com.smartiadev.payments_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PaymentsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentsServiceApplication.class, args);
	}

}
