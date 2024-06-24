package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.filter.factory.AllowIPAccessGatewayFilterFactory;
import cn.com.xuxiaowei.shield.gateway.filter.factory.ErrorWebExceptionGatewayFilterFactory;
import cn.com.xuxiaowei.shield.gateway.filter.factory.JavaScriptAddResponseHeaderGatewayFilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.cloud.gateway.filter.factory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关 过滤工厂
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see GatewayAutoConfiguration
 * @see AddRequestParameterGatewayFilterFactory
 * @see RemoveRequestParameterGatewayFilterFactory
 * @see AddRequestHeaderGatewayFilterFactory
 * @see RemoveRequestHeaderGatewayFilterFactory
 * @see SetRequestHeaderGatewayFilterFactory
 * @see MapRequestHeaderGatewayFilterFactory
 * @see AddResponseHeaderGatewayFilterFactory
 * @see RemoveResponseHeaderGatewayFilterFactory
 * @see SetResponseHeaderGatewayFilterFactory
 * @see DedupeResponseHeaderGatewayFilterFactory
 * @see RedirectToGatewayFilterFactory
 * @see RewritePathGatewayFilterFactory
 * @see PrefixPathGatewayFilterFactory
 * @see PreserveHostHeaderGatewayFilterFactory
 * @see RequestRateLimiterGatewayFilterFactory
 * @see StripPrefixGatewayFilterFactory
 * @see FallbackHeadersGatewayFilterFactory
 */
@Slf4j
@Configuration
public class GatewayFilterFactoryConfig {

	@Bean
	@ConditionalOnEnabledFilter
	public JavaScriptAddResponseHeaderGatewayFilterFactory javaScriptAddResponseHeaderGatewayFilterFactory() {
		return new JavaScriptAddResponseHeaderGatewayFilterFactory();
	}

	@Bean
	@ConditionalOnEnabledFilter
	public AllowIPAccessGatewayFilterFactory allowIpAccessGatewayFilterFactory() {
		return new AllowIPAccessGatewayFilterFactory();
	}

	@Bean
	@ConditionalOnEnabledFilter
	public ErrorWebExceptionGatewayFilterFactory errorWebExceptionGatewayFilterFactory() {
		return new ErrorWebExceptionGatewayFilterFactory();
	}

}
