package com.alpha.healthmobile.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {

	/**
	 * ����post���󵽷�����
	 */
	public static String submitPostData(String urlPath, String params,
			String encode) {
		// ���������
		URL url = null;
		HttpURLConnection httpURLConnection;
		try {
			url = new URL(urlPath);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);// ʹ��POST��ʽ����ʹ�û���

			// ������������������ı�����
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// ����������ĳ���
			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(params.length()));

			// �����������������д������
			OutputStream outputStream = httpURLConnection.getOutputStream();

			// ��÷�������Ӧ��
			int response = httpURLConnection.getResponseCode();
			if (response == httpURLConnection.HTTP_OK) {
				InputStream inputStream = httpURLConnection.getInputStream();
				return inputStream.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return "erro:" + e.getMessage().toString();
		}
		return "-1";
	}

	public static boolean postMethon(String path, Map<String, String> params)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append('=')
					.append(URLEncoder.encode(entry.getValue(), "UTF-8"))
					.append('&');
		}
		sb.deleteCharAt(sb.length() - 1);
		byte[] entityData = sb.toString().getBytes();
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5 * 1000);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				String.valueOf(entityData.length));
		OutputStream os = connection.getOutputStream();
		os.write(entityData);
		os.flush();
		os.close();
		if (connection.getResponseCode() == 200) {
			return true;
		}
		return false;
	}
}
