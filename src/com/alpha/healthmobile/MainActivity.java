package com.alpha.healthmobile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

import com.igexin.sdk.PushManager;

public class MainActivity extends Activity {
	private WebView mWebView;

	long waitTime = 2000;
	long touchTime = 0;

	UpdataInfo info = new UpdataInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
		initSetting();

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

		// mWebView.loadUrl(web_url);
		mWebView.loadUrl(Config.HOST_URL);
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
		}

		/**
		 * ����
		 * 
		 * @param view
		 */
		@JavascriptInterface
		public void onClickShare() {
			Intent intent = new Intent(Intent.ACTION_SEND);
			// intent.setType("image/png");
			// intent.putExtra(Intent.EXTRA_STREAM,
			// "http://found520.bmob.cn/uploads/55d134f2dfb77.png");
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, Config.SHARE_SUBJECT);
			intent.putExtra(Intent.EXTRA_TEXT, Config.SHARE_TEXT
					+ Config.SHARE_URL);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(intent, "�������"));
		}

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
			alert.setTitle("��������")
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
			// ��������
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

		String httpUrl = "http://zrys.code8086.com/HealthMobile/update/upgrade.xml";// �������´��apk��Ϣ��xml�ļ�
		Log.i("cc", "--  getXml  Ready!!!  --");

		HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl)
				.openConnection();

		Log.i("cc", "--���ӷ�������...--");
		conn.setReadTimeout(10 * 1000); // �������ӳ�ʱ��ʱ��
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