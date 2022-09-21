package com.lokcenter.AZN_Spring_ResourceServer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
class AznSpringResourceServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
