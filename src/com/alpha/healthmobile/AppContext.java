package com.alpha.healthmobile;

import android.app.Application;
import android.content.Context;

public class AppContext extends Application {
	private static AppContext appInstance;
	private Context context;

	public static AppContext getInstance() {
		return appInstance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		appInstance = this;
		context = this.getBaseContext();
		// // 获取当前版本号
		initGlobal();
	}

	public void initGlobal() {
		try {
			Config.localVersion = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode; // 设置本地版本号
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
