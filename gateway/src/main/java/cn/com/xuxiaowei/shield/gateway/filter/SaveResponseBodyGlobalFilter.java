package cn.com.xuxiaowei.shield.gateway.filter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.sql.Types;
import java.util.List;

/**
 * 保存 响应体
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Setter
@Component
public class SaveResponseBodyGlobalFilter implements GlobalFilter, Ordered {

	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE;

	public static final String REDIS_KEY = SaveResponseBodyGlobalFilter.class.getName() + ":patterns";

	private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

	private static final String SQL = "UPDATE `gateway_shield_log` SET `response_body` = ? WHERE `gateway_shield_log_id` = ?";

	private StringRedisTemplate stringRedisTemplate;

	private JdbcTemplate jdbcTemplate;

	private int order = ORDERED;

	@Autowired
	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		URI uri = request.getURI();

		List<String> patterns = stringRedisTemplate.opsForList().range(REDIS_KEY, 0, -1);
		if (patterns == null || patterns.isEmpty()) {
			return chain.filter(exchange);
		}

		ServerHttpResponse response = exchange.getResponse();
		String string = uri.toString();

		for (String pattern : patterns) {
			if (ANT_PATH_MATCHER.match(pattern, string)) {

				String logId = exchange.getAttributes().get(LogWebFilter.LOG_ID) + "";

				ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(response) {
					@NonNull
					@Override
					public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {

						Flux<? extends DataBuffer> fluxDataBuffer = (Flux<? extends DataBuffer>) body;

						return response.writeWith(fluxDataBuffer.buffer().handle((dataBuffer, sink) -> {

							DataBuffer join = response.bufferFactory().join(dataBuffer);

							byte[] bytes = new byte[join.readableByteCount()];
							join.read(bytes);
							DataBufferUtils.release(join);

							// @formatter:off
							SqlParameterValue[] parameters = new SqlParameterValue[] {
									new SqlParameterValue(Types.VARCHAR, new String(bytes)),
									new SqlParameterValue(Types.VARCHAR, logId),
							};
							// @formatter:on

							PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(parameters);
							jdbcTemplate.update(SQL, pss);

							sink.next(response.bufferFactory().wrap(bytes));
						}));
					};
				};
				return chain.filter(exchange.mutate().response(decorator).build());
			}
		}

		return chain.filter(exchange);
	}

}
