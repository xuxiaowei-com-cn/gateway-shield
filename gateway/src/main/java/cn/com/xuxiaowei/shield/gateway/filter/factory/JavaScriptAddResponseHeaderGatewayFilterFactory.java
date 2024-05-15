package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;

/**
 * JavaScript 响应头过滤工厂
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see GatewayAutoConfiguration
 * @see AddResponseHeaderGatewayFilterFactory
 */
@Slf4j
public class JavaScriptAddResponseHeaderGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

	private static final String JAVA_SCRIPT_SUFFIX = ".js";

	@Override
	public GatewayFilter apply(NameValueConfig config) {
		return new GatewayFilter() {
			@Override
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				ServerHttpRequest request = exchange.getRequest();
				RequestPath requestPath = request.getPath();
				String path = requestPath.toString();
				if (path.endsWith(JAVA_SCRIPT_SUFFIX)) {
					return chain.filter(exchange).then(Mono.fromRunnable(() -> addHeader(exchange, config)));
				}

				return chain.filter(exchange);
			}

			@Override
			public String toString() {
				return filterToStringCreator(JavaScriptAddResponseHeaderGatewayFilterFactory.this)
					.append(config.getName(), config.getValue())
					.toString();
			}
		};
	}

	void addHeader(ServerWebExchange exchange, NameValueConfig config) {
		final String value = ServerWebExchangeUtils.expand(exchange, config.getValue());
		HttpHeaders headers = exchange.getResponse().getHeaders();
		// if response has been commited, no more response headers will bee added.
		if (!exchange.getResponse().isCommitted()) {
			headers.add(config.getName(), value);
		}
	}

}
