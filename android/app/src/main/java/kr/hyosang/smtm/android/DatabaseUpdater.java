package kr.hyosang.smtm.android;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by hyosang on 2016. 11. 24..
 */

public class DatabaseUpdater extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();

        DatabaseReference databaseRoot = firebase.getReference("atm");

        databaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TEST", "CHILD COUNT = " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return null;
    }
}
