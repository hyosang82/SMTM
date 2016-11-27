package kr.hyosang.smtm.crawler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import kr.hyosang.smtm.common.Atm;
import kr.hyosang.smtm.common.BankInfo;

/**
 * Created by hyosang on 2016. 11. 3..
 */
public abstract class WorkerBase extends Thread {
    protected abstract String getTag();
    protected abstract BankInfo getBankInfo();
    protected abstract String getIndexKey(Atm item);

    protected void log(String str) {
        System.out.println(String.format("[%s] %s", getTag(), str));
    }


    protected void insertOrUpdate(DatabaseReference bankRoot, Atm item) {
        String key = getIndexKey(item);

        DatabaseReference data = bankRoot.child(key);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println(data.getKey());

        //if data exists, update it.

        //else insert new one.

        data.setValue(item);


    }
}
