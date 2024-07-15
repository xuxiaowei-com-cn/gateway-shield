package cn.com.xuxiaowei.shield.gateway.routes;

import cn.com.xuxiaowei.shield.gateway.GatewayApplicationTests;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
		"spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Path=/header",
		"spring.cloud.gateway.routes[0].predicates[1]=Host=demo.localdev.me:*",
})
// @formatter:on
class HostPathTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@SneakyThrows
	@Test
	void header() {

		String url = String.format("http://demo.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

		assertEquals(entity.getStatusCode(), HttpStatus.OK);

		String body = entity.getBody();

		assertNotNull(body);

		GatewayApplicationTests.queryForList(jdbcTemplate);

	}

}
