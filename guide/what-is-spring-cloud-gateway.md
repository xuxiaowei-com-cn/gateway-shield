# 什么是 Spring Cloud Gateway？

1. Spring Cloud Gateway 是由 Java 开发 Spring Cloud 微服中的一个重要成员，
   在微服务开发中不可或缺
2. 可脱离微服务开发模式独立运行，本项目为单体项目
3. 主要功能包含
    1. 请求代理（反向代理）
    2. 请求参数过滤
        1. 添加请求参数 [AddRequestParameterGatewayFilterFactory](../feature/filter/add-request-parameter.md)
        2. 删除请求参数 [RemoveRequestParameterGatewayFilterFactory](../feature/filter/remove-request-parameter.md)
    3. 请求头过滤
        1. 添加请求头 [AddRequestHeaderGatewayFilterFactory](../feature/filter/add-request-header.md)
        2. 删除请求头 [RemoveRequestHeaderGatewayFilterFactory](../feature/filter/remove-request-header.md)
        3. 设置请求头 [SetRequestHeaderGatewayFilterFactory](../feature/filter/set-request-header.md)
        4. 复制请求头 [MapRequestHeaderGatewayFilterFactory](../feature/filter/map-request-header.md)
    4. 响应头过滤
        1. 添加响应头 [AddResponseHeaderGatewayFilterFactory](../feature/filter/add-response-header.md)
        2. 删除响应头 [RemoveResponseHeaderGatewayFilterFactory](../feature/filter/remove-response-header.md)
        3. 设置响应头 [SetResponseHeaderGatewayFilterFactory](../feature/filter/set-response-header.md)
        4. 删除重复的响应头 [DedupeResponseHeaderGatewayFilterFactory](../feature/filter/dedupe-response-header.md)，
           通常与 CORS 跨域资源共享 相关
    5. 其他过滤
        1. 重定向 `RedirectToGatewayFilterFactory`
        2. 修改请求路径 `RewritePathGatewayFilterFactory`
        3. 请求路径处理 `PrefixPathGatewayFilterFactory`
        4. Host 处理 `PreserveHostHeaderGatewayFilterFactory`
        5. 请求速率限制 `RequestRateLimiterGatewayFilterFactory`
