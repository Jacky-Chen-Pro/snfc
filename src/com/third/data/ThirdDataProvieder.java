package com.third.data;

import com.boyu100.snfc.base.Constants;

/**
 * Author Ztiany      <br/>
 * Email ztiany3@gmail.com      <br/>
 * Date 2015-12-26 13:03      <br/>
 * Description：
 */
public class ThirdDataProvieder {

    private static String mWechatAppId = "";
    private static String mWechatSecret = "";


    public static final String SCOPE =                               // 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    /**
     * init wechat config
     */
    public static void initWechat(String wechatAppId, String wechatSecret) {
        mWechatAppId = wechatAppId;
        mWechatSecret = wechatSecret;
    }

    public static String getWechatAppId() {
        return mWechatAppId;
    }

    public static String getWechatSecret() {
        return mWechatSecret;
    }

}
