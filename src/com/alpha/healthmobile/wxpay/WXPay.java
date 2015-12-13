package com.alpha.healthmobile.wxpay;

import java.util.Map;
import java.util.Random;

import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Î¢ÐÅÖ§¸¶
 * 
 * @author SongJian E-mail:1129574214@qq.com
 * @date 2015-12-12 ÏÂÎç4:21:56
 * @version 1.0
 * @parameter
 * @return
 */
public class WXPay {
	private static final String TAG = "WXPay";
	Context context;
	PayReq payReq;
	IWXAPI msgApi;
	Map<String, String> resultunifiedorder;
	StringBuffer sb;
	String total_fee;
	String notify_url;
	String body;
	String outTradNo;

	public WXPay(Context context, String total_fee, String notify_url,
			String body) {
		msgApi = WXAPIFactory.createWXAPI(context, null);
		msgApi.registerApp(Constants.APP_ID);
		sb = new StringBuffer();
		payReq = new PayReq();
		this.context = context;
		this.total_fee = total_fee;
		this.notify_url = notify_url;
		this.body = body;
	}

	public static WXPay getInstance(Context context, String total_fee,
			String notify_url, String body) {
		return new WXPay(context, total_fee, notify_url, body);
	}

	String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	public void doPay() {
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask(this);
		getPrepayId.execute();
	}
}
