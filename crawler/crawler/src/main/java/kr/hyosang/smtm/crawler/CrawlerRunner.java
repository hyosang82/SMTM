package kr.hyosang.smtm.crawler;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import kr.hyosang.smtm.common.BankInfo;
import kr.hyosang.smtm.crawler.kb.KbWorker;
import kr.hyosang.smtm.crawler.wooribank.WooribankWorker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyosang on 2016. 10. 26..
 */
public class CrawlerRunner extends Thread {
    @Override
    public void run() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(this.getClass().getClassLoader().getResourceAsStream("SMTM-88568172169f.json"))
                .setDatabaseUrl("https://smtm-13000.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        try {
            List<WorkerBase> workers = new ArrayList<>();
            workers.add(new KbWorker());
            workers.add(new WooribankWorker());

            int runningThread;

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bank");
            BankInfo bankInfo;

            do {
                runningThread = 0;

                for(WorkerBase t : workers) {
                    switch(t.getState()) {
                        case NEW:
                            bankInfo = t.getBankInfo();
                            ref.child(bankInfo.code).setValue(bankInfo.asMap());

                            t.start();
                            runningThread++;
                            break;

                        case RUNNABLE:
                        case BLOCKED:
                        case WAITING:
                        case TIMED_WAITING:
                            runningThread++;
                            break;

                        case TERMINATED:
                            break;
                    }

                    if(runningThread > 3) {
                        break;
                    }
                }

                Thread.sleep(5000);
            }while(runningThread > 0);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
