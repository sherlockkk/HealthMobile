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
	public static final String HOST_URL = "http://zrys.code8086.com/HealthMobile/www/index.html";

	/* ���ذ���װ·�� */
	public static final String savePath = "/sdcard/download/";

	public static final String saveFileName = savePath + "HealthMobile.apk";

	public static final String SHARE_SUBJECT = "��������";
	public static final String SHARE_TEXT = "��ӭ������������APP�����������Ľ�����������";
	public static final String SHARE_URL = "http://zrys.code8086.com/HealthMobile/update/HealthMobile.apk";

}
