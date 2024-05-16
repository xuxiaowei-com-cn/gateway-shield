package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.AddResponseHeaderGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.AddResponseHeaderGatewayFilterFactoryTests.NAME;
import static cn.com.xuxiaowei.shield.gateway.filter.factory.AddResponseHeaderGatewayFilterFactoryTests.VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see AddResponseHeaderGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=AddResponseHeader",
        "spring.cloud.gateway.routes[0].filters[0].args.name=" + NAME,
        "spring.cloud.gateway.routes[0].filters[0].args.value=" + VALUE,
})
// @formatter:on
class AddResponseHeaderGatewayFilterFactoryTests {

	static final String NAME = "abc-xuxiaowei";

	static final String VALUE = "xuxiaowei.com.cn";

	@LocalServerPort
	private int serverPort;

	@SneakyThrows
	@Test
	void apply() {

		String url = String.format("http://demo.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> entity = restTemplate.postForEntity(url, null, Map.class);

		assertNotNull(entity);

		HttpHeaders headers = entity.getHeaders();

		List<String> list = headers.get(NAME);

		assertNotNull(list);

		assertEquals(1, list.size());
		assertEquals(VALUE, list.get(0));
	}

}
