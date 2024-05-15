package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.RemoveRequestHeaderGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.RemoveRequestHeaderGatewayFilterFactoryTests.NAME;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see RemoveRequestHeaderGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678/",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=RemoveRequestHeader",
        "spring.cloud.gateway.routes[0].filters[0].args.name=" + NAME,
})
// @formatter:on
class RemoveRequestHeaderGatewayFilterFactoryTests {

	static final String NAME = "abcffgg";

	@LocalServerPort
	private int serverPort;

	@SneakyThrows
	@Test
	void apply() {

		String url = String.format("http://demo.localdev.me:%s/header", serverPort);

		String name = "kkllkp";
		String value = UUID.randomUUID().toString();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set(NAME, "abff456456");
		headers.set(name, value);
		HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(headers);
		Map map = restTemplate.postForObject(url, httpEntity, Map.class);

		assertNotNull(map);

		Object object = map.get(NAME);
		Object valueObj = map.get(name);

		assertNull(object);

		assertNotNull(valueObj);
		assertInstanceOf(List.class, valueObj);

		List<String> list = (List<String>) valueObj;
		assertEquals(1, list.size());
		assertEquals(value, list.get(0));

	}

}
