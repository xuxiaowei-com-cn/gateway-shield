package cn.com.xuxiaowei.shield.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties("gateway-shield")
public class GatewayShieldProperties {

	/**
	 * Http 端口
	 * <p>
	 * 当配置了 Spring Boot 的 SSL 后，如果还想启动 http 端口，则需要配置此处
	 */
	private Integer httpPort;

	/**
	 * 刷新路由 过滤器 路径：为空时代表不启用
	 */
	private String refreshRoutesEventPath;

	/**
	 * 刷新路由 过滤器 允许的 CIDR
	 * <p>
	 * 允许所有：0.0.0.0/0，此功能属于管理功能，不建议开放所有地址访问
	 * <p>
	 * 为空时：禁止 刷新路由
	 * <p>
	 * 常用内网IP地址：
	 * <p>
	 * 10.0.0.0 到 10.255.255.255（CIDR：10.0.0.0/8）
	 * <p>
	 * 172.16.0.0 到 172.31.255.255（CIDR：172.16.0.0/12）
	 * <p>
	 * 192.168.0.0 到 192.168.255.255（CIDR：192.168.0.0/16）
	 * <p>
	 * <p>
	 * /8：16777214个用作主机地址，16777216个可用的IP地址，256 x 256 x 256
	 * <p>
	 * /9：8388606个用作主机地址，8388608个可用的IP地址，128 x 256 x 256
	 * <p>
	 * /10：4194302个用作主机地址，4194304个可用的IP地址，64 x 256 x 256
	 * <p>
	 * /11：2097150个用作主机地址，2097152个可用的IP地址，32 x 256 x 256
	 * <p>
	 * /12：1048574个用作主机地址，1048576个可用的IP地址，16 x 256 x 256
	 * <p>
	 * /13：524286个用作主机地址，524288个可用的IP地址，8 x 256 x 256
	 * <p>
	 * /14：262142个用作主机地址，262144个可用的IP地址，4 x 256 x 256
	 * <p>
	 * /15：131070个用作主机地址，131072个可用的IP地址，2 x 256 x 256
	 * <p>
	 * /16：65534个用作主机地址，65536个可用的IP地址，256 x 256
	 * <p>
	 * /17：32766个用作主机地址，32768个可用的IP地址，128 x 256
	 * <p>
	 * /18：16382个用作主机地址，16384个可用的IP地址，64 x 256
	 * <p>
	 * /19：8190个用作主机地址，8192个可用的IP地址，32 x 256
	 * <p>
	 * /20：4094个用作主机地址，4096个可用的IP地址，16 x 256
	 * <p>
	 * /21：2046个用作主机地址，2048个可用的IP地址，8 x 256
	 * <p>
	 * /22：1022个用作主机地址，1024个可用的IP地址，4 x 256
	 * <p>
	 * /23：510个用作主机地址，512个可用的IP地址，2 x 256
	 * <p>
	 * /24：254个用作主机地址，256个可用的IP地址，1 x 256
	 * <p>
	 * /25：126个用作主机地址，128个可用的IP地址
	 * <p>
	 * /26：62个用作主机地址，64个可用的IP地址
	 * <p>
	 * /27：30个用作主机地址，32个可用的IP地址
	 * <p>
	 * /28：14个用作主机地址，16个可用的IP地址
	 * <p>
	 * /29：6个用作主机地址，8个可用的IP地址
	 * <p>
	 * /30：2个用作主机地址，4个可用的IP地址
	 * <p>
	 * /31：0个用作主机地址，2个可用的IP地址
	 * <p>
	 * /32：IP地址作为单个主机使用，即该IP地址没有可用的子网
	 * <p>
	 */
	private List<String> refreshRoutesEventCidr = Arrays.asList("10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16");

	/**
	 * 是否开启 IP ASN 地址识别
	 */
	private boolean enableAsn;

	/**
	 * IP ASN 地址识别 数据库文件路径
	 */
	private String asnDatabase;

	/**
	 * 是否开启 城市 IP 地址识别
	 */
	private boolean enableCity;

	/**
	 * 城市 IP 地址识别 数据库文件路径
	 */
	private String cityDatabase;

	/**
	 * 不存在路由的消息
	 */
	private String nonExistRouteMessage;

	/**
	 * 默认路由异常
	 * <p>
	 * 优先级最低
	 * <p>
	 * // @formatter:off
	 * metadata 数据中的异常 > metadata 数据中的 java.lang.Exception 异常 > defaultRouteExceptionMessage
	 * // @formatter:on
	 */
	private String defaultRouteExceptionMessage;

	/**
	 * 是否启用 网关 异常 处理程序
	 */
	private Boolean enableGatewayErrorWebExceptionHandler;

	/**
	 * 开启保存响应
	 */
	private Boolean enableSaveResponseBody;

}
