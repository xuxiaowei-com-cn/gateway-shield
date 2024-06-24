package cn.com.xuxiaowei.shield.gateway.handler;

import cn.com.xuxiaowei.shield.gateway.properties.GatewayShieldProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 网关 异常 处理程序
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see ErrorWebExceptionHandler
 * @see ErrorWebFluxAutoConfiguration#errorWebExceptionHandler(ErrorAttributes,
 * WebProperties, ObjectProvider, ServerCodecConfigurer, ApplicationContext)
 * @see ErrorWebFluxAutoConfiguration#errorAttributes()
 */
@Slf4j
@Component
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler, Ordered {

	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE + 10000;

	private GatewayShieldProperties gatewayShieldProperties;

	@Autowired
	public void setGatewayShieldProperties(GatewayShieldProperties gatewayShieldProperties) {
		this.gatewayShieldProperties = gatewayShieldProperties;
	}

	@Setter
	private int order = ORDERED;

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

		ServerHttpResponse response = exchange.getResponse();
		HttpHeaders headers = response.getHeaders();
		headers.setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));

		Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
		if (route == null) {
			String nonExistRouteMessage = gatewayShieldProperties.getNonExistRouteMessage();

			DataBuffer dataBuffer = response.bufferFactory().wrap(nonExistRouteMessage.getBytes());
			return response.writeWith(Flux.just(dataBuffer));
		}

		Map<String, Object> metadata = route.getMetadata();

		String name = ex.getClass().getName();
		Object value = metadata.get(name);
		Object object = metadata.get(Exception.class.getName());

		String defaultRouteExceptionMessage = gatewayShieldProperties.getDefaultRouteExceptionMessage();

		String message = value == null ? (object == null ? defaultRouteExceptionMessage : object.toString())
				: value.toString();

		DataBuffer dataBuffer = response.bufferFactory().wrap(message.getBytes());
		return response.writeWith(Flux.just(dataBuffer));
	}

}
