package kr.hyosang.smtm.android.common;

import android.util.Log;

import kr.hyosang.smtm.android.BuildConfig;

/**
 * Created by hyosang on 2016. 12. 23..
 */

public class Logger {
    private static final String TAG = "SMTM";

    public static void d(String log) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, log);
        }
    }

    public static void i(String log) {
        Log.i(TAG, log);
    }

    public static void e(String log) {
        Log.e(TAG, log);
    }

    public static void e(Throwable th) {
        Log.e(TAG, Log.getStackTraceString(th));
    }
}
