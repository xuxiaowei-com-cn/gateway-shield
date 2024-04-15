package cn.com.xuxiaowei.shield.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class GatewayApplicationTests {

	public static void queryForList(JdbcTemplate jdbcTemplate) {
		jdbcTemplate.queryForList("select * from gateway_shield_log").forEach(System.out::println);
	}

}
