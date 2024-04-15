package cn.com.xuxiaowei.shield.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@RestController
public class IndexRestController {

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping
	public Map<String, Object> index(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			Enumeration<String> values = request.getHeaders(name);
			map.put(name, Collections.list(values));
		}

		return map;
	}

}