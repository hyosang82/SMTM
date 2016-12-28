package kr.hyosang.smtm.android.common;

import android.content.Context;

/**
 * Created by hyosang on 2016. 12. 28..
 */

public class Define {
    public static String MARKER_PATH;

    public static void init(Context context) {
        MARKER_PATH = context.getCacheDir().getAbsolutePath();

    }
}
