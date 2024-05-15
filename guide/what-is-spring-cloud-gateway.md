# 什么是 Spring Cloud Gateway？

1. Spring Cloud Gateway 是由 Java 开发 Spring Cloud 微服中的一个重要成员，
   在微服务开发中不可或缺
2. 可脱离微服务开发模式独立运行，本项目为单体项目
3. 主要功能包含
    1. 请求代理（反向代理）
    2. 请求参数过滤
        1. 添加请求参数 [AddRequestParameterGatewayFilterFactory](../feature/filter/add-request-parameter.md)
        2. 删除请求参数 `RemoveRequestParameterGatewayFilterFactory`
    3. 请求头过滤
        1. 添加请求头 `AddRequestHeaderGatewayFilterFactory`
        2. 删除请求头 `RemoveRequestHeaderGatewayFilterFactory`
        3. `SetRequestHeaderGatewayFilterFactory`
        4. 转换请求头 `MapRequestHeaderGatewayFilterFactory`
    4. 响应头过滤
        1. 添加响应头 `AddResponseHeaderGatewayFilterFactory`
        2. 删除响应头 `RemoveResponseHeaderGatewayFilterFactory`
        3. `SetResponseHeaderGatewayFilterFactory`
        4. 删除重复的响应头 `DedupeResponseHeaderGatewayFilterFactory`，通常与 CORS 跨域资源共享 相关
    5. 其他过滤
        1. 重定向 `RedirectToGatewayFilterFactory`
        2. 修改请求路径 `RewritePathGatewayFilterFactory`
        3. 请求路径处理 `PrefixPathGatewayFilterFactory`
        4. Host 处理 `PreserveHostHeaderGatewayFilterFactory`
        5. 请求速率限制 `RequestRateLimiterGatewayFilterFactory`
