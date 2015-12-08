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
 * 启动界面
 * @author Administrator
 */
public class FirstActivity extends Activity implements AMapLocationListener{
	private LocationManagerProxy mLocationManagerProxy;
	
	
	// 加载静态代码块,调用Uninstall文件
	static 
	{
		System.loadLibrary("Uninstall");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		new UninstallApp().Uninstall();	//实现卸载后弹出alpha官网
		init();
		load();
	}
	/**
	 * 初始化定位
	 */
	private void init() {
		// 初始化定位，只采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(false);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次,
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);

	}
/**
 * 将location post到服务器
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
            httpURLConnection.setUseCaches(false);//使用POST方式不能使用缓存

            //设置请求体的类型是文本类型
            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            //httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();

            //获得服务器响应码
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
	 * 3秒后跳转到主页面
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
			// 定位成功回调信息，设置相关消息
			//postLocation(amapLocation.getLatitude() + ","
				//	+ amapLocation.getLongitude());
			Config.lnglat=amapLocation.getLatitude() + ","
					+ amapLocation.getLongitude();
		} else {
			Log.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
		}
	}
}
