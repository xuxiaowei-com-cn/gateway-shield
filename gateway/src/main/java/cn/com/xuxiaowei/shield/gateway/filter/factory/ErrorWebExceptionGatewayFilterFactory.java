package cn.com.xuxiaowei.shield.gateway.filter.factory;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 代理目标服务异常时自定义响应
 * <p>
 * 仅处理 5xx 状态码
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class ErrorWebExceptionGatewayFilterFactory
		extends AbstractGatewayFilterFactory<ErrorWebExceptionGatewayFilterFactory.Config> {

	public ErrorWebExceptionGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return List.of("message");
	}

	@Override
	public GatewayFilter apply(Config config) {

		return (exchange, chain) -> chain.filter(exchange).then(Mono.defer(() -> {
			ServerHttpResponse response = exchange.getResponse();
			HttpStatusCode statusCode = response.getStatusCode();

			if (statusCode.is5xxServerError()) {
				DataBuffer dataBuffer = response.bufferFactory().wrap(config.getMessage().getBytes());
				return response.writeWith(Flux.just(dataBuffer));
			}

			return chain.filter(exchange);
		}));
	}

	/**
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	@Validated
	public static class Config {

		/**
		 *
		 */
		@NotEmpty(message = "异常信息 不能为空")
		private String message;

	}

}
