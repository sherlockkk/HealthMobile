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
				// 需要在线程执行的方法
				try {
					InputStream is = getXml(); // 获取xml内容
					getUpdataInfo(is); // 调用解析方法
					serverVersion = info.getVersion(); // 获得服务器版本
					Log.i("cc",
							"check--infoVersion=" + info.getVersion()
									+ "infoURL=" + info.getUrl() + "infoAbout="
									+ info.getAbout());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 给handler发送一个消息
				mHandler.sendEmptyMessage(new Message().what = 1);
			}
		}.start();
		mWebView = (WebView) findViewById(R.id.web_view);

		// 配置WebView的基本属性
		// initSetting();
		WebSettings webSettings = mWebView.getSettings();

		// 使能JavaScript
		webSettings.setJavaScriptEnabled(true);

		// 支持中文，否则页面中中文显示乱码
		webSettings.setDefaultTextEncodingName("GBK");

		// 限制在WebView中打开网页，而不用默认浏览器
		// mWebView.setWebViewClient(new WebViewClient());

		// 用JavaScript调用Android函数：
		// 先建立桥梁类，将要调用的Android代码写入桥梁类的public函数
		// 绑定桥梁类和WebView中运行的JavaScript代码
		// 将一个对象起一个别名传入，在JS代码中用这个别名代替这个对象，可以调用这个对象的一些方法
		mWebView.addJavascriptInterface(new WebAppInterface(this),
				"myInterfaceName");

		// mWebView.loadUrl(web_url);
		mWebView.loadUrl(Config.HOST_URL);
	}

	// 调用获得和解析xml的方法，（异步或线程中操作）；

	// Handler消息接收机制
	private Handler mHandler = new Handler() {
		// Handler接收到相应消息进行刷新ui等操作
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 收到消息，在此进行ui相关操作，如将解析的内容显示出来。
				Log.i("cc", "--检查版本...--");
				checkVersion();
			}
		}
	};

	/**
	 * 自定义的Android代码和JavaScript代码之间的桥梁类
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

		// 如果target 大于等于API 17，则需要加上如下注解
		@JavascriptInterface
		public void call(String phoneCode) {

			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneCode));
			startActivity(intent);
		}

		/**
		 * 分享
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
			startActivity(Intent.createChooser(intent, "邀请好友"));
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
				// 让Toast的显示时间和等待时间相同
				Toast.makeText(this, "再按一次退出", (int) waitTime).show();
				touchTime = currentTime;
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 退出程序提示
	public static void dialog_Exit(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	// 配置webview的基本属性
	private void initSetting() {
		// 关闭滑动条
		mWebView.setVerticalScrollBarEnabled(true);
		// 设置控制器
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebChromeClient(new PAWebChromeClient());

		// 配置 WebView属性，开启离线缓存等等
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

		// 关闭缩放功能
		settings.setSupportZoom(false);

		CookieSyncManager.createInstance(MainActivity.this);

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// 监听下载功能，当用户点击下载链接的时候，直接调用系统的浏览器来下载
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
					.setPositiveButton("确定", new OnClickListener() {

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
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).setNeutralButton("取消", new OnClickListener() {

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
	 * 检查更新版本
	 */
	private int serverVersion;

	public void checkVersion() {

		Log.i("ac", "------------");
		if (Config.localVersion < serverVersion) {
			Log.i("checkversion", "==============================");
			// 发现新版本，提示用户更新
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("软件升级")
					.setMessage("发现新版本,建议立即更新使用.")
					.setPositiveButton("更新",
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
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			alert.create().show();
		} else {
			// 清理工作
			// cheanUpdateFile()
		}
	}

	public UpdataInfo getUpdataInfo(InputStream is) throws Exception {

		UpdataInfo info = null;
		XmlPullParser parser = Xml.newPullParser();
		Log.i("cc", "--getUpdataInfo--");
		parser.setInput(is, "UTF-8");// 设置解析的数据源，编码格式
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT: // 开始解析
				// 可在此做初始化相关工作
				info = new UpdataInfo();
				Log.i("UpdatePullParser", "--START_DOCUMENT--");
				break;
			case XmlPullParser.START_TAG:
				Log.i("UpdatePullParser", "--START_TAG--");
				String tag = parser.getName();
				if ("version".equals(tag)) {
					info.setVersion(new Integer(parser.nextText())); // 获取版本号
				} else if ("url".equals(tag)) {
					info.setUrl(parser.nextText()); // 获取url地址
				} else if ("about".equals(tag)) {
					info.setAbout(parser.nextText()); // 获取相关信息
				}
				break;
			case XmlPullParser.END_TAG:// 读完一个元素，如有多个元素，存入容器中
				break;
			default:
				break;
			}
			event = parser.next();
		}
		return info; // 返回一个UpdataInfo实体
	}

	public InputStream getXml() throws Exception {

		String httpUrl = Config.UPDATE_URL;// 服务器下存放apk信息的xml文件
		Log.i("cc", "--  getXml  Ready!!!  --");

		HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl)
				.openConnection();

		Log.i("cc", "--连接服务器中...--");
		conn.setReadTimeout(5000); // 设置连接超时的时间
		// conn.setRequestMethod("GET");
		conn.connect(); // 开始连接
		Log.i("cc", "--开始连接--");

		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			Log.i("cc", "--连接服务器成功--");
			return is; // 返回InputStream
		} else {
			Log.i("cc", "---连接失败,即将断开连接---");
		}
		conn.disconnect(); // 断开连接
		return null;

	}

}
