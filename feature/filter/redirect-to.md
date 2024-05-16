# 重定向 RedirectToGatewayFilterFactory

- 此配置是将访问 demo.localdev.me:* 的所有请求都重定向到 http://127.0.0.1:45678
- 重定向 HTTP 状态码是 302
- 重定向时 携带参数
- 重定向时 不携带 url path
- `spring.cloud.gateway.routes[0].uri=`：配置什么值都无意义，不会被使用到，但是不能为空

```java
package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.RedirectToGatewayFilterFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see RedirectToGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://xxx",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=RedirectTo",
        "spring.cloud.gateway.routes[0].filters[0].args.status=302",
        "spring.cloud.gateway.routes[0].filters[0].args.url=http://127.0.0.1:45678",
        "spring.cloud.gateway.routes[0].filters[0].args.includeRequestParams=true",
})
// @formatter:on
class RedirectToGatewayFilterFactoryTests {

    @LocalServerPort
    private int serverPort;

    @SneakyThrows
    @Test
    void apply() {

        String name = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();

        String path = UUID.randomUUID().toString();

        String url = String.format("http://demo.localdev.me:%s/%s?%s=%s", serverPort, path, name, value);

        log.info("url: {}", url);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);

        assertNotNull(entity);

        Map<String, String> map = entity.getBody();

        assertNotNull(map);

        String query = map.get("query");

        assertNotNull(query);
        assertEquals(name + "=" + value, query);
    }

}
```
