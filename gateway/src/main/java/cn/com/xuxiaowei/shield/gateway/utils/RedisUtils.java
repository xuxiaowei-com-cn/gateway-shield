package cn.com.xuxiaowei.shield.gateway.utils;

import cn.com.xuxiaowei.shield.gateway.constant.RedisConstants;
import cn.com.xuxiaowei.shield.gateway.exception.GatewayShieldRuntimeException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Properties;

/**
 * Redis 工具类
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class RedisUtils {

	/**
	 * 获取 Redis 版本
	 * @param redisAccessor Redis
	 * 连接，如：{@link RedisTemplate}、{@link RedisTemplate}、{@link StringRedisTemplate}
	 * @return 返回 Redis 版本
	 */
	public static String redisVersion(RedisAccessor redisAccessor) {
		RedisConnectionFactory connectionFactory = redisAccessor.getConnectionFactory();

		if (connectionFactory == null) {
			throw new GatewayShieldRuntimeException("Redis 连接工厂 为 null");
		}

		RedisConnection connection = connectionFactory.getConnection();
		RedisServerCommands serverCommands = connection.serverCommands();
		Properties info = serverCommands.info();

		if (info == null) {
			throw new GatewayShieldRuntimeException("Redis 连接属性 为 null");
		}

		return info.getProperty(RedisConstants.REDIS_VERSION);
	}

}
