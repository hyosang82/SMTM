package kr.hyosang.smtm.common;

import kr.hyosang.smtm.android.common.Logger;

/**
 * Created by hyosang on 2016. 12. 23..
 */

public class Util {
    public static long parseLong(String val, long defValue) {
        try {
            return Long.parseLong(val, 10);
        }catch(NumberFormatException e) {
            Logger.e(e);
            return defValue;
        }
    }
}
