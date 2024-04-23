package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.filter.JavaScriptAddResponseHeaderGatewayFilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.cloud.gateway.filter.factory.AddResponseHeaderGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关 过滤工厂
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see GatewayAutoConfiguration
 * @see AddResponseHeaderGatewayFilterFactory
 */
@Slf4j
@Configuration
public class GatewayFilterFactoryConfig {

	@Bean
	@ConditionalOnEnabledFilter
	public JavaScriptAddResponseHeaderGatewayFilterFactory javaScriptAddResponseHeaderGatewayFilterFactory() {
		return new JavaScriptAddResponseHeaderGatewayFilterFactory();
	}

}
