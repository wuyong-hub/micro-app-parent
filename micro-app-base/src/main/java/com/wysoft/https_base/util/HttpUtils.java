package com.wysoft.https_base.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	public static String httpPost(String url, String data) {
		// CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		CloseableHttpClient httpClient = null;

		HttpPost httpPost = null;

		// 响应模型
		CloseableHttpResponse response = null;
		String result = null;

		try {
			httpClient = new NoSSLClient();
			
			// 创建Post请求
			httpPost = new HttpPost(url);

			StringEntity entity = new StringEntity(data, "UTF-8");

			// post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
			httpPost.setEntity(entity);

			httpPost.setHeader("Content-Type", "application/json;charset=utf8");

			// 由客户端执行(发送)Post请求
			response = httpClient.execute(httpPost);
			// 从响应模型中获取响应实体
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				result = EntityUtils.toString(responseEntity);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}
	
	public static String httpPost(String url, Map<String, String> headers,String data) {
		// CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		CloseableHttpClient httpClient = null;

		HttpPost httpPost = null;

		// 响应模型
		CloseableHttpResponse response = null;
		String result = null;

		try {
			httpClient = new NoSSLClient();
			
			// 创建Post请求
			httpPost = new HttpPost(url);
			
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}

			StringEntity entity = new StringEntity(data, "UTF-8");

			// post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
			httpPost.setEntity(entity);

			httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");

			// 由客户端执行(发送)Post请求
			response = httpClient.execute(httpPost);
			// 从响应模型中获取响应实体
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				result = EntityUtils.toString(responseEntity);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	public static String httpGet(String url, Map<String, String> headers, Map<String, String> params) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String result = null;
		CloseableHttpResponse response = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);

			if (params != null && params.size() > 0) {
				List<NameValuePair> list = new LinkedList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				uriBuilder.setParameters(list);
			}

			HttpGet httpGet = new HttpGet(uriBuilder.build());
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}

			response = httpClient.execute(httpGet);
			// HttpEntity
			// 是一个中间的桥梁，在httpClient里面，是连接我们的请求与响应的一个中间桥梁，所有的请求参数都是通过HttpEntity携带过去的
			// 所有的响应的数据，也全部都是封装在HttpEntity里面
			HttpEntity entity = response.getEntity();
			// 通过EntityUtils 来将我们的数据转换成字符串
			result = EntityUtils.toString(entity, "UTF-8");
			// EntityUtils.toString(entity)
			System.out.println(result);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	public static String getLocalHost() {
		String ip = "localhost";
		try {
			InetAddress ia = InetAddress.getLocalHost();

			ip = ia.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return ip;
		}
	}

	public static void main(String[] para) {
		System.out.println(getLocalHost());
	}

}
