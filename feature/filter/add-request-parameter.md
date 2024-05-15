# 添加请求参数 AddRequestParameterGatewayFilterFactory

- 如果通过网关代理的请求，需要添加特定的参数，可以使用 `AddRequestParameterGatewayFilterFactory` 过滤器

```java
package cn.com.xuxiaowei.shield.gateway.filter.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.AddRequestParameterGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.AddRequestParameterGatewayFilterFactoryTests.NAME;
import static cn.com.xuxiaowei.shield.gateway.filter.factory.AddRequestParameterGatewayFilterFactoryTests.VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see AddRequestParameterGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=AddRequestParameter",
        "spring.cloud.gateway.routes[0].filters[0].args.name=" + NAME,
        "spring.cloud.gateway.routes[0].filters[0].args.value=" + VALUE
})
// @formatter:on
class AddRequestParameterGatewayFilterFactoryTests {

    static final String NAME = "abc";

    static final String VALUE = "123";

    @LocalServerPort
    private int serverPort;

    @SneakyThrows
    @Test
    void apply() {

        // 接口说明：接收到什么参数就在结果中的 query 返回什么参数
        String url = String.format("http://demo.localdev.me:%s/query", serverPort);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

        assertNotNull(entity);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        String body = entity.getBody();
        assertNotNull(body);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(body, new TypeReference<>() {
        });

        assertNotNull(map);
        assertEquals(NAME + "=" + VALUE, map.get("query"));
    }

}
```
