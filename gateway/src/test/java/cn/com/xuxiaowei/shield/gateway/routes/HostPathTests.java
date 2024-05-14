package cn.com.xuxiaowei.shield.gateway.routes;

import cn.com.xuxiaowei.shield.gateway.GatewayApplicationTests;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

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
        "spring.cloud.gateway.routes[0].id=test-baidu-sugrec",
        "spring.cloud.gateway.routes[0].uri=https://www.baidu.com",
        "spring.cloud.gateway.routes[0].predicates[0]=Path=/sugrec",
        "spring.cloud.gateway.routes[0].predicates[1]=Host=test-baidu.localdev.me:*",
        "spring.cloud.gateway.routes[1].id=test-baidu-a.js",
        "spring.cloud.gateway.routes[1].uri=https://hector.baidu.com",
        "spring.cloud.gateway.routes[1].predicates[0]=Path=/a.js",
        "spring.cloud.gateway.routes[1].predicates[1]=Host=test-baidu.localdev.me:*"
})
// @formatter:on
class HostPathTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@SneakyThrows
	@Test
	void testBaidu() {

		String url = String.format("http://test-baidu.localdev.me:%s/sugrec", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

		assertEquals(entity.getStatusCode(), HttpStatus.OK);

		String body = entity.getBody();

		assertNotNull(body);

		log.info("{} -> https://www.baidu.com/sugrec: {}", url, body);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> map = objectMapper.readValue(body, new TypeReference<>() {
		});

		assertEquals(0, map.get("err_no"));
		assertEquals("", map.get("errmsg"));
		assertNotNull(map.get("queryid"));

		String jsUrl = String.format("http://test-baidu.localdev.me:%s/a.js", serverPort);

		String js = restTemplate.getForObject(jsUrl, String.class);

		assertNotNull(js);

		GatewayApplicationTests.queryForList(jdbcTemplate);

	}

}
