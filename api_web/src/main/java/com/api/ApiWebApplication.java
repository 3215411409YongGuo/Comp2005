package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiWebApplication.class, args);
	}

}
