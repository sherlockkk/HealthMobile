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
	public static final String APP_ID = "wxd3c037fb929ddcf7";
	/** 商家向财付通申请的商家id */
	// public static final String PARTNER_ID = "1297166601";

	public static final String MCH_ID = "1305622301";
	// 商户号
	// public static final String MCH_ID = "1297166601";
	// API密钥，在商户平台设置，商户的密钥
	public static final String API_KEY = "E2xnJ57HREkneZw2RBfq0p2TBWdMnieM";

	/**
	 * 微信开放平台和商户约定的密钥
	 * 
	 * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
	 */
	// db426a9829e4b49a0dcac7b4162da6b6
	public static final String APP_SECRET = "644fb2652c6b59843811b03da484a181"; // wxd930ea5d5a258f4f
																				// 对应的密钥

	/**
	 * 微信开放平台和商户约定的支付密钥
	 * 
	 * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
	 */
	public static final String APP_KEY = " "; // wxd930ea5d5a258f4f
												// 对应的支付密钥

	/**
	 * 微信公众平台商户模块和商户约定的密钥
	 * 
	 * 注意：不能hardcode在客户端，建议genPackage这个过程由服务器端完成
	 */
	// 8934e7d15453e97507ef794cf7b0519d
	public static final String PARTNER_KEY = "alpha666888alpha8313513alpha2521";

	// 接收微信支付异步通知回调地址
	public static final String NOTIFY_URL = "http://www.51zrys.com/HealthMobile/www/weixin_notify.html";
}
