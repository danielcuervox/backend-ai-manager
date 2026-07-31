package com.cuervo.erp_personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ErpPersonalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpPersonalApplication.class, args);
	}

}
