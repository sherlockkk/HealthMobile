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
     * 发送post请求到服务器
     */
    public static String submitPostData(String urlPath, String params, String encode) {
        //获得请求体
        URL url = null;
        HttpURLConnection httpURLConnection;
        try {
            url = new URL(urlPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);//使用POST方式不能使用缓存

            //设置请求体的类型是文本类型
            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            //httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();

            //获得服务器响应码
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
