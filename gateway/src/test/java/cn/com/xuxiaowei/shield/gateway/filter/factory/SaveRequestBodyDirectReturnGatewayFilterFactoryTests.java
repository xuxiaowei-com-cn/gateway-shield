package cn.com.xuxiaowei.shield.gateway.filter.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.SaveRequestBodyDirectReturnGatewayFilterFactory.SAVE_REQUEST_BODY_DIRECT_RETURN_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see SaveRequestBodyDirectReturnGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo-SaveRequestBodyDirectReturn",
        "spring.cloud.gateway.routes[0].uri=http://localhost:1111/",
		"spring.cloud.gateway.routes[0].predicates[0]=Host=demo-SaveRequestBodyDirectReturn.localdev.me:*",
		"spring.cloud.gateway.routes[0].filters[0].name=SaveRequestBodyDirectReturn",
		"spring.cloud.gateway.routes[0].filters[0].args.paths=2208e015-e8a8-4e9d-96a9-f0e7ef0002ec",
})
// @formatter:on
class SaveRequestBodyDirectReturnGatewayFilterFactoryTests {

	private static final String SQL = "SELECT request_body FROM gateway_shield_log WHERE request_id = ?";

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void apply() throws JsonProcessingException {

		String url = String.format(
				"http://demo-SaveRequestBodyDirectReturn.localdev.me:%s/2208e015-e8a8-4e9d-96a9-f0e7ef0002ec",
				serverPort);

		RestTemplate restTemplate = new RestTemplate();

		String name = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();

		HttpHeaders requestHeaders = new HttpHeaders();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put(name, value);
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, requestHeaders);
		ResponseEntity<String> entity = restTemplate.postForEntity(url, httpEntity, String.class);

		String body = entity.getBody();
		assertNull(body);

		HttpHeaders headers = entity.getHeaders();
		assertNotNull(headers);

		List<String> list = headers.get(SAVE_REQUEST_BODY_DIRECT_RETURN_ID);
		assertNotNull(list);
		assertEquals(1, list.size());

		ObjectMapper objectMapper = new ObjectMapper();
		String string = objectMapper.writeValueAsString(requestBody);
		assertNotNull(string);

		String id = list.get(0);

		String string1 = jdbcTemplate.queryForObject(SQL, new Object[] { id }, new int[] { Types.VARCHAR },
				String.class);
		assertNotNull(string1);
		assertEquals(string, string1);
	}

}
