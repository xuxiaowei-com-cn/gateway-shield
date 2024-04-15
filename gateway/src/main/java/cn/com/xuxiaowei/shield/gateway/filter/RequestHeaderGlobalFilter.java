package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.constant.LogConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;

/**
 * 请求头 过滤器
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Setter
@Slf4j
@Component
public class RequestHeaderGlobalFilter implements GlobalFilter, Ordered {

	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE + 20000;

	private int order = ORDERED;

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		String id = request.getId();
		InetSocketAddress remoteAddress = request.getRemoteAddress();
		HttpHeaders headers = request.getHeaders();
		InetAddress address = remoteAddress.getAddress();
		String hostName = address.getHostName();
		String hostAddress = address.getHostAddress();
		List<String> authorizations = headers.get(HttpHeaders.AUTHORIZATION);

		Consumer<HttpHeaders> httpHeaders = httpHeader -> {

			httpHeader.set(LogConstants.G_REQUEST_ID, id);
			httpHeader.set(LogConstants.G_HOST_NAME, hostName);
			httpHeader.set(LogConstants.G_HOST_ADDRESS, hostAddress);

			if (authorizations != null && !authorizations.isEmpty()) {
				httpHeader.addAll(HttpHeaders.AUTHORIZATION, authorizations);
			}
		};

		ServerHttpRequest build = request.mutate().headers(httpHeaders).build();

		return chain.filter(exchange.mutate().request(build).build());
	}

}
