package cn.com.xuxiaowei.shield.gateway.filter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JavaScriptAddResponseHeaderGatewayFilterFactoryTests {

	@LocalServerPort
	private int serverPort;

	@SneakyThrows
	@Test
	void apply() {

		String url = String.format("http://hector.localdev.me:%s/a.js", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

		assertNotNull(entity);
		assertEquals(HttpStatus.OK, entity.getStatusCode());

		HttpHeaders headers = entity.getHeaders();
		String cacheControl = headers.getCacheControl();

		assertNotNull(cacheControl);

		log.info("{}: \n{}", HttpHeaders.CACHE_CONTROL, cacheControl);
	}

}
