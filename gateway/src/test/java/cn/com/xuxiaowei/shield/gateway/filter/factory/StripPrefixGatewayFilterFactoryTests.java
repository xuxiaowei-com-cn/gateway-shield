package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see StripPrefixGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {

		// 将 demo.localdev.me:* 的所有请求转发到 http://localhost:45678，并且删除路径的前1个前缀（使用 / 分割，默认为删除第一个）

        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=StripPrefix",

		// 将 demo.localdev.me:* 的所有请求转发到 http://localhost:45678，并且删除路径的前2个前缀（使用 / 分割）

		"spring.cloud.gateway.routes[1].id=demo-1",
		"spring.cloud.gateway.routes[1].uri=http://localhost:45678",
		"spring.cloud.gateway.routes[1].predicates[0]=Host=demo-1.localdev.me:*",
		"spring.cloud.gateway.routes[1].filters[0].name=StripPrefix",
		"spring.cloud.gateway.routes[1].filters[0].args.parts=2",
})
// @formatter:on
class StripPrefixGatewayFilterFactoryTests {

	@LocalServerPort
	private int serverPort;

	@SneakyThrows
	@Test
	void apply() {

		// @formatter:off
		// 将 demo.localdev.me:* 的所有请求转发到 http://localhost:45678，并且删除路径的前缀（使用 / 分割，默认为删除第一个）
		// 将 http://demo.localdev.me:端口/任何字符串/header 转发到 http://localhost:45678/header
		// @formatter:on

		String url = String.format("http://demo.localdev.me:%s/%s/header", serverPort, UUID.randomUUID());

		log.info("url: {}", url);

		RestTemplate restTemplate = new RestTemplate();

		Map map = restTemplate.getForObject(url, Map.class);

		assertNotNull(map);
	}

	@SneakyThrows
	@Test
	void ua() {

		// @formatter:off
		// 将 demo.localdev.me:* 的所有请求转发到 http://localhost:45678，并且删除路径的前缀（使用 / 分割，默认为删除第一个）
		// 将 http://demo.localdev.me:端口/任何字符串/任何字符串/header 转发到 http://localhost:45678/header
		// @formatter:on

		// @formatter:off
		String url = String.format("http://demo-1.localdev.me:%s/%s/%s/header", serverPort, UUID.randomUUID(), UUID.randomUUID());
		// @formatter:on

		log.info("url: {}", url);

		RestTemplate restTemplate = new RestTemplate();

		Map map = restTemplate.getForObject(url, Map.class);

		assertNotNull(map);
	}

}
