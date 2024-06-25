# 允许 IP 访问 AllowIPAccessGatewayFilterFactory

- 支持 CIDR
- 如果路由配置了此过滤器，则仅允许配置的 IP 才能访问，否则响应 403

```java
package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.AddRequestHeaderGatewayFilterFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.AddRequestHeaderGatewayFilterFactoryTests.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see AddRequestHeaderGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=AddRequestHeader",
        "spring.cloud.gateway.routes[0].filters[0].args.name=" + NAME,
        "spring.cloud.gateway.routes[0].filters[0].args.value=" + VALUE,

		"spring.cloud.gateway.routes[1].id=ua",
		"spring.cloud.gateway.routes[1].uri=http://localhost:45678",
		"spring.cloud.gateway.routes[1].predicates[0]=Host=ua.localdev.me:*",
		"spring.cloud.gateway.routes[1].filters[0].name=AddRequestHeader",
		"spring.cloud.gateway.routes[1].filters[0].args.name=" + UA_NAME,
		"spring.cloud.gateway.routes[1].filters[0].args.value=" + UA_VALUE,
})
// @formatter:on
class AddRequestHeaderGatewayFilterFactoryTests {

    static final String NAME = "abc-xuxiaowei";

    static final String VALUE = "123-test-hkjkljk";

    static final String UA_NAME = "User-Agent";

    static final String UA_VALUE = "123456123456";

    @LocalServerPort
    private int serverPort;

    @SneakyThrows
    @Test
    void apply() {

        String url = String.format("http://demo.localdev.me:%s/header", serverPort);

        RestTemplate restTemplate = new RestTemplate();

        Map map = restTemplate.getForObject(url, Map.class);

        assertNotNull(map);

        Object valueObj = map.get(NAME);

        assertNotNull(valueObj);
        assertInstanceOf(List.class, valueObj);

        List<String> list = (List<String>) valueObj;
        assertEquals(1, list.size());
        assertEquals(VALUE, list.get(0));
    }

    @Test
    void ua() {

        String url = String.format("http://ua.localdev.me:%s/header", serverPort);

        RestTemplate restTemplate = new RestTemplate();

        Map map = restTemplate.getForObject(url, Map.class);

        assertNotNull(map);

        Object valueObj = map.get(UA_NAME.toLowerCase());

        assertNotNull(valueObj);
        assertInstanceOf(List.class, valueObj);

        List<String> list = (List<String>) valueObj;
        assertEquals(2, list.size());
        assertTrue(list.contains(UA_VALUE));
    }

}
```