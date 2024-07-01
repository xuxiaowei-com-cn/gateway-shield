package cn.com.xuxiaowei.shield.gateway.properties;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
public class ReplaceAll {

	/**
	 *
	 */
	private String host;

	/**
	 *
	 */
	private List<String> patterns = new ArrayList<>();

	/**
	 *
	 */
	private String regex;

	/**
	 *
	 */
	private String replacement;

}
