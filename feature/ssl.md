# TLS/SSL/HTTPS 配置 {id=ssl}

## 启用 https {id=https}

```shell
--server.ssl.certificate=/config/xuxiaowei.com.cn.crt --server.ssl.certificate-private-key=/config/xuxiaowei.com.cn.key
```

## 启用 http2 {id=http2}

```shell
--server.ssl.certificate=/config/xuxiaowei.com.cn.crt --server.ssl.certificate-private-key=/config/xuxiaowei.com.cn.key --server.http2.enabled=true
```

## 信任证书 {id=trust}

```shell
--spring.profiles.active=trust-all-x509-trust-manager
```
