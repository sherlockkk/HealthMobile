package com.alpha.healthmobile;

public class Config {
	// 版本信息
	public static int localVersion = 0;
	public static int serverVersion;

	// webview载入路径
	// 早日养生项目首页
	/*
	 * http://zrys.code8086.com/HealthMobile/www/index.html
	 */
	// 阿尔发公司官网
	/*
	 * http://www.atgn.cn/
	 */
	public static final String HOST_URL = "http://www.51zrys.com/HealthMobile/www/index.html";
	// 更新url
	public static final String UPDATE_URL = "http://www.51zrys.com/HealthMobile/update/upgrade.xml";
	/* 下载包安装路径 */
	public static final String savePath = "/sdcard/download/";

	public static final String saveFileName = savePath + "HealthMobile.apk";

	public static final String SHARE_TEXT = "欢迎下载早日养生APP，做人类身心健康的连接器";
	public static final String SHARE_URL = "http://www.51zrys.com/HealthMobile/update/HealthMobile.apk";
	public static final String SHARE_TITLE = "早日养生";
	// 向服务器发送location数据的url
	public static final String LOCATION_URL = "http://192.168.1.69:8080/Health/mobile/login!loginGeocoder.shtml";
	public static String lnglat = "114.931981,25.842652";

	// appID,AppSecret
	public static final String QQ_APPID = "1104911029";
	public static final String QQ_APPSECRET = "4WVr2PMolD8UPpfw";
	public static final String WX_APPID = "wx4fec9e44be45c0cd";
	public static final String WX_APPSECRET = "d4624c36b6795d1d99dcf0547af5443d";
}
