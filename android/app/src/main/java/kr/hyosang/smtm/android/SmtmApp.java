package kr.hyosang.smtm.android;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;

import java.util.List;

import kr.hyosang.smtm.android.common.Define;
import kr.hyosang.smtm.android.common.Logger;
import kr.hyosang.smtm.android.database.DatabaseManager;

/**
 * Created by hyosang on 2016. 12. 23..
 */

public class SmtmApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Define.init(getApplicationContext());
        DatabaseManager.init(getApplicationContext());

        List<String> codes = DatabaseManager.getInstance().getBankCodes();
        MarkerManager.init(this, codes);

        FirebaseAuth.getInstance().signInAnonymously();
    }
}
