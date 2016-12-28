package kr.hyosang.smtm.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.util.List;

import kr.hyosang.smtm.android.common.Logger;
import kr.hyosang.smtm.android.database.DatabaseManager;
import kr.hyosang.smtm.common.Atm;
import kr.hyosang.smtm.common.BankInfo;
import kr.hyosang.smtm.common.Util;

public class MainActivity extends Activity {
    private MapView mapView = null;
    private MapPOIItem [] atmPois = null;
    private List<Atm> mCurrentAtmList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey("0a3c3cbf0463ff4cee15cb9cafd4edb3");

        ViewGroup mapContainer = (ViewGroup) findViewById(R.id.main_map);
        mapContainer.addView(mapView);

        MapPoint pt = MapPoint.mapPointWithGeoCoord(37.541889f, 127.095388f);

        mapView.setMapViewEventListener(mapViewEventListener);
        mapView.setPOIItemEventListener(new MapView.POIItemEventListener() {
            @Override
            public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
                Logger.d("SELECTED");

            }

            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
                Toast.makeText(MainActivity.this, "Balloon:" + mapPOIItem.getTag(), Toast.LENGTH_SHORT).show();
                Logger.d("1111111111");

            }

            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
                Toast.makeText(MainActivity.this, "Balloon2:" + mapPOIItem.getTag(), Toast.LENGTH_SHORT).show();
                Logger.d("2222222222");
            }

            @Override
            public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

            }
        });

        String lastUpdated = DatabaseManager.getInstance().getPreference(DatabaseManager.PREF_LAST_UPDATED, "0");

        if(Util.parseLong(lastUpdated, 0) == 0) {
            //local data is not exists.
            (new DatabaseUpdater(new UpdaterListener(this))).execute();
        }

        atmPois = new MapPOIItem[20];
        for(int i=0;i<atmPois.length ;i++) {
            atmPois[i] = new MapPOIItem();
        }

        //mapView.setMapCenterPoint(pt, false);
    }

    private void refreshMarkers(List<Atm> list) {
        mapView.removePOIItems(atmPois);
        mCurrentAtmList = list;

        if(list.size() > 0) {
            for(int i=0;i<list.size();i++) {
                if(atmPois.length < i) break;

                MapPOIItem poi = atmPois[i];
                Atm atm = list.get(i);

                poi.setItemName(atm.name);
                poi.setTag(i);
                poi.setMapPoint(MapPoint.mapPointWithGeoCoord((double) atm.yE6 / 100_000f, (double) atm.xE6 / 100_000f));
                poi.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poi.setCustomImageBitmap(MarkerManager.getInstance().getMarkerBitmap(atm.bankCode));
                poi.setCustomImageAutoscale(false);
                poi.setCustomImageAnchor(0.5f, 1.0f);

                mapView.addPOIItem(poi);
            }
        }
    }

    private MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {

        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
            MapPointBounds bounds = mapView.getMapPointBounds();

            MapPoint.GeoCoordinate leftBottom = bounds.bottomLeft.getMapPointGeoCoord();
            MapPoint.GeoCoordinate rightTop = bounds.topRight.getMapPointGeoCoord();

            DatabaseManager db = DatabaseManager.getInstance();
            List<Atm> list = db.getAtmInArea(leftBottom.longitude, rightTop.latitude, rightTop.longitude, leftBottom.latitude);

            refreshMarkers(list);
        }
    };

    private class UpdaterListener implements DatabaseUpdater.IListener {
        private ProgressDialog progDlg = null;

        public UpdaterListener(Context context) {
            progDlg = new ProgressDialog(context);
            progDlg.setTitle("Database Update");
            progDlg.setIndeterminate(true);
            progDlg.setCancelable(false);
            progDlg.show();
        }

        @Override
        public void onProgress(BankInfo bankInfo, int totalCount, int currentCount) {
            if(bankInfo == null) {
                progDlg.setMessage("데이터베이스 다운로드 중...");
            }else {
                progDlg.setMessage(String.format("[%s] 업데이트중 (%d/%d)...", bankInfo.name, currentCount, totalCount));
            }
        }

        @Override
        public void onComplete() {
            DatabaseManager.getInstance().setPreference(DatabaseManager.PREF_LAST_UPDATED, String.valueOf(System.currentTimeMillis()));
            progDlg.dismiss();
        }
    }

    private class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        @Override
        public View getCalloutBalloon(MapPOIItem mapPOIItem) {
            return null;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
            return null;
        }
    }

}
