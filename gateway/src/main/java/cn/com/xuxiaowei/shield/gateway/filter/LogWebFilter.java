package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.constant.LogConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

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

	private int order = ORDERED;

	@Override
	public int getOrder() {
		return order;
	}

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

		return chain.filter(exchange);
	}

}
