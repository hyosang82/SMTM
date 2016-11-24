package kr.hyosang.smtm.android;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

public class MainActivity extends Activity {
    private MapView mapView = null;


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

        (new DatabaseUpdater()).execute();



        //mapView.setMapCenterPoint(pt, false);
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

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("atm");

            MapPoint.GeoCoordinate leftBottom = bounds.bottomLeft.getMapPointGeoCoord();
            MapPoint.GeoCoordinate rightTop = bounds.topRight.getMapPointGeoCoord();
            int xmin = (int)(leftBottom.longitude * 100_000);
            int xmax = (int)(rightTop.longitude * 100_000);
            int ymin = (int)(leftBottom.latitude * 100_000);
            int ymax = (int)(rightTop.latitude * 100_000);

            Log.d("TEST", xmin + " ~ " + xmax);

            ref.orderByChild("xE6").startAt(xmin).endAt(xmax)
                    .orderByChild("yE6").startAt(ymin).endAt(ymax)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("TEST", "Count = " + dataSnapshot.getChildrenCount());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("TEST", "Error " + databaseError);

                        }
                    });


            /*
            Log.d("TEST", "POS: " + mapPoint);

            MapPOIItem poi = new MapPOIItem();
            poi.setItemName("Default Marker");
            poi.setTag(0);
            poi.setMapPoint(mapPoint);
            poi.setMarkerType(MapPOIItem.MarkerType.BluePin);
            poi.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mapView.addPOIItem(poi);
            */
        }
    };

}
