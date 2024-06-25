package cn.com.xuxiaowei.shield.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常接口
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@RestController
@RequestMapping("/exception")
public class ExceptionRestController {

	@RequestMapping
	public Map<String, String> index() {
		// 此处运行时将抛出异常
		int i = 1 / 0;
		return new HashMap<>();
	}

}
