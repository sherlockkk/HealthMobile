package com.alpha.healthmobile;

public class Config {
	// �汾��Ϣ
	public static int localVersion = 0;
	public static int serverVersion;

	// webview����·��
	// ����������Ŀ��ҳ
	/*
	 * http://zrys.code8086.com/HealthMobile/www/index.html
	 */
	// ��������˾����
	/*
	 * http://www.atgn.cn/
	 */
	public static final String HOST_URL = "http://www.51zrys.com/HealthMobile/www/index.html";
	// ����url
	public static final String UPDATE_URL = "http://www.51zrys.com/HealthMobile/update/upgrade.xml";
	/* ���ذ���װ·�� */
	public static final String savePath = "/sdcard/download/";

	public static final String saveFileName = savePath + "HealthMobile.apk";

	public static final String SHARE_TEXT = "��ӭ������������APP�����������Ľ�����������";
	public static final String SHARE_URL = "http://www.51zrys.com/HealthMobile/update/HealthMobile.apk";
	public static final String SHARE_TITLE = "��������";
	// �����������location���ݵ�url
	public static final String LOCATION_URL = "http://192.168.1.69:8080/Health/mobile/login!loginGeocoder.shtml";
	public static String lnglat = "114.931981,25.842652";

	// appID,AppSecret
	public static final String QQ_APPID = "1104911029";
	public static final String QQ_APPSECRET = "4WVr2PMolD8UPpfw";
	public static final String WX_APPID = "wx4fec9e44be45c0cd";
	public static final String WX_APPSECRET = "d4624c36b6795d1d99dcf0547af5443d";
}
