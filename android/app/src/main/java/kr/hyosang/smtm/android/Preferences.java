package kr.hyosang.smtm.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hyosang on 2016. 11. 26..
 */

public class Preferences {
    private static final String KEY_APP_ID = "app_id";

    private static SharedPreferences mPref = null;

    public static void init(Context context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
}
