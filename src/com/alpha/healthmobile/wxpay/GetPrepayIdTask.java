package com.alpha.healthmobile.wxpay;

import java.io.IOException;
import java.io.StringReader;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.alpha.healthmobile.utils.GetPhoneHostIp;
import com.alpha.healthmobile.utils.HttpWxUtil;
import com.tencent.mm.sdk.modelpay.PayReq;

/**
 * @author SongJian E-mail:1129574214@qq.com
 * @date 2015-12-12 ÏÂÎç10:40:35
 * @version 1.0
 * @parameter
 * @return
 */
class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {
	/**
	 * 
	 */
	private WXPay GetPrepayIdTask;

	/**
	 * @param wxPay
	 */
	GetPrepayIdTask(WXPay wxPay) {
		GetPrepayIdTask = wxPay;
	}

	private ProgressDialog dialog;

	@Override
	protected void onCancelled(Map<String, String> result) {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog = ProgressDialog
				.show(GetPrepayIdTask.context,
						GetPrepayIdTask.context
								.getString(com.alpha.healthmobile.R.string.app_tip),
						GetPrepayIdTask.context
								.getString(com.alpha.healthmobile.R.string.getting_prepayid));
	}

	@Override
	protected void onPostExecute(Map<String, String> result) {
		// TODO Auto-generated method stub
		if (dialog != null) {
			dialog.dismiss();
		}
		GetPrepayIdTask.sb.append("prepay_id\n" + result.get("prepay_id")
				+ "\n\n");
		GetPrepayIdTask.resultunifiedorder = result;

		Log.e("orion-result_code-->", result.get("result_code"));
		if (result.get("result_code").equals("SUCCESS")) {
			Log.e("orion-result_code-->", "IS SUCCESS!");
			genPayReq();
		} else {
			Message msg = new Message();
			msg.obj = result.get("result_code");
			msg.what = 800;
			// MainActivity.handler.sendMessage(msg);
		}
	}

	private void genPayReq() {
		// TODO Auto-generated method stub
		GetPrepayIdTask.payReq.appId = Constants.APP_ID;
		GetPrepayIdTask.payReq.partnerId = Constants.MCH_ID;
		GetPrepayIdTask.payReq.prepayId = GetPrepayIdTask.resultunifiedorder
				.get("prepay_id");
		GetPrepayIdTask.payReq.packageValue = "Sign=WXPay";
		GetPrepayIdTask.payReq.nonceStr = GetPrepayIdTask.genNonceStr();
		GetPrepayIdTask.payReq.timeStamp = String.valueOf(GetPrepayIdTask
				.genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid",
				GetPrepayIdTask.payReq.appId));
		signParams.add(new BasicNameValuePair("noncestr",
				GetPrepayIdTask.payReq.nonceStr));
		signParams.add(new BasicNameValuePair("package",
				GetPrepayIdTask.payReq.packageValue));
		signParams.add(new BasicNameValuePair("partnerid",
				GetPrepayIdTask.payReq.partnerId));
		signParams.add(new BasicNameValuePair("prepayid",
				GetPrepayIdTask.payReq.prepayId));
		signParams.add(new BasicNameValuePair("timestamp",
				GetPrepayIdTask.payReq.timeStamp));

		GetPrepayIdTask.payReq.sign = genAppSign(signParams);

		GetPrepayIdTask.sb.append("sign\n" + GetPrepayIdTask.payReq.sign
				+ "\n\n");

		Log.e("orion-signParams-->", signParams.toString());

		sendPayReq(GetPrepayIdTask.payReq);
	}

	private void sendPayReq(PayReq payReq) {
		// TODO Auto-generated method stub
		GetPrepayIdTask.msgApi.registerApp(Constants.APP_ID);
		GetPrepayIdTask.msgApi.sendReq(payReq);
	}

	@Override
	protected Map<String, String> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		String url = String
				.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
		String entity = genProductArgs();
		Log.i("orion-entity-->", entity);
		byte[] buf = HttpWxUtil.httpPost(url, entity);
		String content = new String(buf);
		Log.i("orion-content-->", content);
		Map<String, String> xml = decodeXml(content);
		return xml;
	}

	private Map<String, String> decodeXml(String content) {
		// TODO Auto-generated method stub
		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("xml".equals(nodeName) == false) {
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}
			return xml;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String genProductArgs() {
		// TODO Auto-generated method stub
		List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
		packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
		packageParams.add(new BasicNameValuePair("body", GetPrepayIdTask.body));
		packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
		packageParams.add(new BasicNameValuePair("nonce_str", GetPrepayIdTask
				.genNonceStr()));
		packageParams.add(new BasicNameValuePair("notify_url",
				GetPrepayIdTask.notify_url));
		packageParams
				.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
		try {
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					GetPhoneHostIp.getPhoneHostIp()));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		packageParams.add(new BasicNameValuePair("total_fee",
				GetPrepayIdTask.total_fee));
		packageParams.add(new BasicNameValuePair("trade_type", "APP"));
		String sign = genPackageSign(packageParams);
		packageParams.add(new BasicNameValuePair("sign", sign));
		String xmlString = toXml(packageParams);
		return xmlString;
	}

	private String toXml(List<NameValuePair> params) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");
			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		Log.i("orion-sb--->", sb.toString());
		sb.append("</xml>");
		return sb.toString();
	}

	private String genPackageSign(List<NameValuePair> params) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append("=");
			sb.append(params.get(i).getValue());
			sb.append("&");
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase(Locale.CHINA);
		Log.i("orion-packageSign-->", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		GetPrepayIdTask.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		Log.e("orion-appSign-->", appSign);
		return appSign;
	}

	private String genOutTradNo() {
		// TODO Auto-generated method stub
		Random random = new Random();
		int time = (int) System.currentTimeMillis();
		String outTradNo = MD5.getMessageDigest(String.valueOf(
				random.nextInt(10000) + time).getBytes());
		return outTradNo;
	}

}