/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.xuxiaowei.shield.gateway.utils;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 匹配
 * <p>
 * 根据 IP 地址或子网掩码匹配远程地址的请求。
 * <p>
 * 支持 IPv6 和 IPv4 地址，但配置了 IPv4 地址的匹配器永远不会匹配返回 IPv6 地址的请求，反之亦然。
 * <p>
 * 抽取自 <code>org.springframework.security.web.util.matcher.IpAddressMatcher</code>，剔除了
 * <code>org.springframework.security.web.util.matcher.RequestMatcher</code> 接口
 * <p>
 * Matches a request based on IP Address or subnet mask matching against the remote
 * address.
 * <p>
 * Both IPv6 and IPv4 addresses are supported, but a matcher which is configured with an
 * IPv4 address will never match a request which returns an IPv6 address, and vice-versa.
 *
 * @author Luke Taylor
 * @since 3.0.2
 */
public final class IpAddressMatcher {

	private final int nMaskBits;

	private final InetAddress requiredAddress;

	/**
	 * Takes a specific IP address or a range specified using the IP/Netmask (e.g.
	 * 192.168.1.0/24 or 202.24.0.0/14).
	 * @param ipAddress the address or range of addresses from which the request must
	 * come.
	 */
	public IpAddressMatcher(String ipAddress) {
		if (ipAddress.indexOf('/') > 0) {
			String[] addressAndMask = StringUtils.split(ipAddress, "/");
			ipAddress = addressAndMask[0];
			this.nMaskBits = Integer.parseInt(addressAndMask[1]);
		}
		else {
			this.nMaskBits = -1;
		}
		this.requiredAddress = parseAddress(ipAddress);
		Assert.isTrue(this.requiredAddress.getAddress().length * 8 >= this.nMaskBits,
				String.format("IP address %s is too short for bitmask of length %d", ipAddress, this.nMaskBits));
	}

	public boolean matches(String address) {
		InetAddress remoteAddress = parseAddress(address);
		if (!this.requiredAddress.getClass().equals(remoteAddress.getClass())) {
			return false;
		}
		if (this.nMaskBits < 0) {
			return remoteAddress.equals(this.requiredAddress);
		}
		byte[] remAddr = remoteAddress.getAddress();
		byte[] reqAddr = this.requiredAddress.getAddress();
		int nMaskFullBytes = this.nMaskBits / 8;
		byte finalByte = (byte) (0xFF00 >> (this.nMaskBits & 0x07));
		for (int i = 0; i < nMaskFullBytes; i++) {
			if (remAddr[i] != reqAddr[i]) {
				return false;
			}
		}
		if (finalByte != 0) {
			return (remAddr[nMaskFullBytes] & finalByte) == (reqAddr[nMaskFullBytes] & finalByte);
		}
		return true;
	}

	private InetAddress parseAddress(String address) {
		try {
			return InetAddress.getByName(address);
		}
		catch (UnknownHostException ex) {
			throw new IllegalArgumentException("Failed to parse address" + address, ex);
		}
	}

}
