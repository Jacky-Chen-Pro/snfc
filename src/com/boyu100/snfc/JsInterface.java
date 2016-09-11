package com.boyu100.snfc;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class JsInterface {

	Context _context = null;
	WebView _webView = null;

	public JsInterface(Context context, WebView webView) {
		_context = context;
		_webView = webView;
	}

	@JavascriptInterface
	public int callOnJs() {
		return 100;
	}

	@JavascriptInterface
	public void callPhone(String phone) {
		_context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone)));
	}

	@JavascriptInterface
	public double getLocation() {
		return -2;
	}

	@JavascriptInterface
	public void jsBridgeLogin() {
	}

	@JavascriptInterface
	public void jsBridgeShare() {
	}

	@JavascriptInterface
	public void jsBridgeScan() {
	}

	@JavascriptInterface
	public void jsBridgePay() {

	}

}
