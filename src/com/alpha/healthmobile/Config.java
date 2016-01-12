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

	// 测试url
	public static final String TEST_URL = "http://192.168.1.102:8080/HealthMobile/www/index.html";

	public static final String HOST_URL = "http://www.51zrys.com/HealthMobile/www/index.html";
	// 更新url
	public static final String UPDATE_URL = "http://www.51zrys.com/HealthMobile/update/upgrade.xml";
	public static final String UPDATE_APP_CODE_URL = "http://www.51zrys.com/Health/mobile/androiddownload!getAndroidAPPVersionCode.shtml?name=HealthMobile";
	// 新版本下载地址
	public static final String APP_DOWNLOAD_URL = "http://www.51zrys.com/HealthMobile/update/HealthMobile.apk";
	/* 下载包安装路径 */
	public static final String savePath = "/sdcard/download/";

	public static final String saveFileName = savePath + "HealthMobile.apk";

	public static final String SHARE_TEXT = "欢迎下载早日养生APP，做人类身心健康的连接器";
	public static final String SHARE_URL = "http://www.51zrys.com/HealthMobile/update/HealthMobile.apk";
	public static final String SHARE_TITLE = "早日养生";
	// 向服务器发送location数据的url
	public static final String LOCATION_URL = "http://192.168.1.69:8080/Health/mobile/login!loginGeocoder.shtml";
	public static String lnglat = "114.931981,25.842652";

	public static final String DESCRIPTOR = "com.umeng.share";

	// 分享用appID,AppSecret
	public static final String QQ_APPID = "1104732813";
	public static final String QQ_APPKEY = "HmLP4EKuQu7bqWbD";
	public static final String WX_APPID = "wxd3c037fb929ddcf7";
	public static final String WX_APPSECRET = "644fb2652c6b59843811b03da484a181";

	// 订单号回调接口
	public static final String TRADNO_SUBMIT = "http://www.51zrys.com/Health/mobile/single!updatepaystate.shtml?";
}
