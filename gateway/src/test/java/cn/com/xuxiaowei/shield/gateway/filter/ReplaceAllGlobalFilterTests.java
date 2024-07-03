package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.properties.ReplaceAll;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.com.xuxiaowei.shield.gateway.filter.ReplaceAllGlobalFilterTests.HOST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 替换过滤器 测试类
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        // http://localhost:45678/query?abc=xyz：会返回请求参数
        "spring.cloud.gateway.routes[0].id=demo-5",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678/",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=" + HOST + ":*",
})
// @formatter:on
class ReplaceAllGlobalFilterTests {

	static final String HOST = "demo-5.localdev.me";

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@LocalServerPort
	private int serverPort;

	@Test
	void filter() throws JsonProcessingException {

		String regex = "56";
		String replacement = "00";

		ReplaceAll replaceAll = new ReplaceAll();
		replaceAll.setHost(HOST);
		replaceAll.setPatterns(List.of("/**"));
		replaceAll.setRegex(regex);
		replaceAll.setReplacement(replacement);

		List<String> replaceAllType = List.of("application/json");

		ObjectMapper objectMapper = new ObjectMapper();
		String replaceAllStr = objectMapper.writeValueAsString(replaceAll);
		String replaceAllTypeStr = objectMapper.writeValueAsString(replaceAllType);

		stringRedisTemplate.opsForValue().set(ReplaceAllGlobalFilter.REDIS_KEY + "demo-5", replaceAllStr);
		stringRedisTemplate.opsForValue().set(ReplaceAllGlobalFilter.TYPE_REDIS_KEY, replaceAllTypeStr);

		String name = UUID.randomUUID().toString();
		String value = "1234567890";

		String url = String.format("http://%s:%s/query?%s=%s", HOST, serverPort, name, value);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);

		assertNotNull(entity);

		Map<String, String> map = entity.getBody();

		assertNotNull(map);

		String query = map.get("query");

		assertNotNull(query);
		assertEquals(name + "=" + value.replaceAll(regex, replacement), query);
	}

}
