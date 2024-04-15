package cn.com.xuxiaowei.shield.gateway.mysql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest
class ConnectionTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void connection() {

		String sql = "select 2 * 3 as num";

		Integer num = jdbcTemplate.queryForObject(sql, Integer.class);

		assertNotNull(num);
		assertEquals(6, num);
	}

}
