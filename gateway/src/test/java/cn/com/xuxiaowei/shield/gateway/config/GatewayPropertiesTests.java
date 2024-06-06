package cn.com.xuxiaowei.shield.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关配置 测试类
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
class GatewayPropertiesTests {

	/**
     * // @formatter:off
     * spring:
     *   cloud:
     *     gateway:
     *       routes:
     *         - id: baidu
     *           uri: https://www.baidu.com
     *           predicates:
     *             - Host=baidu.localdev.me:*
     *           filters:
     *             - name: AllowIPAccess
     *               args:
     *                 cidr: 127.0.0.1,10.0.0.0/8,172.16.0.0/12,192.168.0.0/16
     * // @formatter:on
     */
	@Test
	void allowIPAccess() throws URISyntaxException, JsonProcessingException {

		GatewayProperties properties = new GatewayProperties();

		RouteDefinition routeDefinition = new RouteDefinition();
		routeDefinition.setId("baidu");
		routeDefinition.setUri(new URI("https://www.baidu.com"));

		PredicateDefinition predicateDefinition = new PredicateDefinition();
		predicateDefinition.setName("Host");
		Map<String, String> pdMap = new HashMap<>();
		pdMap.put("a1", "baidu.localdev.me:*");
		predicateDefinition.setArgs(pdMap);

		routeDefinition.setPredicates(List.of(predicateDefinition));

		FilterDefinition filterDefinition = new FilterDefinition();
		filterDefinition.setName("AllowIPAccess");
		Map<String, String> fdMap = new HashMap<>();
		fdMap.put("cidr", "127.0.0.1,10.0.0.0/8,172.16.0.0/12,192.168.0.0/16");
		filterDefinition.setArgs(fdMap);

		routeDefinition.setFilters(List.of(filterDefinition));

		properties.setRoutes(List.of(routeDefinition));

		ObjectMapper objectMapper = new ObjectMapper();

		for (RouteDefinition rd : properties.getRoutes()) {
			String value = objectMapper.writeValueAsString(rd);
			log.info(value);
		}
	}

}
