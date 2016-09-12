package com.third.weichat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.boyu100.snfc.WebViewActivity;
import com.boyu100.snfc.base.Constants;
import com.boyu100.snfc.utils.PreferencesUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by echo on 5/19/15.
 */
public abstract class WechatHandlerActivity extends FragmentActivity implements IWXAPIEventHandler {

    private IWXAPI mIWXAPI;
    private static final int TYPE_LOGIN = 1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WechatHandlerActivity.this;
        mIWXAPI = WeiChatLoginManager.getIWXAPI();
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }


    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == TYPE_LOGIN) {
                    final String code = ((SendAuth.Resp) resp).code;
                    PreferencesUtils.putString(mContext, Constants.WECHAT_LOGIN_CODE, code);
                    sendBroadcast(new Intent(WebViewActivity.FILTER_LOGIN_BROADCAST));
                    getWeiChatLoginHandler().onReceiveAuthCode(code);
                    finish();
                } else {
                    onShareSuccess();
                    finish();
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (resp.getType() == TYPE_LOGIN) {
                    onLoginCancel();
                } else {
                    onShareCanecl();
                }
                finish();
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                if (resp.getType() == TYPE_LOGIN) {
                    onLoginError();
                } else {
                    onShareError();
                }
                finish();
                break;
        }
    }

    protected abstract WeiChatLoginHandler getWeiChatLoginHandler();


    protected abstract void onShareSuccess();

    protected abstract void onShareError();

    protected abstract void onLoginError();

    protected abstract void onShareCanecl();

    protected abstract void onLoginCancel();


}
