package cn.com.xuxiaowei.shield.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties("gateway-shield")
public class GatewayShieldProperties {

	/**
	 * Http 端口
	 * <p>
	 * 当配置了 Spring Boot 的 SSL 后，如果还想启动 http 端口，则需要配置此处
	 */
	private Integer httpPort;

}
