package com.boyu100.snfc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boyu100.snfc.R;
import com.boyu100.snfc.base.BaseActivity;
import com.boyu100.snfc.base.Constants;
import com.boyu100.snfc.uis.ProgressWebView;
import com.boyu100.snfc.utils.PreferencesUtils;
import com.boyu100.snfc.utils.ToastUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.third.data.ShareType;
import com.third.data.ThirdDataProvieder;
import com.third.model.ShareContent;
import com.third.weichat.WeiChatLoginManager;
import com.third.weichat.WeiChatPayManager;
import com.third.weichat.WeiChatShareManager;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebViewActivity extends BaseActivity{
	WebView mWebView;
	ProgressBar mProgressBar;
	/**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    private WeiChatShareManager mWeChatShareManager;
    private WeiChatLoginManager mWeChatLoginManager;
    private WeiChatPayManager mWeChatPayManager;
    
    private void setWebView() {
    	WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		
		String ua =  settings.getUserAgentString();
		settings.setUserAgentString(ua.replace("Android","SNFCANDROID"));

		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);

		settings.setAllowFileAccess(true);
		settings.setBlockNetworkImage(false);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadsImagesAutomatically(true);

		settings.setDatabaseEnabled(true);
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setGeolocationDatabasePath(dir);
		settings.setGeolocationEnabled(true);
		settings.setDomStorageEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mProgressBar.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mProgressBar.setVisibility(View.GONE);
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				super.onReceivedIcon(view, icon);
			}
		});

    }
    
	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mWebView = (WebView) findViewById(R.id.webView);
		mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
		setWebView();
		 
		ThirdDataProvieder.initWechat(Constants.WECHAT_APP_ID, Constants.WECHAT_APP_SECRET);
        mWeChatShareManager = new WeiChatShareManager(this);
        mWeChatLoginManager = new WeiChatLoginManager(this);
        mWeChatPayManager = new WeiChatPayManager(this);
        
		mWebView.addJavascriptInterface(new Object(){
			
			@JavascriptInterface
			public void jsBridgeLogin() {
				mWeChatLoginManager.login();
			}

			/**
			 * 1.分享，首页还需要一个界面，就是分享到朋友还是，朋友圈
			 * 2.分享的标题，内容，图片和链接需要通过方法传递给我
			 */
			@JavascriptInterface
			public void jsBridgeShare(String type, String title, String content1, String url) {
				ShareContent content = new ShareContent();
				content.setTitle(title);
				content.setURL(url);
				content.setShareImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
				content.setContent(content1);
				mWeChatShareManager.share(content, Integer.parseInt(type), ShareType.WEBPAGE);
			}

			@JavascriptInterface
			public void jsBridgeScan() {
				ToastUtils.showShortToast("正在启动扫描...");
				Intent intent = new Intent(WebViewActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
			}

			@JavascriptInterface
			public void jsBridgePay(String prepayId) {
				ToastUtils.showShortToast("I am in pay Action");
				/**
				 * 微信支付的一些参数：
				 * 	IWXAPI api;
					PayReq request = new PayReq();
					request.appId = "wxd930ea5d5a258f4f";
					request.partnerId = "1900000109";
					request.prepayId= "1101000000140415649af9fc314aa427",;
					request.packageValue = "Sign=WXPay";
					request.nonceStr= "1101000000140429eb40476f8896f4c9";
					request.timeStamp= "1398746574";
					request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
					api.sendReq(req);
				 */
				mWeChatPayManager.pay(prepayId);
			}
		}, "jsApi");
		mWebView.loadUrl("http://123.206.201.153/h5/");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mWebView != null && keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}
	
//    private long exitTime = 0;
//
//   @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//            if((System.currentTimeMillis()-exitTime) > 2000){
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
	
  	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {  
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    
                    if(result.contains("shop.sunyoo51")) {
                        mWebView.loadUrl("javascript:jsBridgeRedirect('"+ result +"')");
                    }else {
                        Intent intent = new Intent();        
                        intent.setAction("android.intent.action.VIEW");    
                        Uri url = Uri.parse(result);   
                        intent.setData(url);  
                        startActivity(intent);
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(WebViewActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        } 
    }
	
  	private WechatLoginBroadcast mLoginBroadcast;
  	public static final String FILTER_LOGIN_BROADCAST = "login_broad_cast";
  	
  	private void registerLoginBroadcast() {
  		mLoginBroadcast = new WechatLoginBroadcast();
  		IntentFilter filter = new IntentFilter(FILTER_LOGIN_BROADCAST);
  		registerReceiver(mLoginBroadcast, filter);
  	}
  	
  	@Override
  	protected void onStart() {	
  		super.onStart();
  		registerLoginBroadcast();
  	}
  	
  	@Override
  	protected void onDestroy() {
  		super.onDestroy();
  		if(mLoginBroadcast!= null)  {
  			unregisterReceiver(mLoginBroadcast);
  		}
  	}
  	
  	class WechatLoginBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String code = PreferencesUtils.getString(WebViewActivity.this, Constants.WECHAT_LOGIN_CODE, "-1");
//			ToastUtils.showShortToast("微信登陆code:" + code);
			if(!code.equals("-1")) {
                mWebView.loadUrl("javascript:jsBridgeLoginReturn('"+ code +"')");
			}
		}
  		
  	}
  	
  	/**
  	 * 正则判断是否是站内链接
  	 * @param reg
  	 * @param string
  	 * @return
  	 */
    private boolean startCheck(String reg,String string)  
    {  
        boolean tem=false;  
          
        Pattern pattern = Pattern.compile(reg);  
        Matcher matcher=pattern.matcher(string);  
          
        tem=matcher.matches();  
        return tem;  
    }  
}
