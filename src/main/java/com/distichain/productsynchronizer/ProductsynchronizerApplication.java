package com.distichain.productsynchronizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductsynchronizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsynchronizerApplication.class, args);
	}

}
