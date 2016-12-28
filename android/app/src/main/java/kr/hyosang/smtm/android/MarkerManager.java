package kr.hyosang.smtm.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.hyosang.smtm.android.common.Define;
import kr.hyosang.smtm.android.common.Logger;

/**
 * Created by hyosang on 2016. 12. 27..
 */

public class MarkerManager {
    private static MarkerManager mInstance = null;

    private Bitmap defaultMarker = null;
    private Map<String, Bitmap> markerBitmap = null;


    private MarkerManager() {
        markerBitmap = new HashMap<>();
    }

    public static void init(Context context, List<String> bankcodes) {
        mInstance = new MarkerManager();
        (new Loader(mInstance.markerBitmap, bankcodes)).start();
        mInstance.defaultMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_default);
    }

    public static MarkerManager getInstance() {
        return mInstance;
    }


    public Bitmap getMarkerBitmap(String code) {
        Bitmap b = markerBitmap.get(code);
        if(b == null) {
            return defaultMarker;
        }else {
            return b;
        }
    }

    private static class Loader extends Thread {
        private Map<String, Bitmap> bitmaps = null;
        private List<String> codeList = null;

        public Loader(Map<String, Bitmap> bmps, List<String> codes) {
            bitmaps = bmps;
            codeList = codes;
        }

        @Override
        public void run() {
            for(String c : codeList) {
                String path = Define.MARKER_PATH + "/" + c + ".png";
                bitmaps.put(c, BitmapFactory.decodeFile(path));
                Logger.d("Marker loaded: " + c);
            }
        }
    }

}
