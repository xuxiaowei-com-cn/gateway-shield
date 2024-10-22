package cn.com.xuxiaowei.shield.gateway.filter.factory;

import cn.com.xuxiaowei.shield.gateway.filter.LogWebFilter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.validation.annotation.Validated;

import java.sql.Types;
import java.util.List;

/**
 * 保存请求 Body 并直接返回 200
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class SaveRequestBodyDirectReturnGatewayFilterFactory
		extends AbstractGatewayFilterFactory<SaveRequestBodyDirectReturnGatewayFilterFactory.Config> {

	private final JdbcTemplate jdbcTemplate;

	public static final String SAVE_REQUEST_BODY_DIRECT_RETURN_ID = "SaveRequestBodyDirectReturnId";

	private static final String SQL = "UPDATE `gateway_shield_log` SET `request_body` = ? WHERE `gateway_shield_log_id` = ?";

	public SaveRequestBodyDirectReturnGatewayFilterFactory(JdbcTemplate jdbcTemplate) {
		super(Config.class);
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return List.of("paths");
	}

	@Override
	public GatewayFilter apply(SaveRequestBodyDirectReturnGatewayFilterFactory.Config config) {
		return (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();

			return request.getBody().collectList().flatMap(body -> {

				StringBuilder bodyBuilder = new StringBuilder();
				body.forEach(dataBuffer -> {
					byte[] bytes = new byte[dataBuffer.readableByteCount()];
					dataBuffer.read(bytes);
					bodyBuilder.append(new String(bytes));
				});

				String requestBody = bodyBuilder.toString();
				String logId = exchange.getAttributes().get(LogWebFilter.LOG_ID) + "";

				// @formatter:off
				SqlParameterValue[] parameters = new SqlParameterValue[] {
						new SqlParameterValue(Types.VARCHAR, requestBody),
						new SqlParameterValue(Types.VARCHAR, logId),
				};
				// @formatter:on

				PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(parameters);
				jdbcTemplate.update(SQL, pss);

				String id = request.getId();

				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.OK);
				HttpHeaders headers = response.getHeaders();
				headers.set(SAVE_REQUEST_BODY_DIRECT_RETURN_ID, id);

				return response.setComplete();
			});
		};
	}

	/**
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	@Validated
	public static class Config {

		/**
		 * 支持使用 , 分隔
		 */
		@NotEmpty(message = "路径 paths 不能为空")
		private String paths;

	}

}
