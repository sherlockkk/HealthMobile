package com.alpha.healthmobile;

import android.util.Log;

public class UpdataInfo {

	private static int version;
	private static String url;
	private static String about;

	public int getVersion() {
		Log.i("cc", "--getVersion--");
		return version;

	}

	public void setVersion(int version) {
		Log.i("cc", "--setVersion--");
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

}
