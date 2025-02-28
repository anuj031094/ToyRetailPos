package com.ey.posvendor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PosVendorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PosVendorServiceApplication.class, args);
	}

}
