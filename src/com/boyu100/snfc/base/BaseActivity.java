package com.boyu100.snfc.base;

import com.boyu100.snfc.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
	protected ProgressDialog mProgressDialog;

	protected void showProgressDialog() {
		if (mProgressDialog == null || !mProgressDialog.isShowing()) {
			mProgressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
			mProgressDialog.setCanceledOnTouchOutside(true);
		}
	}
	

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
