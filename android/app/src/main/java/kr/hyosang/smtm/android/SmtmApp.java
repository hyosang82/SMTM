package kr.hyosang.smtm.android;

import android.app.Application;

import kr.hyosang.smtm.android.database.DatabaseManager;

/**
 * Created by hyosang on 2016. 12. 23..
 */

public class SmtmApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseManager.init(this);
    }
}
