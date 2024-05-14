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

import java.util.UUID;

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
        "spring.cloud.gateway.routes[0].id=redirect-baidu",
        // uri 无意义，但不能为空
        "spring.cloud.gateway.routes[0].uri=http://test",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=redirect.localdev.me:*",
		// 所有地址重定向到 http://127.0.0.1:45678，并且不携带 path 和 参数
        "spring.cloud.gateway.routes[0].filters[0]=RedirectTo=302,http://127.0.0.1:45678"
})
// @formatter:on
class HostRedirectToTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 所有地址重定向到 http://127.0.0.1:45678，并且不携带 path 和 参数
	 */
	@SneakyThrows
	@Test
	void uuid() {

		String uuid = UUID.randomUUID().toString();

		String url = String.format("http://redirect.localdev.me:%s/%s", serverPort, uuid);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

		assertEquals(entity.getStatusCode(), HttpStatus.OK);

		String body = entity.getBody();

		assertNotNull(body);

		log.info("{} -> http://127.0.0.1:45678/{}: {}", url, uuid, body);

		GatewayApplicationTests.queryForList(jdbcTemplate);
	}

}
