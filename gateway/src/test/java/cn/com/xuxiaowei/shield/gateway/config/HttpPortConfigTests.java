package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.properties.GatewayShieldProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * HTTP 端口 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@TestPropertySource(properties = { "gateway-shield.http-port=46655",
		"gateway-shield.enable-gateway-error-web-exception-handler=true" })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpPortConfigTests {

	@Autowired
	private GatewayShieldProperties gatewayShieldProperties;

	@Test
	void start() {
		Integer httpPort = gatewayShieldProperties.getHttpPort();
		String url = String.format("http://127.0.0.1:%s/%s", httpPort, UUID.randomUUID());

		RestTemplate restTemplate = new RestTemplate();

		String string = restTemplate.getForObject(url, String.class);

		assertNotNull(string);

		String nonExistRouteMessage = gatewayShieldProperties.getNonExistRouteMessage();
		assertNotNull(nonExistRouteMessage);

		assertEquals(nonExistRouteMessage, string);
	}

}
