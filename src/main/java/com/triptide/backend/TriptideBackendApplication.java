package com.triptide.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TriptideBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriptideBackendApplication.class, args);
	}

}
