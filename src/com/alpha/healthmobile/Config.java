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

	// ����url
	public static final String TEST_URL = "http://192.168.1.102:8080/HealthMobile/www/index.html";

	public static final String HOST_URL = "http://www.51zrys.com/HealthMobile/www/index.html";
	// ����url
	public static final String UPDATE_URL = "http://www.51zrys.com/HealthMobile/update/upgrade.xml";
	public static final String UPDATE_APP_CODE_URL = "http://www.51zrys.com/Health/mobile/androiddownload!getAndroidAPPVersionCode.shtml?name=HealthMobile";
	// �°汾���ص�ַ
	public static final String APP_DOWNLOAD_URL = "http://www.51zrys.com/HealthMobile/update/HealthMobile.apk";
	/* ���ذ���װ·�� */
	public static final String savePath = "/sdcard/download/";

	public static final String saveFileName = savePath + "HealthMobile.apk";

	public static final String SHARE_TEXT = "��ӭ������������APP�����������Ľ�����������";
	public static final String SHARE_URL = "http://www.51zrys.com/HealthMobile/update/HealthMobile.apk";
	public static final String SHARE_TITLE = "��������";
	// �����������location���ݵ�url
	public static final String LOCATION_URL = "http://192.168.1.69:8080/Health/mobile/login!loginGeocoder.shtml";
	public static String lnglat = "114.931981,25.842652";

	public static final String DESCRIPTOR = "com.umeng.share";

	// ������appID,AppSecret
	public static final String QQ_APPID = "1104732813";
	public static final String QQ_APPKEY = "HmLP4EKuQu7bqWbD";
	public static final String WX_APPID = "wxd3c037fb929ddcf7";
	public static final String WX_APPSECRET = "644fb2652c6b59843811b03da484a181";

	// �����Żص��ӿ�
	public static final String TRADNO_SUBMIT = "http://www.51zrys.com/Health/mobile/single!updatepaystate.shtml?";
}
