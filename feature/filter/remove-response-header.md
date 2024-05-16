# 删除响应头 RemoveResponseHeaderGatewayFilterFactory {id=remove-response-header}

- 如果通过网关代理的请求，需要删除特定的响应头，可以使用 `RemoveResponseHeaderGatewayFilterFactory` 过滤器

```java
package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.factory.RemoveResponseHeaderGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.RemoveResponseHeaderGatewayFilterFactoryTests.NAME;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 * @see RemoveResponseHeaderGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
        "spring.cloud.gateway.routes[0].uri=http://localhost:45678/",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=RemoveResponseHeader",
        "spring.cloud.gateway.routes[0].filters[0].args.name=" + NAME,

		"spring.cloud.gateway.routes[1].id=demo-1",
		"spring.cloud.gateway.routes[1].uri=http://localhost:45678/",
		"spring.cloud.gateway.routes[1].predicates[0]=Host=demo-1.localdev.me:*",
})
// @formatter:on
class RemoveResponseHeaderGatewayFilterFactoryTests {

    static final String NAME = "Date";

    @LocalServerPort
    private int serverPort;

    @SneakyThrows
    @Test
    void apply() {

        String url = String.format("http://demo.localdev.me:%s/header", serverPort);

        String value = UUID.randomUUID().toString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(NAME, value);
        HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Map> entity = restTemplate.postForEntity(url, httpEntity, Map.class);

        assertNotNull(entity);

        HttpHeaders headers = entity.getHeaders();

        assertNotNull(headers);

        assertEquals(2, headers.size());

        Object object = headers.get(NAME);

        assertNull(object);
    }

    @SneakyThrows
    @Test
    void ua() {

        String url = String.format("http://demo-1.localdev.me:%s/header", serverPort);

        String value = UUID.randomUUID().toString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(NAME, value);
        HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Map> entity = restTemplate.postForEntity(url, httpEntity, Map.class);

        assertNotNull(entity);

        HttpHeaders headers = entity.getHeaders();

        assertNotNull(headers);

        assertEquals(3, headers.size());

        Object object = headers.get(NAME);

        assertNotNull(object);
    }

}
```
