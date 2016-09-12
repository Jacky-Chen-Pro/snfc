package com.boyu100.snfc.utils;

import com.boyu100.snfc.base.AppContext;

import android.widget.Toast;

public class ToastUtils {
    public static void showShortToast(String msg) {
        Toast.makeText(AppContext.getAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(int msgId) {
        Toast.makeText(AppContext.getAppContext(), msgId, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String msg) {
        Toast.makeText(AppContext.getAppContext(), msg, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(int msgId) {
        Toast.makeText(AppContext.getAppContext(), msgId, Toast.LENGTH_LONG).show();
    }
}
