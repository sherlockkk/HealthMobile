package com.alpha.healthmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * 启动界面
 * 
 * @author Administrator
 * 
 */
public class FirstActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);

		load();

	}

	// 3秒后跳转到主页面
	public void load() {
		new AsyncTask<Integer, Integer, Integer>() {

			@Override
			protected Integer doInBackground(Integer... arg0) {
				try {
					Thread.sleep(1000);

				} catch (Exception e) {
					// TODO: handle exception
				}

				publishProgress(0);
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {

				Intent intent = new Intent(FirstActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				super.onProgressUpdate(values);
			}

		}.execute(0);
	}

}
