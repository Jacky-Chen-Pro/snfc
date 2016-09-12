package com.third.weichat;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.widget.Toast;

import com.boyu100.snfc.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.third.data.ShareType;
import com.third.data.ThirdDataProvieder;
import com.third.model.ShareContent;
import com.third.utils.BitmapUtil;
import com.third.utils.ShareUtil;

/**
 * Created by echo on 5/18/15.
 */
public class WeiChatShareManager {

    /**
     * friends
     */
    public static final int FRIEND = SendMessageToWX.Req.WXSceneSession;

    /**
     * friends TimeLine
     */
    public static final int TIME_LINE = SendMessageToWX.Req.WXSceneTimeline;

    @IntDef(value = {FRIEND, TIME_LINE})
    public @interface WeiChatShareType {
    }

    private static final int THUMB_SIZE = 116;


    private Context mContext;


    private IWXAPI mIWXAPI;


    private String mWeChatAppId;


    public WeiChatShareManager(Context context) {
        mContext = context;
        mWeChatAppId = ThirdDataProvieder.getWechatAppId();
        if (!TextUtils.isEmpty(mWeChatAppId)) {
            initWeixinShare(context);
        }

    }


    private void initWeixinShare(Context context) {
        mIWXAPI = WXAPIFactory.createWXAPI(context, mWeChatAppId, true);
        if (!mIWXAPI.isWXAppInstalled()) {
            Toast.makeText(context, context.getString(R.string.share_install_wechat_tips), Toast.LENGTH_SHORT).show();
        } else {
            mIWXAPI.registerApp(mWeChatAppId);
        }
    }


    private void shareText(int shareType, ShareContent shareContent) {

        String text = shareContent.getContent();
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("textshare");
        req.message = msg;
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }


    private void sharePicture(int shareType, ShareContent shareContent) {
        WXImageObject imgObj = new WXImageObject();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("imgshareappdata");
        req.message = msg;
        req.scene = shareType;
        sendShare(shareContent.getShareImageBitmap(), req);
    }


    private void shareWebPage(final int shareType, final ShareContent shareContent) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("webpage");
        req.message = msg;
        req.scene = shareType;
        sendShare(shareContent.getShareImageBitmap(), req);
    }


    private void shareMusic(int shareType, ShareContent shareContent) {

        WXMusicObject music = new WXMusicObject();
        
        music.musicUrl = shareContent.getURL() + "#wechat_music_url=" + shareContent.getMusicUrl();
        WXMediaMessage msg = new WXMediaMessage(music);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("music");
        req.message = msg;
        req.scene = shareType;
        sendShare(shareContent.getShareImageBitmap(), req);
    }


    public void share(ShareContent content, @WeiChatShareType int shareType, @ShareType.Share int contentType) {
        switch (contentType) {
            case ShareType.TEXT:
                shareText(shareType, content);
                break;
            case ShareType.PIC:
                sharePicture(shareType, content);
                break;
            case ShareType.WEBPAGE:
                shareWebPage(shareType, content);
                break;
            case ShareType.MUSIC:
                shareMusic(shareType, content);
        }
    }

    private void sendShare(final Bitmap bitmap, final SendMessageToWX.Req req) {
        Bitmap image = bitmap;
        if (image != null) {
            if (req.message.mediaObject instanceof WXImageObject) {
                req.message.mediaObject = new WXImageObject(image);
            }
            req.message.thumbData = ShareUtil.bmpToByteArray(BitmapUtil.scaleCenterCrop(image, THUMB_SIZE, THUMB_SIZE));
        }
        mIWXAPI.sendReq(req);
    }

}
