package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.properties.GatewayShieldProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HTTP 端口 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpPortConfigTests {

	@Autowired
	private GatewayShieldProperties gatewayShieldProperties;

	@Test
	void start() {
		Integer httpPort = gatewayShieldProperties.getHttpPort();
		String url = String.format("http://127.0.0.1:%s/%s", httpPort, UUID.randomUUID());

		RestTemplate restTemplate = new RestTemplate();

		Exception exception = null;

		try {
			restTemplate.getForEntity(url, String.class);
		}
		catch (HttpClientErrorException.NotFound e) {
			exception = e;
			log.error("HTTP 端口 配置 测试", e);
		}

		assertNotNull(exception);
		assertTrue(exception instanceof HttpClientErrorException.NotFound);
	}

}
