package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.MapRequestHeaderGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.MapRequestHeaderGatewayFilterFactoryTests.NAME;
import static cn.com.xuxiaowei.shield.gateway.filter.factory.MapRequestHeaderGatewayFilterFactoryTests.TO_NAME;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see MapRequestHeaderGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=MapRequestHeader",
        "spring.cloud.gateway.routes[0].filters[0].args.fromHeader=" + NAME,
        "spring.cloud.gateway.routes[0].filters[0].args.toHeader=" + TO_NAME,
})
// @formatter:on
class MapRequestHeaderGatewayFilterFactoryTests {

	static final String NAME = "abc-xuxiaowei";

	static final String TO_NAME = "abc-zhangsan";

	@LocalServerPort
	private int serverPort;

	@SneakyThrows
	@Test
	void apply() {

		String url = String.format("http://demo.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		String value = UUID.randomUUID().toString();

		HttpHeaders headers = new HttpHeaders();
		headers.set(NAME, value);
		HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(headers);
		Map map = restTemplate.postForObject(url, httpEntity, Map.class);

		assertNotNull(map);

		Object valueObj = map.get(NAME);

		assertNotNull(valueObj);
		assertInstanceOf(List.class, valueObj);

		List<String> list = (List<String>) valueObj;
		assertEquals(1, list.size());
		assertEquals(value, list.get(0));

		Object toValueObj = map.get(TO_NAME);

		assertNotNull(toValueObj);
		assertEquals(valueObj, toValueObj);
	}

	@Test
	void ua() {

		String url = String.format("http://demo.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		String value = UUID.randomUUID().toString();
		String toValue = UUID.randomUUID().toString();

		HttpHeaders headers = new HttpHeaders();
		headers.set(NAME, value);
		headers.set(TO_NAME, toValue);
		HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(headers);
		Map map = restTemplate.postForObject(url, httpEntity, Map.class);

		assertNotNull(map);

		Object valueObj = map.get(NAME);

		assertNotNull(valueObj);
		assertInstanceOf(List.class, valueObj);

		List<String> list = (List<String>) valueObj;
		assertEquals(1, list.size());
		assertEquals(value, list.get(0));

		Object toValueObj = map.get(TO_NAME);

		assertNotNull(toValueObj);
		assertInstanceOf(List.class, toValueObj);

		List<String> toList = (List<String>) toValueObj;
		assertEquals(2, toList.size());
		assertTrue(toList.contains(toValue));

		assertTrue(toList.contains(value));
	}

}
