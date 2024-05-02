package cn.com.xuxiaowei.shield.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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
		"gateway-shield.refresh-routes-event-path=" + RefreshRoutesEventFilterOkTests.PATH,
		"gateway-shield.refresh-routes-event-cidr=127.0.0.1"
})
// @formatter:on
class RefreshRoutesEventFilterOkTests {

	static final String PATH = "/refresh-routes-event-path";

	@LocalServerPort
	private int serverPort;

	@Test
	void filter() {

		String url = String.format("http://127.0.0.1:%s%s", serverPort, PATH);

		RestTemplate restTemplate = new RestTemplate();

		Map map = restTemplate.getForObject(url, Map.class);

		assertNotNull(map);
		assertNotNull(map.get("msg"));
		assertEquals("刷新路由成功", map.get("msg"));
	}

}
