package com.alpha.healthmobile.wxpay;

/**
 * ΢��֧���Ĳ�������
 * 
 * @author SongJian
 * 
 */
public class Constants {
	// appid
	// ��ͬʱ�޸� androidmanifest.xml���棬.PayActivityd�������<data
	// android:scheme="wxb4ba3c02aa476ea1"/>Ϊ�����õ�appid
	public static final String APP_ID = "wxd3c037fb929ddcf7";
	/** �̼���Ƹ�ͨ������̼�id */
	// public static final String PARTNER_ID = "1297166601";

	public static final String MCH_ID = "1305622301";
	// �̻���
	// public static final String MCH_ID = "1297166601";
	// API��Կ�����̻�ƽ̨���ã��̻�����Կ
	public static final String API_KEY = "E2xnJ57HREkneZw2RBfq0p2TBWdMnieM";

	/**
	 * ΢�ſ���ƽ̨���̻�Լ������Կ
	 * 
	 * ע�⣺����hardcode�ڿͻ��ˣ�����genSign��������ɷ����������
	 */
	// db426a9829e4b49a0dcac7b4162da6b6
	public static final String APP_SECRET = "644fb2652c6b59843811b03da484a181"; // wxd930ea5d5a258f4f
																				// ��Ӧ����Կ

	/**
	 * ΢�ſ���ƽ̨���̻�Լ����֧����Կ
	 * 
	 * ע�⣺����hardcode�ڿͻ��ˣ�����genSign��������ɷ����������
	 */
	public static final String APP_KEY = " "; // wxd930ea5d5a258f4f
												// ��Ӧ��֧����Կ

	/**
	 * ΢�Ź���ƽ̨�̻�ģ����̻�Լ������Կ
	 * 
	 * ע�⣺����hardcode�ڿͻ��ˣ�����genPackage��������ɷ����������
	 */
	// 8934e7d15453e97507ef794cf7b0519d
	public static final String PARTNER_KEY = "alpha666888alpha8313513alpha2521";

	// ����΢��֧���첽֪ͨ�ص���ַ
	public static final String NOTIFY_URL = "http://www.51zrys.com/HealthMobile/www/weixin_notify.html";
}
