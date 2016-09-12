package com.boyu100.snfc.base;


import com.uuzuche.lib_zxing.DisplayUtil;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

public class AppContext extends Application {


    private static Handler mGlobalHanlder;
    private static AppContext mAppContext;
    
    public static Context getAppContext() {
    	return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        initDisplayOpinion();
        mAppContext = this;
        mGlobalHanlder = new Handler(Looper.getMainLooper());
    }

    public static Handler getGlobalHanlder() {
        return mGlobalHanlder;
    }

    public static void post(Runnable runnable) {
        mGlobalHanlder.post(runnable);
    }

    public static AppContext getInstance() {
        return mAppContext;
    }
    
    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }
}
