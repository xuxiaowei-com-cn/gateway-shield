package cn.com.xuxiaowei.shield.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@RestController
@RequestMapping("/query")
public class QueryRestController {

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> index(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>(8);

		String query = request.getQueryString();

		map.put("query", query);

		return map;
	}

}
