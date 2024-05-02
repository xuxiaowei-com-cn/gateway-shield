package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.properties.GatewayShieldProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;

/**
 * HTTP 端口 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see <a href=
 * "https://docs.spring.io/spring-boot/how-to/webserver.html#howto.webserver.enable-multiple-connectors-in-tomcat">Enable
 * Multiple Connectors with Tomcat</a>
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "gateway-shield.http-port")
public class HttpPortConfig {

	private HttpHandler httpHandler;

	private WebServer webServer;

	private GatewayShieldProperties gatewayShieldProperties;

	@Autowired
	public void setHttpHandler(HttpHandler httpHandler) {
		this.httpHandler = httpHandler;
	}

	@Autowired
	public void setGatewayShieldProperties(GatewayShieldProperties gatewayShieldProperties) {
		this.gatewayShieldProperties = gatewayShieldProperties;
	}

	@PostConstruct
	public void start() {

		Integer port = gatewayShieldProperties.getHttpPort();

		log.info("启用 http 端口 {}", port);

		NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory(port);
		webServer = factory.getWebServer(httpHandler);
		webServer.start();
	}

	@PreDestroy
	public void stop() {
		webServer.stop();
	}

}
