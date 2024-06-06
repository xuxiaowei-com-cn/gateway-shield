package cn.com.xuxiaowei.shield.gateway.filter.factory;

import cn.com.xuxiaowei.shield.gateway.utils.IpAddressMatcher;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 允许 IP 访问
 *
 * @see GatewayAutoConfiguration
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class AllowIPAccessGatewayFilterFactory
		extends AbstractGatewayFilterFactory<AllowIPAccessGatewayFilterFactory.Config> {

	public AllowIPAccessGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return List.of("cidrs");
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

			if (isInternalIp(ipAddress, config.getCidrs())) {
				return chain.filter(exchange);
			}
			else {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.FORBIDDEN);
				return response.setComplete();
			}
		};
	}

	private boolean isInternalIp(String ipAddress, List<String> cidrs) {
		return cidrs.stream().anyMatch(cidr -> new IpAddressMatcher(cidr).matches(ipAddress));
	}

	/**
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	@Validated
	public static class Config {

		@NotNull(message = "CIDR 不能为空")
		@Size(min = 1, message = "CIDR 不能为空")
		private List<String> cidrs;

	}

}
