package cn.com.xuxiaowei.shield.gateway.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GatewayShieldRuntimeException extends RuntimeException {

	/**
	 * 错误代码
	 */
	private String code;

	/**
	 * 参考
	 */
	private List<String> references;

	public GatewayShieldRuntimeException(String message) {
		super(message);
		this.code = "500";
	}

	public GatewayShieldRuntimeException(String message, Throwable cause) {
		super(message, cause);
		this.code = "500";
	}

	public GatewayShieldRuntimeException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

}
