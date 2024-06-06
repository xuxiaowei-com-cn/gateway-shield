package cn.com.xuxiaowei.shield.gateway.filter.factory;

import cn.com.xuxiaowei.shield.gateway.utils.IpAddressMatcher;
import com.google.common.base.Splitter;
import jakarta.validation.constraints.NotEmpty;
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
		return List.of("cidr");
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

			if (isInternalIp(ipAddress, config.getCidr())) {
				return chain.filter(exchange);
			}
			else {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.FORBIDDEN);
				return response.setComplete();
			}
		};
	}

	private boolean isInternalIp(String ipAddress, String cidr) {
		List<String> cidrs = Splitter.on(",").trimResults().splitToList(cidr);
		return cidrs.stream().anyMatch(predicate -> new IpAddressMatcher(predicate).matches(ipAddress));
	}

	/**
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	@Validated
	public static class Config {

		/**
		 * 支持使用 , 分隔
		 */
		@NotEmpty(message = "CIDR 不能为空")
		private String cidr;

	}

}
