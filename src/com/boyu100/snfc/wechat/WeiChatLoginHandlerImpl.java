package com.boyu100.snfc.wechat;

import android.util.Log;
import android.widget.Toast;

import com.boyu100.snfc.base.AppContext;
import com.boyu100.snfc.http.HttpUtil;
import com.boyu100.snfc.http.OnResultListener;
import com.third.weichat.WeiChatLoginHandler;

import org.json.JSONException;

public class WeiChatLoginHandlerImpl extends WeiChatLoginHandler {

    private static WeiChatLoginHandlerImpl weiChatLoginHandler = new WeiChatLoginHandlerImpl();

    public static WeiChatLoginHandlerImpl getInstance() {
        return weiChatLoginHandler;
    }


    @Override
    protected void onAuthUrl(String authUrl) {
        Log.d("WeiChatLoginHandlerImpl", authUrl);
        HttpUtil.get(authUrl, new OnResultListener() {
            @Override
            public void onResult(String result) {
                try {
                    requestUserInfo(getAccessUserInfoUrl(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AppContext.getInstance(), "onAuthUrl", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestUserInfo(String accessUserInfoUrl) {
        HttpUtil.get(accessUserInfoUrl, new OnResultListener() {
            @Override
            public void onResult(String result) {
                Log.d("WeiChatLoginHandlerImpl", "parseUserInfo(result):" + parseUserInfo(result));
            }
        });
    }

}
