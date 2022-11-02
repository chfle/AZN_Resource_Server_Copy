package com.lokcenter.AZN_Spring_ResourceServer;

import com.lokcenter.AZN_Spring_ResourceServer.services.MemService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@AutoConfigureMockMvc
class AznSpringResourceServerApplicationTests {

	/**
	 * Used to fake memcached
	 */
	@MockBean
	private MemService memService;
	@Test
	void contextLoads() {
	}

}
