package cn.com.xuxiaowei.shield.demo.controller;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@RestController
public class HomeRestController {

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> home(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> map = new HashMap<>(8);
		Map<String, Object> headers = new HashMap<>(8);
		map.put("headers", headers);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			Enumeration<String> values = request.getHeaders(name);
			headers.put(name, Collections.list(values));
		}

		ServletInputStream inputStream = request.getInputStream();

		// 使用 BufferedReader 读取输入流中的数据
		StringBuilder body = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}
		}
		catch (Exception e) {
			log.error("读取请求流异常：", e);
			throw e;
		}

		// 将读取的请求体内容存入 Map 中
		map.put("body", body.toString());

		String query = request.getQueryString();
		map.put("query", query);

		return map;
	}

}
