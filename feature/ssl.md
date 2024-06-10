# TLS/SSL/HTTPS 配置 {id=ssl}

## 启用 https {id=https}

```shell
java -jar app.jar --server.ssl.certificate=/config/xuxiaowei.com.cn.crt --server.ssl.certificate-private-key=/config/xuxiaowei.com.cn.key
```

```shell
# 启动日志如下
# 端口可以使用环境变量：GATEWAY_SHIELD_PORT、GATEWAY_SHIELD_PORT_HTTP 修改
# 端口也可以使用启动参数：--server.port= --gateway-shield.http-port= 修改
2024-06-10 18:18:19.903 - INFO 19420 --- [           main] c.c.x.s.gateway.config.HttpPortConfig    :  :  :  :  : 启用 http 端口 45455
2024-06-10 18:18:20.063 - INFO 19420 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  :  :  :  :  : Netty started on port 45455 (http)
2024-06-10 18:18:20.693 - INFO 19420 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  :  :  :  :  : Netty started on port 45450 (https)
```

## 启用 http2 {id=http2}

```shell
java -jar app.jar --server.ssl.certificate=/config/xuxiaowei.com.cn.crt --server.ssl.certificate-private-key=/config/xuxiaowei.com.cn.key --server.http2.enabled=true
```

## 信任证书 {id=trust}

::: code-group

```shell [网关代理时信任所有目标证书]
# https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/tls-and-ssl.html
java -jar app.jar --spring.cloud.gateway.httpclient.ssl.useInsecureTrustManager=true
```

```shell [网关代理时信任指定证书]
# https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/tls-and-ssl.html
java -jar app.jar --spring.cloud.gateway.httpclient.ssl.trustedX509Certificates[0]=/config/cert1.pem --spring.cloud.gateway.httpclient.ssl.trustedX509Certificates[1]=/config/cert2.pem
```

```shell [非网关代理时信任目标证书]
# 如：RestTemplate 等
java -jar app.jar --spring.profiles.active=trust-all-x509-trust-manager
```

:::
