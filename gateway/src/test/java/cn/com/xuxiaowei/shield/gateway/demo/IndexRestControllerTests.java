package cn.com.xuxiaowei.shield.gateway.demo;

import cn.com.xuxiaowei.shield.gateway.GatewayApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @see org.springframework.http.HttpHeaders
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
		"spring.cloud.gateway.routes[0].id=demo",
		"spring.cloud.gateway.routes[0].uri=http://localhost:45678",
		"spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*"
})
// @formatter:on
class IndexRestControllerTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@SneakyThrows
	@Test
	void index() {

		String proto = "http";
		String host = "demo.localdev.me:" + serverPort;
		String url = String.format("%s://%s/header", proto, host);

		RestTemplate restTemplate = new RestTemplate();

		Map map = restTemplate.getForObject(url, Map.class);

		assertNotNull(map);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
		String value = objectWriter.writeValueAsString(map);

		log.info("Headers: \n{}", value);

		assertEquals(List.of(proto), map.get("x-forwarded-proto"));
		assertEquals(List.of(host), map.get("x-forwarded-host"));

		GatewayApplicationTests.queryForList(jdbcTemplate);
	}

	@SneakyThrows
	@Test
	void headerNamesAuthorization() {

		String url = String.format("http://demo.localdev.me:%s/header", serverPort);

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setBasicAuth("admin", "password");
		Map<String, Object> requestBody = new HashMap<>();
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
		Map map = restTemplate.postForObject(url, httpEntity, Map.class);

		assertNotNull(map);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
		String value = objectWriter.writeValueAsString(map);

		log.info("Headers: \n{}", value);

		assertNotNull(map.get(HttpHeaders.AUTHORIZATION.toLowerCase()));

		List<String> list = httpHeaders.get(HttpHeaders.AUTHORIZATION);
		assertEquals(list, map.get(HttpHeaders.AUTHORIZATION.toLowerCase()));
	}

}
