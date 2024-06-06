package cn.com.xuxiaowei.shield.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=trust-all-x509-trust-manager" })
class X509TrustManagerConfigTests {

	@Test
	void restTemplate() {
		new RestTemplate().getForObject("https://39.156.66.18", String.class);
	}

}
