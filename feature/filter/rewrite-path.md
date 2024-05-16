# 修改请求路径 RewritePathGatewayFilterFactory

```java
package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.RewritePathGatewayFilterFactoryTests.REGEXP;
import static cn.com.xuxiaowei.shield.gateway.filter.factory.RewritePathGatewayFilterFactoryTests.REPLACEMENT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see RewritePathGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {

		// http://localhost:45678/header?abc=xyz：只会返回所接收的请求头，不会返回请求参数
		// http://localhost:45678/query?abc=xyz：只会返回请求参数，不会返回请求头

		// 将请求路径中的 /header 修改为 /query

        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678/",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=RewritePath",
        "spring.cloud.gateway.routes[0].filters[0].args.regexp=" + REGEXP,
        "spring.cloud.gateway.routes[0].filters[0].args.replacement=" + REPLACEMENT,

		// 正常代理，不修改请求地址

		"spring.cloud.gateway.routes[1].id=demo-1",
		"spring.cloud.gateway.routes[1].uri=http://localhost:45678/",
		"spring.cloud.gateway.routes[1].predicates[0]=Host=demo-1.localdev.me:*",
})
// @formatter:on
class RewritePathGatewayFilterFactoryTests {

    static final String REGEXP = "/header";

    static final String REPLACEMENT = "/query";

    @LocalServerPort
    private int serverPort;

    @SneakyThrows
    @Test
    void apply() {

        String name = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();

        String url = String.format("http://demo.localdev.me:%s/header?%s=%s", serverPort, name, value);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);

        assertNotNull(entity);

        Map<String, String> map = entity.getBody();

        assertNotNull(map);

        String query = map.get("query");

        assertNotNull(query);
        assertEquals(name + "=" + value, query);
    }

    @SneakyThrows
    @Test
    void ua() {

        String name = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();

        String url = String.format("http://demo-1.localdev.me:%s/header?%s=%s", serverPort, name, value);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);

        assertNotNull(entity);

        Map<String, String> map = entity.getBody();

        assertNotNull(map);

        String query = map.get("query");

        assertNull(query);
    }

}
```
