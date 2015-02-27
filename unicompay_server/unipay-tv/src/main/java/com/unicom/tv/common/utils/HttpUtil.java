package com.unicom.tv.common.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

public class HttpUtil {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
	
	private static CloseableHttpClient client;
	
    static {
    	SSLContextBuilder builder = null;
    	SSLConnectionSocketFactory sslsf = null;
		try {
			builder = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy());
			sslsf = new SSLConnectionSocketFactory(builder.build(), new AllowAllHostnameVerifier());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		RequestConfig config = RequestConfig.custom().setConnectTimeout(15000).setSocketTimeout(15000).build();
		client = HttpClients.custom().setDefaultRequestConfig(config).setSSLSocketFactory(sslsf).build();
    }
	
	public static String simplePost(String uri, String body) {
		HttpPost httpPost = new HttpPost(uri);
		if (StringUtils.isNotBlank(body)) {
			httpPost.setEntity(new StringEntity(body, "UTF-8"));
		}
		return getResponse(httpPost, null, null);
	}
	
	public static String simpleGet(String uri) {
		return getResponse(new HttpGet(uri), null, null);
	}
	
	private static String getResponse(HttpUriRequest request, Map<String, String> headers, Map<String, String> params) {
		String content = null;
		CloseableHttpResponse response = null;
		InputStream inputStream = null;
		try {
			if (headers != null) {
				for (Entry<String, String> header : headers.entrySet()) {
					request.setHeader(header.getKey(), header.getValue());
				}
			}
			response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					inputStream = entity.getContent();
					if (inputStream != null) {
						content = StreamUtils.copyToString(inputStream, CharsetUtils.get("UTF-8"));
					}
				}
			}
		} catch (ClientProtocolException e) {
			LOGGER.error("", e);
		} catch (IOException e) {
			LOGGER.error("", e);
		} finally {
			closeResource(response);
			closeResource(inputStream);
		}
		return content;
	}
	
	public static byte[] simpleByteGet(String uri) {
		return getByteResponse(new HttpGet(uri), null, null);
	}
	
	private static byte[] getByteResponse(HttpUriRequest request, Map<String, String> headers, Map<String, String> params) {
		byte[] content = null;
		CloseableHttpResponse response = null;
		InputStream inputStream = null;
		try {
			if (headers != null) {
				for (Entry<String, String> header : headers.entrySet()) {
					request.setHeader(header.getKey(), header.getValue());
				}
			}
			response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					inputStream = entity.getContent();
					if (inputStream != null) {
						content = StreamUtils.copyToByteArray(inputStream);
					}
				}
			}
		} catch (ClientProtocolException e) {
			LOGGER.error("", e);
		} catch (IOException e) {
			LOGGER.error("", e);
		} finally {
			closeResource(response);
			closeResource(inputStream);
		}
		return content;
	}
	
	public static void closeResource(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static String post(String url, Map<String, String> headers, String body) {
		HttpPost httpPost = new HttpPost(url);
		if (StringUtils.isNotBlank(body)) {
			httpPost.setEntity(new StringEntity(body, "UTF-8"));
		}
		return getResponse(httpPost, headers, null);
	}
	
	private HttpUtil() {
	}
	
}
