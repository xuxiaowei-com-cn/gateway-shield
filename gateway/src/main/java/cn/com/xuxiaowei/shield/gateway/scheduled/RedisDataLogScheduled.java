package cn.com.xuxiaowei.shield.gateway.scheduled;

import cn.com.xuxiaowei.shield.gateway.constant.LogConstants;
import cn.com.xuxiaowei.shield.gateway.filter.ReplaceAllGlobalFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis 数据日志
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Component
public class RedisDataLogScheduled {

	private static final String ROUTEDEFINITION_REDIS_KEY_PREFIX_QUERY = "routedefinition_";

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Schedules(value = { @Scheduled(initialDelay = 10_000), @Scheduled(cron = "0 0 * * * ?") })
	public void replaceAll() {
		String id = RandomStringUtils.randomAlphanumeric(8);
		MDC.put(LogConstants.G_REQUEST_ID, id);
		Set<String> keys = stringRedisTemplate.keys(ReplaceAllGlobalFilter.REDIS_KEY + "*");
		log.info("ReplaceAll keys: {}", keys == null ? null : keys.size());
		if (keys != null && !keys.isEmpty()) {
			for (String key : keys) {
				log.info("ReplaceAll key: {}", key);
				String value = stringRedisTemplate.opsForValue().get(key);
				log.info("ReplaceAll value: {}", value);
			}
		}
	}

	@Schedules(value = { @Scheduled(initialDelay = 10_000), @Scheduled(cron = "0 0 * * * ?") })
	public void routes() {
		String id = RandomStringUtils.randomAlphanumeric(8);
		MDC.put(LogConstants.G_REQUEST_ID, id);
		Set<String> keys = stringRedisTemplate.keys(ROUTEDEFINITION_REDIS_KEY_PREFIX_QUERY + "*");
		log.info("Routes keys: {}", keys == null ? null : keys.size());
		if (keys != null && !keys.isEmpty()) {
			for (String key : keys) {
				log.info("Routes key: {}", key);
				String value = stringRedisTemplate.opsForValue().get(key);
				log.info("Routes value: {}", value);
			}
		}
	}

}
