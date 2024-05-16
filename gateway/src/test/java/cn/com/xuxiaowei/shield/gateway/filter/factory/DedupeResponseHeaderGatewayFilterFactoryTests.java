package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see DedupeResponseHeaderGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678/",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=DedupeResponseHeader",
        "spring.cloud.gateway.routes[0].filters[0].args.name=" + ACCESS_CONTROL_ALLOW_CREDENTIALS + " " + ACCESS_CONTROL_ALLOW_ORIGIN,

		"spring.cloud.gateway.routes[1].id=demo-2",
		"spring.cloud.gateway.routes[1].uri=http://localhost:45678/",
		"spring.cloud.gateway.routes[1].predicates[0]=Host=demo-2.localdev.me:*",
})
// @formatter:on
class DedupeResponseHeaderGatewayFilterFactoryTests {

	@LocalServerPort
	private int serverPort;

	@SneakyThrows
	@Test
	void apply() {

		String url = String.format("http://demo.localdev.me:%s/header/dedupe-response-header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> entity = restTemplate.postForEntity(url, null, Map.class);

		assertNotNull(entity);

		HttpHeaders headers = entity.getHeaders();

		assertNotNull(headers);

		assertEquals(5, headers.size());

		Object accessControlAllowCredentialsObj = headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS);
		Object accessControlAllowOriginObj = headers.get(ACCESS_CONTROL_ALLOW_ORIGIN);

		assertNotNull(accessControlAllowCredentialsObj);
		assertNotNull(accessControlAllowOriginObj);

		assertInstanceOf(List.class, accessControlAllowCredentialsObj);
		assertInstanceOf(List.class, accessControlAllowOriginObj);

		List<String> accessControlAllowCredentialsList = (List<String>) accessControlAllowCredentialsObj;
		List<String> accessControlAllowOriginList = (List<String>) accessControlAllowOriginObj;

		assertEquals(1, accessControlAllowCredentialsList.size());
		assertEquals(1, accessControlAllowOriginList.size());
	}

	@SneakyThrows
	@Test
	void ua() {

		String url = String.format("http://demo-2.localdev.me:%s/header/dedupe-response-header", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> entity = restTemplate.postForEntity(url, null, Map.class);

		assertNotNull(entity);

		HttpHeaders headers = entity.getHeaders();

		assertNotNull(headers);

		assertEquals(5, headers.size());

		Object accessControlAllowCredentialsObj = headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS);
		Object accessControlAllowOriginObj = headers.get(ACCESS_CONTROL_ALLOW_ORIGIN);

		assertNotNull(accessControlAllowCredentialsObj);
		assertNotNull(accessControlAllowOriginObj);

		assertInstanceOf(List.class, accessControlAllowCredentialsObj);
		assertInstanceOf(List.class, accessControlAllowOriginObj);

		List<String> accessControlAllowCredentialsList = (List<String>) accessControlAllowCredentialsObj;
		List<String> accessControlAllowOriginList = (List<String>) accessControlAllowOriginObj;

		assertEquals(2, accessControlAllowCredentialsList.size());
		assertEquals(2, accessControlAllowOriginList.size());
	}

}
