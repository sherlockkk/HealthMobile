package com.alpha.healthmobile.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.methods.HttpPost;

import com.alpha.healthmobile.Config;

public class HttpUtil {
	

	/**
     * ����post���󵽷�����
     */
    public static String submitPostData(String urlPath, String params, String encode) {
        //���������
        URL url = null;
        HttpURLConnection httpURLConnection;
        try {
            url = new URL(urlPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);//ʹ��POST��ʽ����ʹ�û���

            //������������������ı�����
            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //����������ĳ���
            //httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

            //�����������������д������
            OutputStream outputStream = httpURLConnection.getOutputStream();

            //��÷�������Ӧ��
            int response = httpURLConnection.getResponseCode();
//            if (response == httpURLConnection.HTTP_OK) {
//                InputStream inputStream = httpURLConnection.getInputStream();
//                
//            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return "erro:" + e.getMessage().toString();
        }
        return "-1";
    }


	
	

}
