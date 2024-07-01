package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.properties.ReplaceAll;
import cn.com.xuxiaowei.shield.gateway.utils.GzipUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * 替换过滤器
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Setter
@Component
public class ReplaceAllGlobalFilter implements GlobalFilter, Ordered {

	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE + 60000;

	public static final String REDIS_KEY = "replace-all:";

	private StringRedisTemplate stringRedisTemplate;

	private int order = ORDERED;

	@Autowired
	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		Set<String> keys = stringRedisTemplate.keys(REDIS_KEY + "*");
		if (keys != null && !keys.isEmpty()) {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();
			ObjectMapper objectMapper = new ObjectMapper();

			URI uri = request.getURI();
			String uriHost = uri.getHost();
			String uriPath = uri.getPath();

			AntPathMatcher antPathMatcher = new AntPathMatcher();
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

						HttpHeaders headers = getDelegate().getHeaders();
						String contentEncoding = headers.getFirst(HttpHeaders.CONTENT_ENCODING);
						boolean gzip = contentEncoding != null && contentEncoding.contains("gzip");

						String responseBody;
						try {
							responseBody = decompressResponseBody(bytes, gzip);
						}
						catch (IOException e) {
							sink.error(new RuntimeException(e));
							return;
						}

						String result = responseBody;

						for (String key : keys) {
							String string = stringRedisTemplate.opsForValue().get(key);
							ReplaceAll replaceAll;
							try {
								replaceAll = objectMapper.readValue(string, ReplaceAll.class);
							}
							catch (JsonProcessingException e) {
								sink.error(new RuntimeException(e));
								return;
							}
							String host = replaceAll.getHost();
							if (antPathMatcher.match(host, uriHost)) {
								String regex = replaceAll.getRegex();
								String replacement = replaceAll.getReplacement();
								List<String> patterns = replaceAll.getPatterns();
								for (String pattern : patterns) {
									if (antPathMatcher.match(pattern, uriPath)) {
										result = result.replaceAll(regex, replacement);
									}
								}
							}
						}

						try {
							sink.next(response.bufferFactory().wrap(compressResponseBody(result, gzip)));
						}
						catch (IOException e) {
							sink.error(new RuntimeException(e));
						}
					}));
				};
			};

			return chain.filter(exchange.mutate().response(decorator).build());
		}

		return chain.filter(exchange);
	}

	private String decompressResponseBody(byte[] contentBytes, boolean gzip) throws IOException {
		if (gzip) {
			contentBytes = GzipUtils.decompress(contentBytes);
		}
		return new String(contentBytes);
	}

	private byte[] compressResponseBody(String content, boolean gzip) throws IOException {
		if (gzip) {
			return GzipUtils.compress(content);
		}
		return content.getBytes();
	}

}
