package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.GatewayApplicationTests;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 自定义 {@link Bean} 实现 spring.cloud.gateway.routes 配置 测试类
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Import({ PropertiesRouteDefinitionLocatorConfig.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PropertiesRouteDefinitionLocatorConfigTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@SneakyThrows
	@Test
	void sugrec() {

		String url = String.format("http://test-config.localdev.me:%s/sugrec", serverPort);

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

		GatewayApplicationTests.queryForList(jdbcTemplate);
	}

}
