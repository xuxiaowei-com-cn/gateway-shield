package cn.com.xuxiaowei.shield.gateway.config;

import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 * 自定义 {@link Bean} 实现 spring.cloud.gateway.routes 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class PropertiesRouteDefinitionLocatorConfig {

	/**
	 * 自定义 {@link Bean} 实现 spring.cloud.gateway.routes 配置
	 * 等价于配置
	 * // @formatter:off
	 * spring:
	 *   cloud:
	 *     gateway:
	 *       routes:
	 *         - id: test-config
	 *           uri: https://www.baidu.com
	 *           predicates:
	 *             # http://baidu.localdev.me:45450/sugrec
	 *             # 测试类中的 server.port 为 0
	 *             # * 代表所有端口
	 *             - Host=test-config.localdev.me:*
	 * // @formatter:on
	 * @see GatewayAutoConfiguration#propertiesRouteDefinitionLocator(GatewayProperties)
	 */
	@Bean
	public PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator() throws URISyntaxException {

		GatewayProperties properties = new GatewayProperties();

		RouteDefinition routeDefinition = new RouteDefinition();
		routeDefinition.setId("test-config");
		routeDefinition.setUri(new URI("https://www.baidu.com"));

		PredicateDefinition predicateDefinition = new PredicateDefinition();
		predicateDefinition.setName("Host");
		HashMap<String, String> map = new HashMap<>();
		map.put("a1", "test-config.localdev.me:*");
		predicateDefinition.setArgs(map);

		routeDefinition.setPredicates(List.of(predicateDefinition));

		properties.setRoutes(List.of(routeDefinition));

		return new PropertiesRouteDefinitionLocator(properties);
	}

}
