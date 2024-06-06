package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.GatewayApplicationTests;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.actuate.AbstractGatewayControllerEndpoint;
import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.cloud.gateway.actuate.GatewayLegacyControllerEndpoint;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RedisRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 使用 Redis 储存 网关 route 路由配置 测试类
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see AbstractGatewayControllerEndpoint
 * @see GatewayControllerEndpoint
 * @see GatewayLegacyControllerEndpoint
 * @see org.springframework.cloud.gateway.config.GatewayRedisAutoConfiguration
 * @see org.springframework.cloud.gateway.config.GatewayRedisAutoConfiguration#redisRouteDefinitionRepository(ReactiveRedisTemplate)
 */
@Slf4j
@Import({ RedisRouteDefinitionRepositoryConfig.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisRouteDefinitionRepositoryConfigTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@SneakyThrows
	@Test
	void sugrec() {
		String prefix = RandomStringUtils.randomAlphabetic(4).toLowerCase();

		// 初始化 Redis 中的路由
		init(prefix);

		// 如果在创建 RedisRouteDefinitionRepository Bean 之前，Redis 已经有路由数据了，直接读取Redis数据，此处无需刷新。
		// 如果在创建 RedisRouteDefinitionRepository Bean 之后，Redis 数据路由发生变更，需要刷新路由，才可以读取到最新的配置。
		// 如果引入了 org.springframework.boot:spring-boot-starter-actuator 依赖，
		// 可以调用 /actuator/gateway/refresh 接口刷新路由配置。
		applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));

		// 增加订阅程序处理时间，防止自动化流水线偶尔出现异常
		Thread.sleep(5_000);

		String url = String.format("http://%s.localdev.me:%s/sugrec", prefix, serverPort);

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

	/**
	 * 初始化 Redis 中的路由
	 * @see RedisRouteDefinitionRepository
	 * @see AbstractGatewayControllerEndpoint
	 * @see GatewayControllerEndpoint
	 * @see GatewayLegacyControllerEndpoint
	 * @see org.springframework.cloud.gateway.config.GatewayRedisAutoConfiguration
	 * @see org.springframework.cloud.gateway.config.GatewayRedisAutoConfiguration#redisRouteDefinitionRepository(ReactiveRedisTemplate)
	 */
	@SneakyThrows
	public void init(String prefix) {
		String key = "routedefinition_:" + prefix;

		RouteDefinition routeDefinition = new RouteDefinition();
		routeDefinition.setId(prefix);
		routeDefinition.setUri(new URI("https://www.baidu.com"));

		PredicateDefinition predicateDefinition = new PredicateDefinition();
		predicateDefinition.setName("Host");
		Map<String, String> args = new HashMap<>();
		args.put("a1", prefix + ".localdev.me:*");
		predicateDefinition.setArgs(args);

		routeDefinition.setPredicates(List.of(predicateDefinition));

		ObjectMapper objectMapper = new ObjectMapper();
		String routeDefinitionStr = objectMapper.writeValueAsString(routeDefinition);
		log.info("routeDefinition: {}", routeDefinitionStr);

		stringRedisTemplate.opsForValue().set(key, routeDefinitionStr);
	}

}
