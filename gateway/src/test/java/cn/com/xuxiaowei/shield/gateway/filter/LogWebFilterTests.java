package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.GatewayApplicationTests;
import cn.com.xuxiaowei.shield.gateway.utils.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogWebFilterTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ConfigurableEnvironment environment;

	private Pattern pattern = Pattern.compile(": (.*?) : (.*?) : (.*?) :");

	@Test
	void filter() throws IOException {
		String loggingFileName = environment.getProperty("logging.file.name");

		assertNotNull(loggingFileName);

		List<String> list = FileUtils.readList(loggingFileName);
		int startLine = list.size();

		String proto = "http";
		String host = "demo.localdev.me:" + serverPort;
		String url = String.format("%s://%s", proto, host);

		RestTemplate restTemplate = new RestTemplate();

		Map map = restTemplate.getForObject(url, Map.class);

		assertNotNull(map);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
		String value = objectWriter.writeValueAsString(map);

		log.info("Headers: \n{}", value);

		assertEquals(List.of(proto), map.get("x-forwarded-proto"));
		assertEquals(List.of(host), map.get("x-forwarded-host"));

		list = FileUtils.readList(loggingFileName);
		int endLine = list.size();

		boolean contains = false;

		for (int i = startLine; i < endLine; i++) {
			String line = list.get(i);
			if (line.contains(url)) {
				contains = true;

				Matcher matcher = pattern.matcher(line);
				assertTrue(matcher.find());

				String requestId = matcher.group(1);
				String hostName = matcher.group(2);
				String hostAddress = matcher.group(3);

				assertNotNull(requestId);
				assertNotNull(hostName);
				assertNotNull(hostAddress);

				break;
			}
		}

		assertTrue(contains);

		GatewayApplicationTests.queryForList(jdbcTemplate);
	}

}
