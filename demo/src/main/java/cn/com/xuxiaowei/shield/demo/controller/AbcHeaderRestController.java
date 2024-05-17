package cn.com.xuxiaowei.shield.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@RestController
@RequestMapping("/abc/123")
public class AbcHeaderRestController {

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, List<String>> index(HttpServletRequest request, HttpServletResponse response) {
		Map<String, List<String>> map = new HashMap<>(8);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			Enumeration<String> values = request.getHeaders(name);
			map.put(name, Collections.list(values));
		}

		return map;
	}

	@RequestMapping(value = "/dedupe-response-header", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, List<String>> dedupeResponseHeader(HttpServletRequest request, HttpServletResponse response) {

		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(true));
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(true));

		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://musk.mars");
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://musk.mars");

		Map<String, List<String>> map = new HashMap<>(8);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			Enumeration<String> values = request.getHeaders(name);
			map.put(name, Collections.list(values));
		}

		return map;
	}

}
