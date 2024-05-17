# 增加路径前缀 PrefixPathGatewayFilterFactory

- 如果通过网关代理的请求，需要增加路径前缀，可以使用 `PrefixPathGatewayFilterFactory` 过滤器

```java
package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.PrefixPathGatewayFilterFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see PrefixPathGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {

		// 访问 demo.localdev.me:* 时，给路径增加指定前缀

        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=PrefixPath",
		// 需要使用 / 开头
		"spring.cloud.gateway.routes[0].filters[0].args.prefix=/abc",
})
// @formatter:on
class PrefixPathGatewayFilterFactoryTests {

    @LocalServerPort
    private int serverPort;

    @SneakyThrows
    @Test
    void apply() {

        // 通过网关 http://demo.localdev.me:端口/123 路径时，添加前缀 /abc，
        // 代理后的路径 http://localhost:45678/abc/123

        String url = String.format("http://demo.localdev.me:%s/123", serverPort);

        log.info("url: {}", url);

        RestTemplate restTemplate = new RestTemplate();

        Map map = restTemplate.getForObject(url, Map.class);

        assertNotNull(map);
    }

}
```
