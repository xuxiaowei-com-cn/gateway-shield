package cn.com.xuxiaowei.shield.gateway.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip 压缩工具
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class GzipUtils {

	/**
	 * 将字符串进行 gzip 压缩
	 * @param str 要压缩的字符串
	 * @return 压缩后的字节数组
	 * @throws IOException 如果压缩过程中发生错误
	 */
	public static byte[] compress(String str) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos)) {
			gzipOutputStream.write(str.getBytes());
		}
		return bos.toByteArray();
	}

	/**
	 * 解压缩 gzip 压缩的字节数组
	 * @param compressedBytes 压缩后的字节数组
	 * @return 解压缩后的字节数组
	 * @throws IOException 如果解压缩过程中发生错误
	 */
	public static byte[] decompress(byte[] compressedBytes) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (GZIPInputStream gzipInputStream = new GZIPInputStream(bis)) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gzipInputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
		}
		return bos.toByteArray();
	}

	public static String decompressResponseBody(byte[] contentBytes, boolean gzip) throws IOException {
		if (gzip) {
			contentBytes = GzipUtils.decompress(contentBytes);
		}
		return new String(contentBytes);
	}

	public static byte[] compressResponseBody(String content, boolean gzip) throws IOException {
		if (gzip) {
			return GzipUtils.compress(content);
		}
		return content.getBytes();
	}

}
