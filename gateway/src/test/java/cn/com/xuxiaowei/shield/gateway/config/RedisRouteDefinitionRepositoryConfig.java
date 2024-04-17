package cn.com.xuxiaowei.shield.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.actuate.AbstractGatewayControllerEndpoint;
import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.cloud.gateway.actuate.GatewayLegacyControllerEndpoint;
import org.springframework.cloud.gateway.route.RedisRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * 使用 Redis 储存 网关 route 路由配置
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
public class RedisRouteDefinitionRepositoryConfig {

	@Bean
	public RedisRouteDefinitionRepository redisRouteDefinitionRepository(
			ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate) {
		return new RedisRouteDefinitionRepository(reactiveRedisTemplate);
	}

}
