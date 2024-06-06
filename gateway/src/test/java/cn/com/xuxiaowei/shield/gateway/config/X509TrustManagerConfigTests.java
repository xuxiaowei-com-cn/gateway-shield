package cn.com.xuxiaowei.shield.gateway.config;

import cn.com.xuxiaowei.shield.gateway.ssl.TrustAllX509TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
class X509TrustManagerConfigTests {

	@Test
	void restTemplate() throws NoSuchAlgorithmException, KeyManagementException {

		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllX509TrustManager() };

			// Install the all-trusting trust manager
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = (hostname, session) -> true;

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		}
		catch (Exception e) {
			log.error("忽略 SSL 异常", e);

			throw e;
		}

		String url = "https://39.156.66.18";
		new RestTemplate().getForObject(url, String.class);
	}

}
