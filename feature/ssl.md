# TLS/SSL/HTTPS 配置 {id=ssl}

## 启用 https {id=https}

```shell
java -jar app.jar --server.ssl.certificate=/config/xuxiaowei.com.cn.crt --server.ssl.certificate-private-key=/config/xuxiaowei.com.cn.key
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
