package com.alpha.healthmobile;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
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
import com.alpha.healthmobile.utils.HttpUtil;
import com.alpha.healthmobile.wxpay.Constants;
import com.alpha.healthmobile.wxpay.MD5;
import com.alpha.healthmobile.wxpay.OrderDetail;
import com.alpha.healthmobile.wxpay.WxPayUtile;
import com.igexin.sdk.PushManager;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
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
	private static final int SDK_PAY_FLAG = 1; // 支付宝支付旗标

	private IWXAPI api;
	private static String TAG = "WXPay";
	private WebView mWebView;
	long waitTime = 2000;
	long touchTime = 0;

	OrderDetail orderDetail = new OrderDetail();
	UpdataInfo info = new UpdataInfo();

	// 社会化分享
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService(Config.DESCRIPTOR);

	// 设置分享内容
	// mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
	// 设置分享图片, 参数2为图片的url地址
	// mController.setShareMedia(new UMImage(getActivity(),
	// "http://www.umeng.com/images/pic/banner_module_social.png"));
	// 设置分享图片，参数2为本地图片的资源引用
	// mController.setShareMedia(new UMImage(getActivity(), R.drawable.icon));
	// 设置分享图片，参数2为本地图片的路径(绝对路径)
	// mController.setShareMedia(new UMImage(getActivity(),
	// BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		init();
		initSetting();
		ssoConfig();

		mController.getConfig().setPlatforms(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
		PushManager.getInstance().initialize(this.getApplicationContext());

		new Thread() {
			@Override
			public void run() {
				// 需要在线程执行的方法
				try {
					// InputStream is = getXml(); // 获取xml内容
					InputStream is = getUpdateServerAppVersionCodeJson();
					// getUpdataInfo(is); // 调用解析方法

					byte[] buffer = new byte[1024];
					System.out.println(">>>>>>>>>>开始读取更新数据");
					int len = is.read(buffer);
					String str = new String(buffer, 0, len);
					JSONObject json = new JSONObject(str);

					serverVersion = Integer.valueOf(json.getString("data"));
					System.out.println("获取到了服务器上的版本为:" + serverVersion);

					// serverVersion = info.getVersion(); // 获得服务器版本
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

		// mWebView.loadUrl(Config.TEST_URL);
		mWebView.loadUrl(Config.HOST_URL);
	}

	/*
	 * 各平台SSO免登陆配置
	 */
	private void ssoConfig() {
		// TODO Auto-generated method stub
		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MainActivity.this,
				Config.QQ_APPID, Config.QQ_APPKEY);
		qqSsoHandler.addToSocialSDK();
		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				MainActivity.this, Config.QQ_APPID, Config.QQ_APPKEY);
		qZoneSsoHandler.addToSocialSDK();
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(MainActivity.this,
				Config.WX_APPID, Config.WX_APPSECRET);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(MainActivity.this,
				Config.WX_APPID, Config.WX_APPSECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
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
		} // 如果target 大于等于API 17，则需要加上如下注解

		/**
		 * 经纬度获取
		 * 
		 * @return
		 */
		@JavascriptInterface
		public String lnglat() {
			return Config.lnglat;
		}

		/**
		 * 支付宝支付
		 * 
		 * @param subject
		 *            订单主题，对商品或支付单的简要描述
		 * @param body
		 *            商品详情描述
		 * @param price
		 *            订单金额
		 */
		AliPay aliPay = new AliPay();

		@JavascriptInterface
		public void Alipay(String subject, String price) {

			String orderInfo = aliPay.getOrderInfo(subject, price);

			String sign = aliPay.sign(orderInfo);
			try {
				sign = URLEncoder.encode(sign, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 注意：这里的订单信息一定要符合支付宝规范，否则会支付失败，
			// 具体规范参考支付宝官方开发文档
			// http://doc.open.alipay.com/doc2/detail.htm?spm=0.0.0.0.E3Jb4k&treeId=59&articleId=103663&docType=1
			final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
					+ aliPay.getSignType();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					PayTask alipayPayTask = new PayTask(MainActivity.this);
					String result = alipayPayTask.pay(payInfo);
					Log.i("cc", "返回的结果为：" + result);
					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					payHandler.sendMessage(msg);
				}
			};
			// 必须异步调用
			Thread payThread = new Thread(runnable);
			payThread.start();
		}

		private String getTradNo() {
			// TODO Auto-generated method stub
			return aliPay.getOutTradeNo();
		}

		public Map<String, String> getParams() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderNo", getTradNo());
			params.put("type", "AliPay");
			return params;
		}

		/**
		 * 判断支付宝支付状态
		 */
		Handler payHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SDK_PAY_FLAG:
					PayResult payResult = new PayResult((String) msg.obj);
					String resultInfo = payResult.getResult();
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus为“9000”则表示支付成功
					if (TextUtils.equals(resultStatus, "9000")) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									HttpUtil.postMethon(Config.TRADNO_SUBMIT,
											getParams());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						Toast.makeText(MainActivity.this, "支付成功",
								Toast.LENGTH_SHORT).show();
						mWebView.loadUrl(Config.HOST_URL);
					} else {
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(MainActivity.this, "支付结果确认中...",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "支付失败，请重试",
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
		 * 微信支付
		 * 
		 * @param subject
		 * @param body
		 * @param price
		 */
		@JavascriptInterface
		public void WeiXinPay(String subject, String price) {
			regWX();
			orderDetail.setSubject(subject);
			orderDetail.setPrice(price);
			// price + "00"
			WxPayUtile.getInstance(MainActivity.this, price + "00",
					Constants.NOTIFY_URL, subject, genOutTradNo()).doPay();
		}

		/**
		 * 将app注册到微信
		 */
		private void regWX() {
			// TODO Auto-generated method stub
			// 通过WXAPIFactory工厂，获取IWXAPI的实例
			api = WXAPIFactory.createWXAPI(mContext, Constants.APP_ID, false);
			api.registerApp(Constants.APP_ID);
		}

		/**
		 * 第三方登陆JS接口方法
		 * 
		 */
		@JavascriptInterface
		public void login_QQ() {
			Log.i("ccc", ">>>>>>>>>>>>>");
			login(SHARE_MEDIA.QQ);
		}

		@JavascriptInterface
		public void login_WX() {
			login(SHARE_MEDIA.WEIXIN);
		}

		/**
		 * 第三方登陆
		 * 
		 * @param platform
		 */
		private void login(SHARE_MEDIA platform) {
			// TODO Auto-generated method stub
			mController.doOauthVerify(MainActivity.this, platform,
					new UMAuthListener() {

						@Override
						public void onStart(SHARE_MEDIA arg0) {
							// TODO Auto-generated method stub
							Toast.makeText(MainActivity.this, "正在跳转...",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onError(SocializeException arg0,
								SHARE_MEDIA arg1) {
							// TODO Auto-generated method stub
							Log.i("cc", arg0.toString());

						}

						@Override
						public void onComplete(Bundle value,
								SHARE_MEDIA platform) {
							// TODO Auto-generated method stub
							Toast.makeText(MainActivity.this, "跳转成功",
									Toast.LENGTH_SHORT).show();
							System.out.println(value);
							String uid = value.getString("uid");
							if (!TextUtils.isEmpty(uid)) {
								getUserInfo(platform);
							} else {
								Toast.makeText(MainActivity.this, "授权失败",
										Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onCancel(SHARE_MEDIA arg0) {
							// TODO Auto-generated method stub

						}
					});
		}

		/**
		 * 获取授权平台的用户信息
		 * 
		 * @param platform
		 */
		private void getUserInfo(SHARE_MEDIA platform) {
			mController.getPlatformInfo(MainActivity.this, platform,
					new UMDataListener() {

						@Override
						public void onStart() {
							// TODO Auto-generated method stub

						}

						@Override
						public void onComplete(int status,
								Map<String, Object> info) {
							// TODO Auto-generated method stub
							if (info != null) {
								Log.i("cc", info.toString());
								Toast.makeText(MainActivity.this,
										info.toString(), Toast.LENGTH_SHORT)
										.show();
							}
						}
					});
		}

		/**
		 * 分享
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
									Uri content_url = Uri
											.parse(Config.APP_DOWNLOAD_URL);
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

	public InputStream getUpdateServerAppVersionCodeJson() throws Exception {
		String httpUrl = Config.UPDATE_APP_CODE_URL; // 服务器下存放apk信息的api接口
		HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl)
				.openConnection();

		Log.i("cc", "--连接服务器中...--" + httpUrl);
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

	/**
	 * 注意：商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
	 */
	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

}
