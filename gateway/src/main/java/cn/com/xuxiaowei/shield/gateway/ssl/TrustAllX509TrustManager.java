package cn.com.xuxiaowei.shield.gateway.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 信任所有证书
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class TrustAllX509TrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

}
