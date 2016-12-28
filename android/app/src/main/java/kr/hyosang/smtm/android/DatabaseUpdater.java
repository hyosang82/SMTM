package kr.hyosang.smtm.android;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.hyosang.smtm.android.common.Define;
import kr.hyosang.smtm.android.common.Logger;
import kr.hyosang.smtm.android.database.DatabaseManager;
import kr.hyosang.smtm.common.Atm;
import kr.hyosang.smtm.common.BankInfo;

/**
 * Created by hyosang on 2016. 11. 24..
 */

public class DatabaseUpdater extends AsyncTask<Void, Integer, Void> {
    public interface IListener {
        void onProgress(BankInfo bankInfo, int totalCount, int currentCount);
        void onComplete();
    }

    private Object mLock = new Object();

    private List<BankInfo> bankCodes = new ArrayList<>();
    private IListener mListener = null;
    private BankInfo currentBank = null;

    public DatabaseUpdater(IListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        publishProgress(0, 0);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();

        DatabaseReference databaseRoot = firebase.getReference("bank");

        databaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                DatabaseManager db = DatabaseManager.getInstance();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://smtm-13000.appspot.com/marker");

                while(iter.hasNext()) {
                    DataSnapshot data = iter.next();

                    BankInfo bank = data.getValue(BankInfo.class);

                    if(db.isBankExist(bank.code)) {
                        Logger.d("Pass " + bank.name);
                    }else {
                        long rawid = db.insertBankInfo(bank);

                        Logger.d("Inserted " + bank.name + " = " + rawid);

                        if (rawid > 0) {
                            bankCodes.add(bank);
                        }

                        String markerFile = bank.code + ".png";

                        storageRef.child(markerFile).getFile(new File(Define.MARKER_PATH + "/" + markerFile))
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Logger.d("Marker downloaded: " + taskSnapshot.toString());
                                }
                            }
                        );
                    }
                }

                synchronized(mLock) {
                    mLock.notifyAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        synchronized(mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
                Logger.e(e);
            }
        }

        for(BankInfo bankInfo : bankCodes) {
            Logger.d("Bank: " + bankInfo.name);
            currentBank = bankInfo;

            this.publishProgress(0, 0);

            DatabaseReference ref = firebase.getReference("atm/" + bankInfo.code);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                    DatabaseManager db = DatabaseManager.getInstance();
                    int total = (int)dataSnapshot.getChildrenCount();
                    int count = 0;

                    while(iter.hasNext()) {
                        DataSnapshot data = iter.next();
                        Atm atm = data.getValue(Atm.class);

                        long rowid = db.insertOrUpdateAtm(atm);

                        //Logger.d("Result=" + rowid + " :ATM: " + atm.toString());

                        count++;

                        publishProgress(total, count);
                    }

                    synchronized(mLock) {
                        mLock.notifyAll();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            synchronized(mLock) {
                try {
                    mLock.wait();
                }catch(InterruptedException e) {
                    Logger.e(e);
                }
            }

            if(mListener != null) {
                mListener.onComplete();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... info) {
        if(mListener != null) {
            mListener.onProgress(currentBank, info[0], info[1]);
        }

    }
}
