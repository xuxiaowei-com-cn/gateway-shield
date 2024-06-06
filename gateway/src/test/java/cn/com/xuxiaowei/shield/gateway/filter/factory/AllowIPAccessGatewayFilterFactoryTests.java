package cn.com.xuxiaowei.shield.gateway.filter.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 允许 IP 访问 测试类
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see AllowIPAccessGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=allow-baidu",
        "spring.cloud.gateway.routes[0].uri=https://www.baidu.com",
		"spring.cloud.gateway.routes[0].predicates[0]=Host=allow.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=AllowIPAccess",
        "spring.cloud.gateway.routes[0].filters[0].args.cidrs[0]=127.0.0.1",

        "spring.cloud.gateway.routes[1].id=reject-baidu",
        "spring.cloud.gateway.routes[1].uri=https://www.baidu.com",
		"spring.cloud.gateway.routes[1].predicates[0]=Host=reject.localdev.me:*",
        "spring.cloud.gateway.routes[1].filters[0].name=AllowIPAccess",
        "spring.cloud.gateway.routes[1].filters[0].args.cidrs[0]=100.100.100.100",
})
// @formatter:on
class AllowIPAccessGatewayFilterFactoryTests {

	@LocalServerPort
	private int serverPort;

	@Test
	void allow() throws JsonProcessingException {

		String url = String.format("http://allow.localdev.me:%s/sugrec", serverPort);

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

	}

	@Test
	void reject() {

		String url = String.format("http://reject.localdev.me:%s/sugrec", serverPort);

		RestTemplate restTemplate = new RestTemplate();

		boolean exception = false;

		try {
			restTemplate.getForEntity(url, String.class);
		}
		catch (HttpClientErrorException.Forbidden e) {
			log.error("访问：{} 被拒绝", url, e);
			exception = true;
		}

		assertTrue(exception);
	}

}
