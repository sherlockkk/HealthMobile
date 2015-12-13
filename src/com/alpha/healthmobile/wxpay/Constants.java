package com.alpha.healthmobile.wxpay;

/**
 * 微信支付的参数配置
 * 
 * @author SongJian
 * 
 */
public class Constants {
	// appid
	// 请同时修改 androidmanifest.xml里面，.PayActivityd里的属性<data
	// android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
	public static final String APP_ID = "";
	// 商户号
	public static final String MCH_ID = "";
	// API密钥，在商户平台设置，商户的密钥
	public static final String API_KEY = "";
	// 接收微信支付异步通知回调地址
	public static final String NOTIFY_URL = "";
}
