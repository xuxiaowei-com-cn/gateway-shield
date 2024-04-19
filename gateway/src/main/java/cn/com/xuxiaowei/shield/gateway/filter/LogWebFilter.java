package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.constant.LogConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

/**
 * 日志 过滤器
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Setter
@Component
public class LogWebFilter implements WebFilter, Ordered {

	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE;

	private static final String sql = "INSERT INTO `gateway_shield`.`gateway_shield_log` (`gateway_shield_log_id`, `request_id`, `scheme`, `host_name`, `host_address`, `port`, `path`, `query`, `raw_query`, `type`, `user_agent`, `referer`, `headers_json`, `year`, `month`, `day`, `hour`, `minute`, `second`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private JdbcTemplate jdbcTemplate;

	private StringRedisTemplate stringRedisTemplate;

	private int order = ORDERED;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Autowired
	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@SneakyThrows
	@NonNull
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();

		String id = request.getId();
		URI uri = request.getURI();
		InetSocketAddress remoteAddress = request.getRemoteAddress();
		InetAddress address = remoteAddress.getAddress();
		String hostName = address.getHostName();
		String hostAddress = address.getHostAddress();

		MDC.put(LogConstants.G_REQUEST_ID, id);
		MDC.put(LogConstants.G_HOST_NAME, hostName);
		MDC.put(LogConstants.G_HOST_ADDRESS, hostAddress);

		log.debug("URI: {}, {}: {}, {}: {}", uri, LogConstants.G_HOST_NAME, hostName, LogConstants.G_HOST_ADDRESS,
				hostAddress);

		save(exchange);

		// String redisVersion = RedisUtils.redisVersion(stringRedisTemplate);
		// log.info("redisVersion: {}", redisVersion);

		return chain.filter(exchange);
	}

	private void save(ServerWebExchange exchange) throws JsonProcessingException {

		ServerHttpRequest request = exchange.getRequest();

		String id = request.getId();
		URI uri = request.getURI();
		String scheme = uri.getScheme();
		String query = uri.getQuery();
		String rawQuery = uri.getRawQuery();
		int port = uri.getPort();
		String path = uri.getPath();

		String type = "";

		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		Month month = now.getMonth();
		int monthValue = month.getValue();
		int dayOfMonth = now.getDayOfMonth();
		int hour = now.getHour();
		int minute = now.getMinute();
		int second = now.getSecond();

		HttpHeaders headers = request.getHeaders();

		String userAgent = headers.getFirst(HttpHeaders.USER_AGENT);
		String referer = headers.getFirst(HttpHeaders.REFERER);

		ObjectMapper objectMapper = new ObjectMapper();
		String headersJson = objectMapper.writeValueAsString(headers);

		InetSocketAddress remoteAddress = request.getRemoteAddress();
		InetAddress address = remoteAddress.getAddress();
		String hostName = address.getHostName();
		String hostAddress = address.getHostAddress();

		// @formatter:off
		SqlParameterValue[] parameters = new SqlParameterValue[] {
				new SqlParameterValue(Types.VARCHAR, UUID.randomUUID().toString()),
				new SqlParameterValue(Types.VARCHAR, id),
				new SqlParameterValue(Types.VARCHAR, scheme),
				new SqlParameterValue(Types.VARCHAR, hostName),
				new SqlParameterValue(Types.VARCHAR, hostAddress),
				new SqlParameterValue(Types.INTEGER, port),
				new SqlParameterValue(Types.VARCHAR, path),
				new SqlParameterValue(Types.VARCHAR, query),
				new SqlParameterValue(Types.VARCHAR, rawQuery),
				new SqlParameterValue(Types.VARCHAR, type),
				new SqlParameterValue(Types.VARCHAR, userAgent),
				new SqlParameterValue(Types.VARCHAR, referer),
				new SqlParameterValue(Types.VARCHAR, headersJson),
				new SqlParameterValue(Types.INTEGER, year),
				new SqlParameterValue(Types.INTEGER, monthValue),
				new SqlParameterValue(Types.INTEGER, dayOfMonth),
				new SqlParameterValue(Types.INTEGER, hour),
				new SqlParameterValue(Types.INTEGER, minute),
				new SqlParameterValue(Types.INTEGER, second)
		};
		// @formatter:on

		PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(parameters);
		jdbcTemplate.update(sql, pss);
	}

}
