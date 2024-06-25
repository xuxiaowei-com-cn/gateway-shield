# 代理目标服务异常时自定义响应 ErrorWebExceptionGatewayFilterFactory

- 如果代理的目标服务响应 5xx 状态码，则使用自定义内容响应

```java
package cn.com.xuxiaowei.shield.gateway.filter.factory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static cn.com.xuxiaowei.shield.gateway.filter.factory.ErrorWebExceptionGatewayFilterFactoryTests.MESSAGE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 代理目标服务异常时自定义响应 测试类
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see ErrorWebExceptionGatewayFilterFactory
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @formatter:off
@TestPropertySource(properties = {
        "spring.cloud.gateway.routes[0].id=demo",
		"spring.cloud.gateway.routes[0].uri=http://localhost:45678",
        "spring.cloud.gateway.routes[0].predicates[0]=Host=demo.localdev.me:*",
        "spring.cloud.gateway.routes[0].filters[0].name=ErrorWebException",
        "spring.cloud.gateway.routes[0].filters[0].args.message=" + MESSAGE,
})
// @formatter:on
class ErrorWebExceptionGatewayFilterFactoryTests {

    static final String MESSAGE = "<h1>服务异常，稍后再试，或联系管理员：xuxiaowei@xuxiaowei.com.cn</h1>";

    @LocalServerPort
    private int serverPort;

    @Test
    void exception() {

        String url = String.format("http://demo.localdev.me:%s/exception", serverPort);

        RestTemplate restTemplate = new RestTemplate();

        Exception exception = null;

        try {
            restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            exception = e;
            log.error("代理异常：", e);
        }

        assertNotNull(exception);
        assertInstanceOf(HttpServerErrorException.InternalServerError.class, exception);

        HttpServerErrorException.InternalServerError internalError = (HttpServerErrorException.InternalServerError) exception;

        String statusText = internalError.getStatusText();
        assertNotNull(statusText);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), statusText);

        HttpStatusCode statusCode = internalError.getStatusCode();
        assertNotNull(statusCode);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);

        String message = internalError.getMessage();
        assertNotNull(message);
        assertTrue(message.contains(MESSAGE));
        assertTrue(message.contains(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }

}
```
