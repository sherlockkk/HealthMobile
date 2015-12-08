package com.alpha.healthmobile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.alpha.healthmobile.uninstall.UninstallApp;
import com.alpha.healthmobile.utils.HttpUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * ��������
 * @author Administrator
 */
public class FirstActivity extends Activity implements AMapLocationListener{
	private LocationManagerProxy mLocationManagerProxy;
	
	
	// ���ؾ�̬�����,����Uninstall�ļ�
	static 
	{
		System.loadLibrary("Uninstall");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		new UninstallApp().Uninstall();	//ʵ��ж�غ󵯳�alpha����
		init();
		load();
	}
	/**
	 * ��ʼ����λ
	 */
	private void init() {
		// ��ʼ����λ��ֻ�������綨λ
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(false);
		// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
		// ע�����ú��ʵĶ�λʱ��ļ������С���֧��Ϊ2000ms���������ں���ʱ�����removeUpdates()������ȡ����λ����
		// �ڶ�λ�������ں��ʵ��������ڵ���destroy()����
		// ����������ʱ��Ϊ-1����λֻ��һ��,
		// �ڵ��ζ�λ����£���λ���۳ɹ���񣬶��������removeUpdates()�����Ƴ����󣬶�λsdk�ڲ����Ƴ�
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);

	}
/**
 * ��location post��������
 */
	private void postLocation(String location) {
		// TODO Auto-generated method stub
		//HttpUtil httpUtil = new HttpUtil();
		//httpUtil.submitPostData(Config.LOCATION_URL, location, "UTF-8");
		URL url = null;
        HttpURLConnection httpURLConnection;
        try {
            url = new URL(Config.LOCATION_URL+"?lnglat="+location);
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
//	            if (response == httpURLConnection.HTTP_OK) {
//	                InputStream inputStream = httpURLConnection.getInputStream();
//	                
//	            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
           // return "erro:" + e.getMessage().toString();
        }
       // return "-1";
		
	}

	/**
	 * 3�����ת����ҳ��
	 */
	public void load() {
		new AsyncTask<Integer, Integer, Integer>() {

			@Override
			protected Integer doInBackground(Integer... arg0) {
				try 
				{
					Thread.sleep(1000);
				} 
				catch (Exception e) 
				{
					
				}

				publishProgress(0);
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {

				Intent intent = new Intent(FirstActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				super.onProgressUpdate(values);
			}

		}.execute(0);
	}
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		// TODO Auto-generated method stub
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// ��λ�ɹ��ص���Ϣ�����������Ϣ
			//postLocation(amapLocation.getLatitude() + ","
				//	+ amapLocation.getLongitude());
			Config.lnglat=amapLocation.getLatitude() + ","
					+ amapLocation.getLongitude();
		} else {
			Log.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
		}
	}
}
