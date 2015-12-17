package com.alpha.healthmobile;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alpha.healthmobile.alipay.AliPay;
import com.alpha.healthmobile.alipay.PayResult;
import com.alpha.healthmobile.wxpay.Constants;
import com.alpha.healthmobile.wxpay.WXPay;
import com.igexin.sdk.PushManager;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class MainActivity extends Activity {
	private static Context mContext;
	private static final int SDK_PAY_FLAG = 1; // ֧����֧�����
	public static Handler weixinPayHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 800:
				//
				Toast.makeText(mContext, "�̻��������ظ�", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(mContext, "֧���ɹ�", Toast.LENGTH_SHORT).show();
				break;
			case -1:
				Bundle bundle = new Bundle();
				bundle = msg.getData();
				String tip = bundle.getString("return_msg");
				Toast.makeText(mContext, "֧��ʧ��" + "		" + tip,
						Toast.LENGTH_SHORT).show();
				break;
			case -2:
				Toast.makeText(mContext, "֧��ȡ��", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	private WebView mWebView;
	long waitTime = 2000;
	long touchTime = 0;

	UpdataInfo info = new UpdataInfo();

	// ��ữ����
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	// ���÷�������
	// mController.setShareContent("������ữ�����SDK�����ƶ�Ӧ�ÿ��������罻�����ܣ�http://www.umeng.com/social");
	// ���÷���ͼƬ, ����2ΪͼƬ��url��ַ
	// mController.setShareMedia(new UMImage(getActivity(),
	// "http://www.umeng.com/images/pic/banner_module_social.png"));
	// ���÷���ͼƬ������2Ϊ����ͼƬ����Դ����
	// mController.setShareMedia(new UMImage(getActivity(), R.drawable.icon));
	// ���÷���ͼƬ������2Ϊ����ͼƬ��·��(����·��)
	// mController.setShareMedia(new UMImage(getActivity(),
	// BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		init();
		initSetting();
		mController.getConfig().setPlatforms(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
		PushManager.getInstance().initialize(this.getApplicationContext());

		new Thread() {
			@Override
			public void run() {
				// ��Ҫ���߳�ִ�еķ���
				try {
					InputStream is = getXml(); // ��ȡxml����
					getUpdataInfo(is); // ���ý�������
					serverVersion = info.getVersion(); // ��÷������汾
					Log.i("cc",
							"check--infoVersion=" + info.getVersion()
									+ "infoURL=" + info.getUrl() + "infoAbout="
									+ info.getAbout());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// ��handler����һ����Ϣ
				mHandler.sendEmptyMessage(new Message().what = 1);
			}
		}.start();
		mWebView = (WebView) findViewById(R.id.web_view);

		// ����WebView�Ļ�������
		// initSetting();
		WebSettings webSettings = mWebView.getSettings();

		// ʹ��JavaScript
		webSettings.setJavaScriptEnabled(true);

		// ֧�����ģ�����ҳ����������ʾ����
		webSettings.setDefaultTextEncodingName("GBK");

		// ������WebView�д���ҳ��������Ĭ�������
		// mWebView.setWebViewClient(new WebViewClient());

		// ��JavaScript����Android������
		// �Ƚ��������࣬��Ҫ���õ�Android����д���������public����
		// ���������WebView�����е�JavaScript����
		// ��һ��������һ���������룬��JS�������������������������󣬿��Ե�����������һЩ����
		mWebView.addJavascriptInterface(new WebAppInterface(this),
				"myInterfaceName");

		mWebView.loadUrl(Config.TEST_URL);
		// mWebView.loadUrl(Config.HOST_URL);
	}

	// ���û�úͽ���xml�ķ��������첽���߳��в�������

	// Handler��Ϣ���ջ���
	private Handler mHandler = new Handler() {
		// Handler���յ���Ӧ��Ϣ����ˢ��ui�Ȳ���
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// �յ���Ϣ���ڴ˽���ui��ز������罫������������ʾ������
				Log.i("cc", "--���汾...--");
				checkVersion();
			}
		}
	};

	/**
	 * �Զ����Android�����JavaScript����֮���������
	 * 
	 * @author 1
	 * 
	 */

	public class WebAppInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		WebAppInterface(Context c) {
			mContext = c;
		}

		// ���target ���ڵ���API 17������Ҫ��������ע��
		@JavascriptInterface
		public void call(String phoneCode) {

			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneCode));
			startActivity(intent);
		} // ���target ���ڵ���API 17������Ҫ��������ע��

		@JavascriptInterface
		public String lnglat() {
			return Config.lnglat;
		}

		/**
		 * ֧����֧��
		 * 
		 * @param subject
		 *            �������⣬����Ʒ��֧�����ļ�Ҫ����
		 * @param body
		 *            ��Ʒ��������
		 * @param price
		 *            �������
		 */

		@JavascriptInterface
		public void Alipay(String subject, String price) {
			AliPay aliPay = new AliPay();
			String orderInfo = aliPay.getOrderInfo(subject, price);
			String sign = aliPay.sign(orderInfo);
			try {
				sign = URLEncoder.encode(sign, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ע�⣺����Ķ�����Ϣһ��Ҫ����֧�����淶�������֧��ʧ�ܣ�
			// ����淶�ο�֧�����ٷ������ĵ�
			// http://doc.open.alipay.com/doc2/detail.htm?spm=0.0.0.0.E3Jb4k&treeId=59&articleId=103663&docType=1
			final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
					+ aliPay.getSignType();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					PayTask alipayPayTask = new PayTask(MainActivity.this);
					String result = alipayPayTask.pay(payInfo);
					Log.i("cc", "���صĽ��Ϊ��" + result);
					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					payHandler.sendMessage(msg);
				}
			};
			// �����첽����
			Thread payThread = new Thread(runnable);
			payThread.start();
		}

		/**
		 * �ж�֧����֧��״̬
		 */
		Handler payHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SDK_PAY_FLAG:
					PayResult payResult = new PayResult((String) msg.obj);
					String resultInfo = payResult.getResult();
					String resultStatus = payResult.getResultStatus();
					// �ж�resultStatusΪ��9000�����ʾ֧���ɹ�
					if (TextUtils.equals(resultStatus, "9000")) {
						Toast.makeText(MainActivity.this, "֧���ɹ�",
								Toast.LENGTH_SHORT).show();
						mWebView.loadUrl(Config.TEST_URL);
					} else {
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(MainActivity.this, "֧�����ȷ����...",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "֧��ʧ�ܣ�������",
									Toast.LENGTH_SHORT).show();
							mWebView.goBack();
						}
					}
					break;

				default:
					break;
				}
			}
		};

		/**
		 * ΢��֧��
		 * 
		 * @param subject
		 * @param body
		 * @param price
		 */
		@JavascriptInterface
		public void WeiXinPay(String subject, String price) {
			// IWXAPI weixinIwxapi = null;
			// weixinIwxapi.registerApp(Constants.APP_ID);
			WXPay.getInstance(mContext, price, Constants.NOTIFY_URL, subject)
					.doPay();
		}

		/**
		 * ����
		 * 
		 * @param view
		 */

		@JavascriptInterface
		public void onClickShare() {
			mController.openShare(MainActivity.this, false);
			ssoConfig();
			QZoneShareContent();
			QQShareContent();
			WXShareContent();
			WXCircleShareContent();
		}

		/*
		 * ��ƽ̨SSO���½����
		 */
		private void ssoConfig() {
			// TODO Auto-generated method stub
			// ����1Ϊ��ǰActivity������2Ϊ��������QQ���������APP ID������3Ϊ��������QQ���������APP kEY.
			UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MainActivity.this,
					Config.QQ_APPID, Config.QQ_APPKEY);
			qqSsoHandler.addToSocialSDK();
			// ����1Ϊ��ǰActivity������2Ϊ��������QQ���������APP ID������3Ϊ��������QQ���������APP kEY.
			QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
					MainActivity.this, Config.QQ_APPID, Config.QQ_APPKEY);
			qZoneSsoHandler.addToSocialSDK();
			// ���΢��ƽ̨
			UMWXHandler wxHandler = new UMWXHandler(MainActivity.this,
					Config.WX_APPID, Config.WX_APPSECRET);
			wxHandler.addToSocialSDK();
			// ���΢������Ȧ
			UMWXHandler wxCircleHandler = new UMWXHandler(MainActivity.this,
					Config.WX_APPID, Config.WX_APPSECRET);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();
		}
	}

	private void QZoneShareContent() {
		// TODO Auto-generated method stub
		QZoneShareContent qzoneShare = new QZoneShareContent();
		qzoneShare.setTargetUrl(Config.SHARE_URL);
		qzoneShare.setShareContent(Config.SHARE_TEXT);
		qzoneShare.setTitle(Config.SHARE_TITLE);
		mController.setShareMedia(qzoneShare);
	}

	private void QQShareContent() {
		// TODO Auto-generated method stub
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(Config.SHARE_TEXT + Config.SHARE_URL);
		qqShareContent.setTargetUrl(Config.SHARE_URL);
		qqShareContent.setTitle(Config.SHARE_TITLE);
		// qqShareContent.setShareImage(new UMImage(MainActivity.this,
		// R.drawable.ic_launcher));
		mController.setShareMedia(qqShareContent);
	}

	private void WXShareContent() {
		// TODO Auto-generated method stub
		WeiXinShareContent wxShareContent = new WeiXinShareContent();
		wxShareContent.setShareContent(Config.SHARE_TEXT + Config.SHARE_URL);
		wxShareContent.setTargetUrl(Config.SHARE_URL);
		wxShareContent.setTitle(Config.SHARE_TITLE);
		wxShareContent.setShareImage(new UMImage(MainActivity.this,
				R.drawable.ic_launcher));
		mController.setShareMedia(wxShareContent);
	}

	private void WXCircleShareContent() {
		// TODO Auto-generated method stub
		CircleShareContent wxCircleShareContent = new CircleShareContent();
		wxCircleShareContent.setShareContent(Config.SHARE_TEXT
				+ Config.SHARE_URL);
		wxCircleShareContent.setTargetUrl(Config.SHARE_URL);
		wxCircleShareContent.setTitle(Config.SHARE_TITLE);
		wxCircleShareContent.setShareImage(new UMImage(MainActivity.this,
				R.drawable.ic_launcher));
		mController.setShareMedia(wxCircleShareContent);
	}

	private void init() {
		mWebView = (WebView) findViewById(R.id.web_view);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else if ((currentTime - touchTime) >= waitTime) {
				// ��Toast����ʾʱ��͵ȴ�ʱ����ͬ
				Toast.makeText(this, "�ٰ�һ���˳�", (int) waitTime).show();
				touchTime = currentTime;
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// �˳�������ʾ
	public static void dialog_Exit(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("ȷ��Ҫ�˳���?");
		builder.setTitle("��ʾ");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	// ����webview�Ļ�������
	private void initSetting() {
		// �رջ�����
		mWebView.setVerticalScrollBarEnabled(true);
		// ���ÿ�����
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebChromeClient(new PAWebChromeClient());

		// ���� WebView���ԣ��������߻���ȵ�
		WebSettings settings = mWebView.getSettings();
		settings.setLoadWithOverviewMode(true);

		String cacheDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		settings.setAppCachePath(cacheDir);
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setAllowFileAccess(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setAppCacheMaxSize(1024 * 1024 * 50);
		settings.setDomStorageEnabled(true);
		settings.setAppCacheEnabled(true);
		settings.setJavaScriptEnabled(true);
		settings.setSupportMultipleWindows(true);
		settings.setDatabaseEnabled(true);
		String dbPath = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setDatabasePath(dbPath);

		// �ر����Ź���
		settings.setSupportZoom(false);

		CookieSyncManager.createInstance(MainActivity.this);

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// �������ع��ܣ����û�����������ӵ�ʱ��ֱ�ӵ���ϵͳ�������������
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
	}

	class PAWebChromeClient extends WebChromeClient {
		private WebView newWebView = null;

		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded,
				long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(spaceNeeded * 2);
		}

		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());
			builder.setMessage(message)
					.setPositiveButton("ȷ��", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).create().show();

			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());
			builder.setMessage(message)
					.setPositiveButton("ȷ��", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).setNeutralButton("ȡ��", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					}).create().show();
			return true;
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog,
				boolean isUserGesture, Message resultMsg) {
			newWebView = new WebView(view.getContext());
			view.addView(newWebView);
			WebSettings settings = newWebView.getSettings();
			settings.setJavaScriptEnabled(true);
			newWebView.setWebViewClient(new WebViewClient());
			newWebView.setWebChromeClient(this);
			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(newWebView);
			resultMsg.sendToTarget();
			return true;
		}

		@Override
		public void onCloseWindow(WebView view) {

			if (newWebView != null) {
				newWebView.setVisibility(View.GONE);
				view.removeView(newWebView);
			}
		}

	}

	/**
	 * �����°汾
	 */
	private int serverVersion;

	public void checkVersion() {

		Log.i("ac", "------------");
		if (Config.localVersion < serverVersion) {
			Log.i("checkversion", "==============================");
			// �����°汾����ʾ�û�����
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("�������")
					.setMessage("�����°汾,������������ʹ��.")
					.setPositiveButton("����",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									Uri content_url = Uri.parse(info.getUrl());
									intent.setData(content_url);
									startActivity(intent);
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			alert.create().show();
		} else {
			// ������
			// cheanUpdateFile()
		}
	}

	public UpdataInfo getUpdataInfo(InputStream is) throws Exception {

		UpdataInfo info = null;
		XmlPullParser parser = Xml.newPullParser();
		Log.i("cc", "--getUpdataInfo--");
		parser.setInput(is, "UTF-8");// ���ý���������Դ�������ʽ
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT: // ��ʼ����
				// ���ڴ�����ʼ����ع���
				info = new UpdataInfo();
				Log.i("UpdatePullParser", "--START_DOCUMENT--");
				break;
			case XmlPullParser.START_TAG:
				Log.i("UpdatePullParser", "--START_TAG--");
				String tag = parser.getName();
				if ("version".equals(tag)) {
					info.setVersion(new Integer(parser.nextText())); // ��ȡ�汾��
				} else if ("url".equals(tag)) {
					info.setUrl(parser.nextText()); // ��ȡurl��ַ
				} else if ("about".equals(tag)) {
					info.setAbout(parser.nextText()); // ��ȡ�����Ϣ
				}
				break;
			case XmlPullParser.END_TAG:// ����һ��Ԫ�أ����ж��Ԫ�أ�����������
				break;
			default:
				break;
			}
			event = parser.next();
		}
		return info; // ����һ��UpdataInfoʵ��
	}

	public InputStream getXml() throws Exception {

		String httpUrl = Config.UPDATE_URL;// �������´��apk��Ϣ��xml�ļ�
		HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl)
				.openConnection();

		Log.i("cc", "--���ӷ�������...--");
		conn.setReadTimeout(5000); // �������ӳ�ʱ��ʱ��
		// conn.setRequestMethod("GET");
		conn.connect(); // ��ʼ����
		Log.i("cc", "--��ʼ����--");

		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			Log.i("cc", "--���ӷ������ɹ�--");
			return is; // ����InputStream
		} else {
			Log.i("cc", "---����ʧ��,�����Ͽ�����---");
		}
		conn.disconnect(); // �Ͽ�����
		return null;

	}

}
