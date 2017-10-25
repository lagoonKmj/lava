package com.lagoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
public class LagoonApplication {

	public static void main(String[] args) {
		SpringApplication.run(LagoonApplication.class, args); 
	}
}
