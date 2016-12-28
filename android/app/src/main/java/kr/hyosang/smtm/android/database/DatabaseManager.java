package kr.hyosang.smtm.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import kr.hyosang.smtm.android.common.Logger;
import kr.hyosang.smtm.common.Atm;
import kr.hyosang.smtm.common.BankInfo;

/**
 * Created by hyosang on 2016. 11. 24..
 */

public class DatabaseManager extends SQLiteOpenHelper {
    private static DatabaseManager mInstance = null;

    private static final int VERSION = 1;

    public static final String PREF_LAST_UPDATED = "last_updated";

    private static final String [] ALL_ATM_COLUMNS = new String[]{
            "seq", "bankcode", "branchcode", "cornercode",
            "name", "address", "description",
            "lng", "lat", "opentime", "closetime"
    };

    private DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer query = new StringBuffer();

        query.append("CREATE TABLE pref (")
                .append(" key TEXT NOT NULL PRIMARY KEY,")
                .append(" data TEXT NOT NULL")
                .append(")");
        db.execSQL(query.toString());

        query.setLength(0);
        query.append("CREATE TABLE bankinfo (")
                .append(" code TEXT NOT NULL PRIMARY KEY,")
                .append(" name TEXT ")
                .append(" )");
        db.execSQL(query.toString());

        query.setLength(0);
        query.append("CREATE TABLE atminfo (")
                .append(" seq INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(" bankcode TEXT NOT NULL,")
                .append(" branchcode TEXT,")
                .append(" cornercode TEXT,")
                .append(" name TEXT,")
                .append(" address TEXT,")
                .append(" description TEXT,")
                .append(" lng REAL,")
                .append(" lat REAL,")
                .append(" opentime INTEGER DEFAULT 0,")
                .append(" closetime INTEGER DEFAULT 0")
                .append(")");
        db.execSQL(query.toString());

        Logger.i("Database table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void init(Context context) {
        mInstance = new DatabaseManager(context.getApplicationContext(), "local.db", null, VERSION);

        Logger.i("Database initialized");
    }

    public static DatabaseManager getInstance() {
        return mInstance;
    }

    public long insertBankInfo(BankInfo info) {
        ContentValues values = new ContentValues();
        values.put("code", info.code);
        values.put("name", info.name);

        SQLiteDatabase db = getWritableDatabase();
        long key = db.insert("bankinfo", null, values);
        db.releaseReference();

        return key;
    }

    public boolean isBankExist(String bankCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM bankinfo WHERE code=?", new String[]{bankCode});

        try {
            if (c.getCount() > 0) {
                return true;
            }
            return false;
        }finally {
            db.releaseReference();
        }
    }

    public List<String> getBankCodes() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT code FROM bankinfo", null);
        List<String> codes = new ArrayList<>();

        try {
            while(c.moveToNext()) {
                codes.add(c.getString(c.getColumnIndex("code")));
            }
        }finally {
            db.releaseReference();
        }

        return codes;
    }

    public long insertOrUpdateAtm(Atm atm) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bankcode", atm.bankCode);
        values.put("branchcode", atm.branchCode);
        values.put("cornercode", atm.cornerCode);
        values.put("name", atm.name);
        values.put("address", atm.address);
        values.put("description", atm.description);
        values.put("lng", (double)atm.xE6 / 100000f);
        values.put("lat", (double)atm.yE6 / 100000f);
        values.put("opentime", atm.openTime);
        values.put("closetime", atm.closeTime);

        String wherestr = "bankcode=? AND branchcode=? AND cornercode=?";
        String [] wherearg = new String[]{atm.bankCode, atm.branchCode, atm.cornerCode};

        try {
            Cursor c = db.query("atminfo", new String[]{"seq"}, wherestr, wherearg, null, null, null);
            if (c.moveToNext()) {
                //update
                db.update("atminfo", values, wherestr, wherearg);

                return c.getInt(0);
            } else {
                return db.insert("atminfo", null, values);
            }
        }finally {
            db.releaseReference();
        }
    }

    public List<Atm> getAtmInArea(double left, double top, double right, double bottom) {
        List<Atm> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query("atminfo", ALL_ATM_COLUMNS,
                "(lng > ? AND lng < ?) AND (lat > ? AND lat < ?)",
                new String[]{
                        String.valueOf(left), String.valueOf(right),
                        String.valueOf(bottom), String.valueOf(top)
                }, null, null, "RANDOM()", "20");

        while(c.moveToNext()) {
            list.add(toAtm(c));
        }

        return list;
    }

    private Atm toAtm(Cursor c) {
        Atm atm = new Atm();

        atm.bankCode = c.getString(c.getColumnIndex("bankcode"));
        atm.branchCode = c.getString(c.getColumnIndex("branchcode"));
        atm.cornerCode = c.getString(c.getColumnIndex("cornercode"));
        atm.name = c.getString(c.getColumnIndex("name"));
        atm.address = c.getString(c.getColumnIndex("address"));
        atm.description = c.getString(c.getColumnIndex("description"));
        atm.xE6 = (int)(c.getDouble(c.getColumnIndex("lng")) * 100_000);
        atm.yE6 = (int)(c.getDouble(c.getColumnIndex("lat")) * 100_000);
        atm.openTime = c.getInt(c.getColumnIndex("opentime"));
        atm.closeTime = c.getInt(c.getColumnIndex("closetime"));

        return atm;
    }

    public String getPreference(String key, String defValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("pref", new String[]{"key", "data"}, "key=?", new String[]{key}, null, null, null);
        String val = defValue;
        if(c.moveToNext()) {
            val = c.getString(c.getColumnIndex("data"));
        }
        db.releaseReference();

        return val;
    }

    public boolean setPreference(String key, String val) {
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("data", val);

        SQLiteDatabase db = getWritableDatabase();

        long res = -1;
        Cursor c = db.query("pref", new String[]{"data"}, "key=?", new String[]{key}, null, null, null);
        if(c.moveToNext()) {
            res = db.update("pref", values, "key=?", new String[]{key});
        }else {
            res = db.insert("pref", null, values);
        }

        return (res > 0);

    }
}
