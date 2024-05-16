package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.SetResponseHeaderGatewayFilterFactoryTests.NAME;
import static cn.com.xuxiaowei.shield.gateway.filter.factory.SetResponseHeaderGatewayFilterFactoryTests.VALUE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see SetResponseHeaderGatewayFilterFactoryTests
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=SetResponseHeader",
        "spring.cloud.gateway.routes[0].filters[0].args.name=" + NAME,
        "spring.cloud.gateway.routes[0].filters[0].args.value=" + VALUE,

        "spring.cloud.gateway.routes[1].id=ua",
        "spring.cloud.gateway.routes[1].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[1].predicates[0]=Host=ua.localdev.me:*",
})
// @formatter:on
class SetResponseHeaderGatewayFilterFactoryTests {

	static final String NAME = "Content-Type";

	static final String VALUE = "application/json;charset=utf-8";

	@LocalServerPort
	private int serverPort;

	@SneakyThrows
	@Test
	void apply() {

		String url = String.format("http://demo.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);

		assertNotNull(entity);

		HttpHeaders headers = entity.getHeaders();

		assertNotNull(headers);

		Object valueObj = headers.get(NAME);

		assertNotNull(valueObj);
		assertInstanceOf(List.class, valueObj);

		List<String> list = (List<String>) valueObj;
		assertEquals(1, list.size());
		assertTrue(list.contains(VALUE));
	}

	@Test
	void ua() {

		String url = String.format("http://ua.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);

		assertNotNull(entity);

		HttpHeaders headers = entity.getHeaders();

		assertNotNull(headers);

		Object valueObj = headers.get(NAME);

		assertNotNull(valueObj);
		assertInstanceOf(List.class, valueObj);

		List<String> list = (List<String>) valueObj;
		assertEquals(1, list.size());
		assertFalse(list.contains(VALUE));
	}

}
