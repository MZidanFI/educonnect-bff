package com.edu.website_bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebsiteBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsiteBffApplication.class, args);
	}

}
