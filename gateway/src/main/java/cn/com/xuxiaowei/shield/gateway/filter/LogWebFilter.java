package cn.com.xuxiaowei.shield.gateway.filter;

import cn.com.xuxiaowei.shield.gateway.constant.LogConstants;
import cn.com.xuxiaowei.shield.gateway.properties.GatewayShieldProperties;
import cn.com.xuxiaowei.shield.gateway.utils.IpAddressMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 日志 过滤器
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Setter
@Component
public class LogWebFilter implements WebFilter, Ordered {

	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE;

	private static final String SQL = "INSERT INTO `gateway_shield_log` "
			+ " (`gateway_shield_log_id`, `request_id`, `scheme`, `host_name`, `host_address`, `network`, `system_organization`, `system_number`, `continent_code`, `continent_geo_name_id`, `continent_name`, `country_iso_code`, `country_geo_name_id`, `country_name`, `is_in_european_union`, `subdivision_iso_codes`, `subdivision_geo_name_ids`, `subdivision_names`, `city_geo_name_id`, `city_name`, `host`, `port`, `path`, `query`, `raw_query`, `type`, `user_agent`, `referer`, `headers_json`, `year`, `month`, `day`, `hour`, `minute`, `second`) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final List<String> INTRANETS = Arrays.asList(
			// 10.0.0.0 到 10.255.255.255
			"10.0.0.0/8",
			// 172.16.0.0 到 172.31.255.255
			"172.16.0.0/12",
			// 192.168.0.0 到 192.168.255.255
			"192.168.0.0/16",
			// 127.0.0.1
			"127.0.0.1/32");

	private JdbcTemplate jdbcTemplate;

	private GatewayShieldProperties gatewayShieldProperties;

	private int order = ORDERED;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Autowired
	public void setGatewayShieldProperties(GatewayShieldProperties gatewayShieldProperties) {
		this.gatewayShieldProperties = gatewayShieldProperties;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@SneakyThrows
	@NonNull
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();

		String id = request.getId();
		URI uri = request.getURI();
		HttpHeaders headers = request.getHeaders();
		String host = headers.getFirst(HttpHeaders.HOST);
		InetSocketAddress remoteAddress = request.getRemoteAddress();
		InetAddress address = remoteAddress.getAddress();
		String hostName = address.getHostName();
		String hostAddress = address.getHostAddress();

		MDC.put(LogConstants.G_REQUEST_ID, id);
		MDC.put(LogConstants.G_HOST, host);
		MDC.put(LogConstants.G_HOST_NAME, hostName);
		MDC.put(LogConstants.G_HOST_ADDRESS, hostAddress);

		// @formatter:off
		log.debug("URI: {}, {}: {}, {}: {}, {}: {}", uri,
				LogConstants.G_HOST, host,
				LogConstants.G_HOST_NAME, hostName,
				LogConstants.G_HOST_ADDRESS, hostAddress);
		// @formatter:on

		save(exchange);

		// String redisVersion = RedisUtils.redisVersion(stringRedisTemplate);
		// log.info("redisVersion: {}", redisVersion);

		return chain.filter(exchange);
	}

	private void save(ServerWebExchange exchange) throws JsonProcessingException {

		ServerHttpRequest request = exchange.getRequest();

		String id = request.getId();
		URI uri = request.getURI();
		String scheme = uri.getScheme();
		String query = uri.getQuery();
		String rawQuery = uri.getRawQuery();
		int port = uri.getPort();
		String path = uri.getPath();

		String type = "";

		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		Month month = now.getMonth();
		int monthValue = month.getValue();
		int dayOfMonth = now.getDayOfMonth();
		int hour = now.getHour();
		int minute = now.getMinute();
		int second = now.getSecond();

		HttpHeaders headers = request.getHeaders();

		String userAgent = headers.getFirst(HttpHeaders.USER_AGENT);
		String referer = headers.getFirst(HttpHeaders.REFERER);
		String host = headers.getFirst(HttpHeaders.HOST);

		ObjectMapper objectMapper = new ObjectMapper();
		String headersJson = objectMapper.writeValueAsString(headers);

		InetSocketAddress remoteAddress = request.getRemoteAddress();
		InetAddress address = remoteAddress.getAddress();
		String hostName = address.getHostName();
		String hostAddress = address.getHostAddress();

		String network = null;
		String systemOrganization = null;
		Long systemNumber = null;
		String continentCode = null;
		Long continentGeoNameId = null;
		String continentName = null;
		String countryIsoCode = null;
		Long countryGeoNameId = null;
		String countryName = null;
		boolean isInEuropeanUnion = false;
		String subdivisionIsoCodes = null;
		String subdivisionGeoNameIds = null;
		String subdivisionNames = null;
		Long cityGeoNameId = null;
		String cityName = null;

		if (gatewayShieldProperties.isEnableAsn()) {
			String asnDatabase = gatewayShieldProperties.getAsnDatabase();
			if (StringUtils.hasText(asnDatabase)) {
				File database = new File(asnDatabase);

				// This reader object should be reused across lookups as creation of it is
				// expensive.
				// DatabaseReader reader = new DatabaseReader.Builder(database).build();

				// If you want to use caching at the cost of a small (~2MB) memory
				// overhead:
				// new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
				DatabaseReader reader = null;
				try {
					reader = new DatabaseReader.Builder(database).locales(List.of("zh-CN"))
						.withCache(new CHMCache())
						.build();
				}
				catch (IOException e) {
					log.error("创建 ASN 读取器 异常：", e);
				}

				if (reader != null) {

					InetAddress ipAddress = null;
					try {
						ipAddress = InetAddress.getByName(hostAddress);
					}
					catch (Exception e) {
						log.error("ASN 解析地址 异常：", e);
					}

					if (ipAddress != null) {
						AsnResponse asnResponse = null;
						try {
							asnResponse = reader.asn(ipAddress);
						}
						catch (IOException e) {
							log.error("ASN IOException 异常：", e);
						}
						catch (GeoIp2Exception e) {
							log.error("ASN GeoIp2Exception 异常：", e);
						}

						if (asnResponse != null) {
							network = asnResponse.getNetwork().toString();
							systemOrganization = asnResponse.getAutonomousSystemOrganization();
							systemNumber = asnResponse.getAutonomousSystemNumber();
						}
					}
				}

			}
			else {
				log.warn("虽然已开启 ASN，但是 ASN 数据库为空，无法使用 ASN");
			}
		}

		if (gatewayShieldProperties.isEnableCity()) {
			String cityDatabase = gatewayShieldProperties.getCityDatabase();
			if (StringUtils.hasText(cityDatabase)) {
				File database = new File(cityDatabase);

				// This reader object should be reused across lookups as creation of it is
				// expensive.
				// DatabaseReader reader = new DatabaseReader.Builder(database).build();

				// If you want to use caching at the cost of a small (~2MB) memory
				// overhead:
				// new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
				DatabaseReader reader = null;
				try {
					reader = new DatabaseReader.Builder(database).locales(List.of("zh-CN"))
						.withCache(new CHMCache())
						.build();
				}
				catch (IOException e) {
					log.error("创建 IP 城市匹配 读取器 异常：", e);
				}

				if (reader != null) {

					InetAddress ipAddress = null;
					try {
						ipAddress = InetAddress.getByName(hostAddress);
					}
					catch (Exception e) {
						log.error("IP 城市匹配 解析地址 异常：", e);
					}

					if (ipAddress != null) {
						CityResponse cityResponse = null;
						try {
							cityResponse = reader.city(ipAddress);
						}
						catch (IOException e) {
							log.error("IP 城市匹配 IOException 异常：", e);
						}
						catch (GeoIp2Exception e) {
							log.error("IP 城市匹配 GeoIp2Exception 异常：", e);
						}

						if (cityResponse != null) {
							Continent continent = cityResponse.getContinent();
							Country country = cityResponse.getCountry();
							List<Subdivision> subdivisions = cityResponse.getSubdivisions();
							City city = cityResponse.getCity();

							continentCode = continent.getCode();
							continentGeoNameId = continent.getGeoNameId();
							continentName = continent.getName();

							countryIsoCode = country.getIsoCode();
							countryGeoNameId = country.getGeoNameId();
							countryName = country.getName();

							isInEuropeanUnion = country.isInEuropeanUnion();

							List<String> subdivisionIsoCodeList = new ArrayList<>();
							List<Long> subdivisionGeoNameIdList = new ArrayList<>();
							List<String> subdivisionNameList = new ArrayList<>();
							for (Subdivision subdivision : subdivisions) {
								String isoCode = subdivision.getIsoCode();
								Long geoNameId = subdivision.getGeoNameId();
								String name = subdivision.getName();
								if (StringUtils.hasText(isoCode)) {
									subdivisionIsoCodeList.add(isoCode);
								}
								if (geoNameId != null) {
									subdivisionGeoNameIdList.add(geoNameId);
								}
								if (StringUtils.hasText(name)) {
									subdivisionNameList.add(name);
								}
							}

							subdivisionIsoCodes = Joiner.on(",").join(subdivisionIsoCodeList);
							subdivisionGeoNameIds = Joiner.on(",").join(subdivisionGeoNameIdList);
							subdivisionNames = Joiner.on(",").join(subdivisionNameList);

							cityGeoNameId = city.getGeoNameId();
							cityName = city.getName();
						}
					}
				}

			}
			else {
				log.warn("虽然已开启 IP 城市匹配，但是 IP 城市匹配 数据库为空，无法使用 IP 城市匹配");
			}
		}

		if (network == null && hostAddress != null) {
			for (String intranet : INTRANETS) {
				try {
					IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(intranet);
					if (ipAddressMatcher.matches(hostAddress)) {
						network = intranet;
						break;
					}
				}
				catch (Exception e) {
					log.error("host 内网识别异常：", e);
				}
			}
		}

		// @formatter:off
		SqlParameterValue[] parameters = new SqlParameterValue[] {
				new SqlParameterValue(Types.VARCHAR, UUID.randomUUID().toString()),
				new SqlParameterValue(Types.VARCHAR, id),
				new SqlParameterValue(Types.VARCHAR, scheme),
				new SqlParameterValue(Types.VARCHAR, hostName),
				new SqlParameterValue(Types.VARCHAR, hostAddress),
				new SqlParameterValue(Types.VARCHAR, network),
				new SqlParameterValue(Types.VARCHAR, systemOrganization),
				new SqlParameterValue(Types.VARCHAR, systemNumber),
				new SqlParameterValue(Types.VARCHAR, continentCode),
				new SqlParameterValue(Types.INTEGER, continentGeoNameId),
				new SqlParameterValue(Types.VARCHAR, continentName),
				new SqlParameterValue(Types.VARCHAR, countryIsoCode),
				new SqlParameterValue(Types.INTEGER, countryGeoNameId),
				new SqlParameterValue(Types.VARCHAR, countryName),
				new SqlParameterValue(Types.INTEGER, isInEuropeanUnion?1:0),
				new SqlParameterValue(Types.VARCHAR, subdivisionIsoCodes),
				new SqlParameterValue(Types.VARCHAR, subdivisionGeoNameIds),
				new SqlParameterValue(Types.VARCHAR, subdivisionNames),
				new SqlParameterValue(Types.INTEGER, cityGeoNameId),
				new SqlParameterValue(Types.VARCHAR, cityName),
				new SqlParameterValue(Types.VARCHAR, host),
				new SqlParameterValue(Types.INTEGER, port),
				new SqlParameterValue(Types.VARCHAR, path),
				new SqlParameterValue(Types.VARCHAR, query),
				new SqlParameterValue(Types.VARCHAR, rawQuery),
				new SqlParameterValue(Types.VARCHAR, type),
				new SqlParameterValue(Types.VARCHAR, userAgent),
				new SqlParameterValue(Types.VARCHAR, referer),
				new SqlParameterValue(Types.VARCHAR, headersJson),
				new SqlParameterValue(Types.INTEGER, year),
				new SqlParameterValue(Types.INTEGER, monthValue),
				new SqlParameterValue(Types.INTEGER, dayOfMonth),
				new SqlParameterValue(Types.INTEGER, hour),
				new SqlParameterValue(Types.INTEGER, minute),
				new SqlParameterValue(Types.INTEGER, second)
		};
		// @formatter:on

		PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(parameters);
		jdbcTemplate.update(SQL, pss);
	}

}
