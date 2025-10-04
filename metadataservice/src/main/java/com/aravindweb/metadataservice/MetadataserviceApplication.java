package com.aravindweb.metadataservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MetadataserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetadataserviceApplication.class, args);
	}

}
