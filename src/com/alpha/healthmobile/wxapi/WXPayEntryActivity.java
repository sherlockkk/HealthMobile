package com.alpha.healthmobile.wxapi;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alpha.healthmobile.Config;
import com.alpha.healthmobile.MainActivity;
import com.alpha.healthmobile.R;
import com.alpha.healthmobile.utils.HttpUtil;
import com.alpha.healthmobile.wxpay.Constants;
import com.alpha.healthmobile.wxpay.WxPayUtile;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付完成回调类
 * 
 * @author SongJian E-mail:1129574214@qq.com
 * @date 2016-1-5 下午6:20:46
 * @version 1.0
 * @parameter
 * @return
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "WXPayEntryActivity";

	private IWXAPI api;
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		initView();
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		textView = (TextView) findViewById(R.id.tv);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(final BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode + "---"
				+ resp.errStr);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.app_tip);
			// builder.setMessage(getString(R.string.pay_result_callback_msg,
			// String.valueOf(resp.errCode)));
			// builder.show();

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					postTradNo();
					// textView.setText(resp.errStr + "即将跳转到首页，请等待...");
					toMainActivity();
				}

			});
		}
	}

	private void postTradNo() {
		// TODO Auto-generated method stub
		try {
			HttpUtil.postMethon(Config.TRADNO_SUBMIT, getParams());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderNo", getOutTradNo());
		params.put("type", "WXPay");
		return params;
	}

	public String getOutTradNo() {
		WxPayUtile wxPayUtile = new WxPayUtile();
		return wxPayUtile.genOutTradNo();
	}

	public void toMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}