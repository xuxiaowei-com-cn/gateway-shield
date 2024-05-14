package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.GatewayApplicationTests;
import cn.com.xuxiaowei.shield.gateway.constant.LogConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
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
public class RequestHeaderGlobalFilterTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void filter() throws IOException {
		String proto = "http";
		String host = "demo.localdev.me:" + serverPort;
		String url = String.format("%s://%s", proto, host);

		RestTemplate restTemplate = new RestTemplate();

		Map<String, List<String>> map = restTemplate.getForObject(url, Map.class);

		assertNotNull(map);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
		String value = objectWriter.writeValueAsString(map);

		log.info("Headers: \n{}", value);

		assertEquals(List.of(proto), map.get("x-forwarded-proto"));
		assertEquals(List.of(host), map.get("x-forwarded-host"));

		assertEquals(1, map.get(LogConstants.G_REQUEST_ID.toLowerCase()).size());
		assertEquals(1, map.get(LogConstants.G_HOST_NAME.toLowerCase()).size());
		assertEquals(1, map.get(LogConstants.G_HOST_ADDRESS.toLowerCase()).size());

		GatewayApplicationTests.queryForList(jdbcTemplate);
	}

}
