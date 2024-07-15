package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 允许 IP 访问 测试类
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see AllowIPAccessGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=allow",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
		"spring.cloud.gateway.routes[0].predicates[0]=Host=allow.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=AllowIPAccess",
        "spring.cloud.gateway.routes[0].filters[0].args.cidr=127.0.0.1",

        "spring.cloud.gateway.routes[1].id=reject",
        "spring.cloud.gateway.routes[1].uri=https://www.baidu.com",
		"spring.cloud.gateway.routes[1].predicates[0]=Host=reject.localdev.me:*",
        "spring.cloud.gateway.routes[1].filters[0].name=AllowIPAccess",
        "spring.cloud.gateway.routes[1].filters[0].args.cidr=100.100.100.100",
})
// @formatter:on
class AllowIPAccessGatewayFilterFactoryTests {

	@LocalServerPort
	private int serverPort;

	@Test
	void allow() {

		String url = String.format("http://allow.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

		assertEquals(entity.getStatusCode(), HttpStatus.OK);

		String body = entity.getBody();

		assertNotNull(body);
	}

	@Test
	void reject() {

		String url = String.format("http://reject.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		boolean exception = false;

		try {
			restTemplate.getForEntity(url, String.class);
		}
		catch (HttpClientErrorException.Forbidden e) {
			log.error("访问：{} 被拒绝", url, e);
			exception = true;
		}

		assertTrue(exception);
	}

}
