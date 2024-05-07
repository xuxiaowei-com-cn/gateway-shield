package cn.com.xuxiaowei.shield.gateway;

import cn.com.xuxiaowei.shield.gateway.properties.GatewayShieldProperties;
import cn.com.xuxiaowei.shield.gateway.utils.IpAddressMatcher;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DataTests {

	@Autowired
	private GatewayShieldProperties gatewayShieldProperties;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// @Test
	void geoip2() {

		List<Map<String, Object>> maps = jdbcTemplate
			.queryForList("SELECT gateway_shield_log_id, host_address FROM gateway_shield_log WHERE network IS NULL");

		// 内网
		List<String> cidrs = Arrays.asList("10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16");

		for (Map<String, Object> map : maps) {

			String gatewayShieldLogId = map.get("gateway_shield_log_id").toString();
			String hostAddress = map.get("host_address").toString();

			// 跳过内网
			boolean matches = false;
			for (String cidr : cidrs) {
				IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(cidr);
				matches = ipAddressMatcher.matches(hostAddress);
				if (matches) {
					break;
				}
			}
			if (matches) {
				continue;
			}

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

					// This reader object should be reused across lookups as creation of
					// it is
					// expensive.
					// DatabaseReader reader = new
					// DatabaseReader.Builder(database).build();

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

					// This reader object should be reused across lookups as creation of
					// it is
					// expensive.
					// DatabaseReader reader = new
					// DatabaseReader.Builder(database).build();

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

			// @formatter:off
			SqlParameterValue[] parameters = new SqlParameterValue[] {
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
					new SqlParameterValue(Types.VARCHAR, gatewayShieldLogId)
			};
			// @formatter:on

			PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(parameters);
			jdbcTemplate.update(
					"UPDATE `gateway_shield_log` SET `network` = ?, `system_organization` = ?, `system_number` = ?, `continent_code` = ?, `continent_geo_name_id` = ?, `continent_name` = ?, `country_iso_code` = ?, `country_geo_name_id` = ?, `country_name` = ?, `is_in_european_union` = ?, `subdivision_iso_codes` = ?, `subdivision_geo_name_ids` = ?, `subdivision_names` = ?, `city_geo_name_id` = ?, `city_name` = ? WHERE `gateway_shield_log_id` = ?",
					pss);
		}
	}

}
