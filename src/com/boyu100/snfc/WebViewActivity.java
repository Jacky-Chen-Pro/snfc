package com.boyu100.snfc;

import com.boyu100.snfc.R;
import com.boyu100.snfc.base.BaseActivity;
import com.boyu100.snfc.base.Constants;
import com.boyu100.snfc.uis.ProgressWebView;
import com.boyu100.snfc.utils.ToastUtils;
import com.third.data.ShareType;
import com.third.data.ThirdDataProvieder;
import com.third.model.ShareContent;
import com.third.weichat.WeiChatLoginManager;
import com.third.weichat.WeiChatShareManager;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

public class WebViewActivity extends BaseActivity {
	ProgressWebView mWebView;
	
	/**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    private WeiChatShareManager mWeChatShareManager;
    private WeiChatLoginManager mWeChatLoginManager;
    
	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mWebView = (ProgressWebView) findViewById(R.id.webView);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		
		 String ua =  settings.getUserAgentString();
		 settings.setUserAgentString(ua.replace("Android","SNFCANDROID"));
		 
		 Log.i("user-agent", mWebView.getSettings().getUserAgentString());
		 
		 findViewById(R.id.action).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//		        mWeChatLoginManager.login();
				wxShare(0, "title", "content", "http://www.baidu.com","https://www.baidu.com/img/bd_logo1.png");
			}
		});
		 
		ThirdDataProvieder.initWechat(Constants.WECHAT_APP_ID, Constants.WECHAT_APP_SECRET);
        mWeChatShareManager = new WeiChatShareManager(this);
        mWeChatLoginManager = new WeiChatLoginManager(this);

		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);

		mWebView.addJavascriptInterface(new Object(){
			@JavascriptInterface
			public void jsBridgeLogin() {
				ToastUtils.showShortToast("I am in Login Action");
				mWeChatLoginManager.login();
				
				//private void wxShare(int type, String title, String content, String url, String imageUrl)
//				wxShare(0, "title", "content", "http://www.baidu.com","https://www.baidu.com/img/bd_logo1.png");
			}

			/**
			 * 1.分享，首页还需要一个界面，就是分享到朋友还是，朋友圈
			 * 2.分享的标题，内容，图片和链接需要通过方法传递给我
			 */
			@JavascriptInterface
			public void jsBridgeShare(int type, String title, String content, String url, String imageUrl) {
				ToastUtils.showShortToast("I am in Share Action");
				wxShare(type, title, content, url, imageUrl);
			}

			@JavascriptInterface
			public void jsBridgeScan() {
				ToastUtils.showShortToast("正在启动扫描...");
				Intent intent = new Intent(WebViewActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
			}

			@JavascriptInterface
			public void jsBridgePay() {
				ToastUtils.showShortToast("I am in pay Action");
			}

			@JavascriptInterface
		    public void weiChatLogin() {
		        mWeChatLoginManager.login();
		    }
		}, "jsApi");

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
	
	  private void wxShare(int type, String title, String content, String url, String imageUrl) {
	        ShareContent shareContent = new ShareContent();
	        shareContent.setContent(content);
	        shareContent.setTitle(title);
	        shareContent.setURL(url);
	        shareContent.setImageUrl(imageUrl);
	        //分析PIC或者WEBPAGE请添加图片，直接添加bitmap
	        shareContent.setShareImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
	        mWeChatShareManager.share(shareContent, type, ShareType.WEBPAGE);
	    }
	  
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
	                    mWebView.loadUrl("javascript:jsBridgeRedirect("+ result +")");
	                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
	                    Toast.makeText(WebViewActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
	                }
	            }
	        }
	    }
}
