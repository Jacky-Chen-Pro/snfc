package com.third.weichat;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.boyu100.snfc.R;
import com.boyu100.snfc.base.Constants;
import com.boyu100.snfc.utils.ToastUtils;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.third.data.ThirdDataProvieder;

public class WeiChatPayManager {

	private String mWeChatAppId;
    private static IWXAPI mIWXAPI;
	    
	public WeiChatPayManager(Context context) {
		  mWeChatAppId = ThirdDataProvieder.getWechatAppId();
	        if (!TextUtils.isEmpty(mWeChatAppId)) {
	            mIWXAPI = WXAPIFactory.createWXAPI(context, mWeChatAppId, true);
	            if (!mIWXAPI.isWXAppInstalled()) {
	                Toast.makeText(context, context.getString(R.string.share_install_wechat_tips), Toast.LENGTH_SHORT).show();
	                return;
	            } else {
	                mIWXAPI.registerApp(mWeChatAppId);
	            }
	        }
	}
	
	 public static IWXAPI getIWXAPI() {
	        return mIWXAPI;
	    }


	 /**
	  * 支付
	  * prepayId 预支付id
	  * body 商品描述
	  */
    public void pay(String prepayId) {
        if (mIWXAPI != null) {
        	PayReq request = new PayReq();
        	request.appId = mWeChatAppId;
        	request.partnerId = Constants.WECHAT_PARTNER_ID;
        	request.prepayId= prepayId;
        	//暂填写固定值Sign=WXPay
        	request.packageValue = "Sign=WXPay";
        	request.nonceStr= getRandomString(32);
        	request.timeStamp= System.currentTimeMillis()+"";
        	
        	//stringA="appid=wxd930ea5d5a258f4f&body=test & device_info=1000&mch_id=10000100 & nonce_str=ibuaiVcKdpRxkhJA";
        	String signA = "appid=" + mWeChatAppId + "&mch_id=" + Constants.WECHAT_PARTNER_ID +"&nonceStr=" + request.nonceStr;
        	signA = signA +"&key=" + Constants.WECHAT_PARTNER_KEY;
        	signA=EncoderByMd5(signA).toUpperCase();
        	if(TextUtils.isEmpty(signA)) {
        		ToastUtils.showShortToast("SomethingError When call the payReq");
        	}else {
        		request.sign= signA;
            	mIWXAPI.sendReq(request);
        	}
        }
    }
    
	public static String getRandomString(int length) {
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }  
	
	  public String EncoderByMd5(String str) {
		  String newstr = "";
		  try {
			  	MessageDigest md5 = MessageDigest.getInstance("MD5");
	        	newstr = Base64.encode(md5.digest(str.getBytes("utf-8")), Base64.DEFAULT).toString();
		  }catch(Exception e) {
			  	e.printStackTrace();
		  }
        return newstr;
	  }
}
