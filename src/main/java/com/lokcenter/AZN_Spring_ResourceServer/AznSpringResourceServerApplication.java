package com.lokcenter.AZN_Spring_ResourceServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;

/**
 * Main Class
 */
@SpringBootApplication
public class AznSpringResourceServerApplication {
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		SpringApplication.run(AznSpringResourceServerApplication.class, args);
	}

}
