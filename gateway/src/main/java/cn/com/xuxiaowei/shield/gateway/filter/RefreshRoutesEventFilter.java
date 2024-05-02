package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.properties.GatewayShieldProperties;
import cn.com.xuxiaowei.shield.gateway.utils.IpAddressMatcher;
import cn.com.xuxiaowei.shield.gateway.utils.ResponseUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 刷新路由 过滤器
 * <p>
 * 当且仅当
 * {@link GatewayShieldProperties#getRefreshRoutesEventPath()}、{@link GatewayShieldProperties#getRefreshRoutesEventCidr()}
 * 有值时，才启用此过滤器
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Setter
@Component
@ConditionalOnProperty(name = { "gateway-shield.refresh-routes-event-path" })
public class RefreshRoutesEventFilter implements WebFilter, Ordered {

	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE + 50000;

	private ApplicationEventPublisher applicationEventPublisher;

	private GatewayShieldProperties gatewayShieldProperties;

	private int order = ORDERED;

	@Autowired
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Autowired
	public void setGatewayShieldProperties(GatewayShieldProperties gatewayShieldProperties) {
		this.gatewayShieldProperties = gatewayShieldProperties;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@NonNull
	@Override
	public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		URI uri = request.getURI();
		String path = uri.getPath();
		InetSocketAddress remoteAddress = request.getRemoteAddress();
		InetAddress address = remoteAddress.getAddress();
		String hostAddress = address.getHostAddress();

		String refreshRoutesEventPath = gatewayShieldProperties.getRefreshRoutesEventPath();

		if (path.equals(refreshRoutesEventPath)) {

			log.info("访问 刷新路由 过滤器：{} ", refreshRoutesEventPath);

			List<String> refreshRoutesEventCidr = gatewayShieldProperties.getRefreshRoutesEventCidr();
			if (refreshRoutesEventCidr == null || refreshRoutesEventCidr.isEmpty()) {

				log.warn("禁止 刷新路由：请配置 刷新路由 允许的 CIDR");

				return chain.filter(exchange);
			}

			for (String cidr : refreshRoutesEventCidr) {
				IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(cidr);
				boolean matches = ipAddressMatcher.matches(hostAddress);
				if (matches) {
					applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
					Map<String, Object> map = new HashMap<>();
					map.put("msg", "刷新路由成功");
					return ResponseUtils.writeWith(response, map);
				}
			}

			String msg = "禁止 刷新路由：当前用户的 IP 不在 刷新路由 允许的 CIDR 内";
			log.warn(msg);

			Map<String, Object> map = new HashMap<>();
			map.put("msg", msg);
			return ResponseUtils.writeWith(response, map);
		}

		return chain.filter(exchange);
	}

}
