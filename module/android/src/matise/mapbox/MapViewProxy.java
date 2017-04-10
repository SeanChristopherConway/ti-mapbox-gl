/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package matise.mapbox;
//java imports
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//android imports
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.app.Fragment;
//mapbox imports
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.*;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
//titanium imports
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.Log;
import org.appcelerator.titanium.util.TiConfig;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIFragment;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.io.TitaniumBlob;
import org.appcelerator.titanium.TiBlob;

// fragment test

@Kroll.proxy(creatableInModule = MapboxModule.class)
public class MapViewProxy extends TiViewProxy {
    // Standard Debugging variables
    private static final String TAG = "MapViewProxy";
    private static final boolean DBG = TiConfig.LOGD;

    private MapboxMap mapboxMap;
    private static final String LCAT = "MapboxModule";
    public static final String PROPERTY_LATITUDE = "lat";
    public static final String PROPERTY_LONGITUDE = "lng";
    private static final String PROPERTY_ANNOTATION = "annotation";
    private static final String PROPERTY_ANNOTATIONS = "annotations";
    private static final String PROPERTY_USER_LOCATION = "userLocation";
    private static final String PROPERTY_ANNOTATION_TITLE = "title";
    private static final String PROPERTY_ANNOTATION_DESCRIPTION = "description";
    private static final String PROPERTY_ZOOM = "zoom";
    private static final String PROPERTY_MAP = "map";
    private static final String PROPERTY_SIZE = "size";
    private static final String PROPERTY_COLOR = "color";
    private static final String PROPERTY_COORDS = "points";
    private static final String PROPERTY_IMAGEURL = "image";
    private static final String PROPERTY_MARKER_ID = "markerID";
    private static final String PROPERTY_BEARING = "bearing";
    private static final String PROPERTY_TILT = "tilt";

    private static final int MSG_FIRST_ID = TiViewProxy.MSG_LAST_ID + 1;
    private static final int MSG_SET_REGION = MSG_FIRST_ID + 500;
    private static final int MSG_SET_ANNOTATIONS = MSG_FIRST_ID + 501;
    private static final int MSG_SET_USER_LOCATION = MSG_FIRST_ID + 502;
    private static final int MSG_SET_USER_ZOOM = MSG_FIRST_ID + 503;
    private static final int MSG_SET_USER_MAP = MSG_FIRST_ID + 504;
    private static final int MSG_SET_ANNOTATION = MSG_FIRST_ID + 505;

    // Variables that are needed during setup
    private String styleUrl = Style.SATELLITE;
    private float lat = 0;
    private float lng = 0;
    private int zoom = 13;

    private HashMap<String, Marker> markers = new HashMap<String, Marker>();

    private class MapViewFragment extends TiUIFragment {
        public MapViewFragment(final TiViewProxy proxy, Activity activity) {
            super(proxy, activity);
        }

        @Override
        protected Fragment createFragment() {
            // Get access token from AndroidManifest

            // Set options
            MapboxAccountManager.start(proxy.getActivity(), "{enter mapbox api key here}");
            MapboxMapOptions options = new MapboxMapOptions();
            //options.accessToken(accessToken);
            options.styleUrl(styleUrl);

            if (lat != 0) {
                options.camera(new CameraPosition.Builder()
                               .target(new LatLng(lat, lng))
                               .zoom(zoom)
                               .build());
            }

            // Create MapFragment
            SupportMapFragment map = SupportMapFragment.newInstance(options);

            if (map instanceof SupportMapFragment) {
                map.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap _mapboxMap) {
                        mapboxMap = _mapboxMap;

                        // MapboxModule.getInstance().map = _mapboxMap;

                        // Customize map with markers, polylines, etc.

                        KrollDict props = new KrollDict();
                        proxy.fireEvent("mapReady", props);

                        mapboxMap.setMyLocationEnabled(true);
                        mapboxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                        //mapboxMap.getTrackingSettings().setDismissAllTrackingOnGesture(false);
                        mapboxMap.getMyLocation();

                        mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng point) {

                                // When the user clicks on the map, we want to animate the marker to that
                                // location.
                                //final PointF pointF = mapboxMap.getProjection().toScreenLocation(point);
                                //List<Feature> renderedFeatures = mapboxMap.queryRenderedFeatures(pointF,"sites");


                                try {
                                    customCallBack(point);
                                } catch (Error err) {
                                    System.out.println("Error on tap map is: "
                                                       + err.toString());
                                }


                            }
                        });

                        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                // do nothing
                                for (Map.Entry<String,Marker> entry : markers.entrySet()) {
                                    if(entry.getValue()==marker){
                                        try {
                                            customCallBack(marker.getPosition(),entry.getKey());
                                        } catch (Error err) {
                                            System.out.println("Error on tap marker is: "
                                                               + err.toString());
                                        }
                                    }
                                }

                                return true;
                            }
                        });

                    }
                });
            }

            return map;
        }


        @Override
        public void processProperties(KrollDict options) {
            super.processProperties(options);

            Log.e(TAG, "***** processing!");

            if (options.containsKey("styleUrl")) {
                //styleUrl = options.get("styleUrl");
                // Log.e(TAG, "***** styleUrl!");
            }

            if (options.containsKey(PROPERTY_ANNOTATIONS)) {
                Object[] annotations = (Object[]) options.get(PROPERTY_ANNOTATIONS);
                this.setAnnotations(annotations);
                //  notifyOfAnnotationsChange(annotations);
            }

            if (options.containsKey(PROPERTY_ANNOTATION)) {
                Object annotation = (Object) options.get(PROPERTY_ANNOTATION);
                this.addAnnotation(annotation);
                //notifyOfAnnotationChange(annotation);
            }

            /*  if (options.containsKey(PROPERTY_ANNOTATION)) {
             Object[] annotation = (Object[]) options.get(PROPERTY_ANNOTATION);
             this.removeAnnotation(annotation);
             //notifyOfAnnotationsChange(annotation);
             }*/

        }

        public void addAnnotation(final Object annotation) {


            HashMap<String, Object> theAnnotation = (HashMap<String, Object>) annotation;
            float _lat = 0;
            float _lng = 0;
            String annotationTitle = "";
            String annotationDescription = "";
            String imageUrl = "";
            String id = null;

            if (theAnnotation.containsKey(PROPERTY_MARKER_ID)) {
                id = (TiConvert.toString(theAnnotation, PROPERTY_MARKER_ID));
            }

            if (theAnnotation.containsKey(PROPERTY_LATITUDE)) {
                _lat = (TiConvert.toFloat(theAnnotation, PROPERTY_LATITUDE));
            }
            if (theAnnotation.containsKey(PROPERTY_LONGITUDE)) {
                _lng = (TiConvert.toFloat(theAnnotation,
                                          PROPERTY_LONGITUDE));
            }
            if (theAnnotation.containsKey(PROPERTY_ANNOTATION_TITLE)) {
                annotationTitle = TiConvert.toString(theAnnotation,
                                                     PROPERTY_ANNOTATION_TITLE);
            }

            if (theAnnotation.containsKey(PROPERTY_ANNOTATION_DESCRIPTION)) {
                annotationDescription = TiConvert.toString(theAnnotation,
                                                           PROPERTY_ANNOTATION_DESCRIPTION);
            }
            if (theAnnotation.containsKey(PROPERTY_IMAGEURL)) {
                imageUrl = TiConvert.toString(theAnnotation, PROPERTY_IMAGEURL);

            }
            LatLng latlong = new LatLng(_lat, _lng);


            Bitmap bitmap = null;
            try {

                if (imageUrl != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeResource(proxy.getActivity()
                                                          .getResources(), TiRHelper
                                                          .getApplicationResource("drawable." + imageUrl),
                                                          options);

                }

            } catch (ResourceNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }



            Icon icon = null;

            if (bitmap != null) {
                Drawable d = new BitmapDrawable(proxy.getActivity()
                                                .getApplicationContext().getResources(), bitmap);
                IconFactory iconFactory = IconFactory.getInstance(proxy.getActivity());
                icon = iconFactory.fromDrawable(d);

            }



            if (mapboxMap != null) {
                Marker marker = mapboxMap.addMarker(new MarkerOptions()
                                                    .position(latlong)
                                                    .title(annotationTitle)
                                                    .snippet(annotationDescription)
                                                    .icon(icon)

                                                    );

                markers.put(id, marker);
            }

        }


        public void setAnnotations(final Object[] annotations) {

            for (Object anAnnotation : annotations) {
                if (!(anAnnotation instanceof HashMap)) {
                    Log.e(LCAT,
                          "An object in the array parameter passed to setAnnotation is not an dictionary.");
                    continue;
                }

                HashMap<String, Object> theAnnotation = (HashMap<String, Object>) anAnnotation;
                float _lat = 0;
                float _lng = 0;
                String annotationTitle = "";
                String annotationDescription = "";
                String imageUrl = "";
                String id = null;

                if (theAnnotation.containsKey(PROPERTY_MARKER_ID)) {
                    id = (TiConvert.toString(theAnnotation, PROPERTY_MARKER_ID));
                }

                if (theAnnotation.containsKey(PROPERTY_LATITUDE)) {
                    _lat = (TiConvert.toFloat(theAnnotation, PROPERTY_LATITUDE));
                }
                if (theAnnotation.containsKey(PROPERTY_LONGITUDE)) {
                    _lng = (TiConvert.toFloat(theAnnotation,
                                              PROPERTY_LONGITUDE));
                }
                if (theAnnotation.containsKey(PROPERTY_ANNOTATION_TITLE)) {
                    annotationTitle = TiConvert.toString(theAnnotation,
                                                         PROPERTY_ANNOTATION_TITLE);
                }

                if (theAnnotation.containsKey(PROPERTY_ANNOTATION_DESCRIPTION)) {
                    annotationDescription = TiConvert.toString(theAnnotation,
                                                               PROPERTY_ANNOTATION_DESCRIPTION);
                }
                if (theAnnotation.containsKey(PROPERTY_IMAGEURL)) {
                    imageUrl = TiConvert.toString(theAnnotation, PROPERTY_IMAGEURL);

                }
                LatLng latlong = new LatLng(_lat, _lng);


                Bitmap bitmap = null;
                try {

                    if (imageUrl != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        bitmap = BitmapFactory.decodeResource(proxy.getActivity()
                                                              .getResources(), TiRHelper
                                                              .getApplicationResource("drawable." + imageUrl),
                                                              options);

                    }

                } catch (ResourceNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


                Icon icon = null;

                if (bitmap != null) {
                    Drawable d = new BitmapDrawable(proxy.getActivity()
                                                    .getApplicationContext().getResources(), bitmap);
                    IconFactory iconFactory = IconFactory.getInstance(proxy.getActivity());
                    icon = iconFactory.fromDrawable(d);

                }

                if (mapboxMap != null) {
                    Marker marker = mapboxMap.addMarker(new MarkerOptions()
                                                        .position(latlong)
                                                        .title(annotationTitle)
                                                        .snippet(annotationDescription)
                                                        .icon(icon)

                                                        );

                    markers.put(id, marker);
                }

            }
        }


        public void removeAnnotation(final Object annotation) {

            HashMap<String, Object> theAnnotation = (HashMap<String, Object>) annotation;

            String id = null;

            if (theAnnotation.containsKey(PROPERTY_MARKER_ID)) {
                id = TiConvert.toString(theAnnotation,
                                        PROPERTY_MARKER_ID);
            }
            Marker marker = markers.get(id);
            Log.d("In remove annotation and marker id is: ",id);
            if (mapboxMap != null && marker != null) {
                mapboxMap.removeAnnotation(marker);
                markers.remove(id);
            }

        }


        private void notifyOfAnnotationChange(Object annotation) {
            if (proxy.hasListeners("annotationsChange")) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("annotation", annotation);
                proxy.fireEvent("annotationChange", hm);
            }
        }


        private void notifyOfAnnotationsChange(Object[] annotations) {
            if (proxy.hasListeners("annotationsChange")) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("annotations", annotations);
                proxy.fireEvent("annotationsChange", hm);
            }
        }

        protected void customCallBack(LatLng point) {

            KrollDict props = new KrollDict();

            /*if (renderedFeatures.size() > 0) {
             String featureId = renderedFeatures.get(0).getId();
             //props.put("feature", featureId);
             System.out.println("id of feature is: "+featureId);
             }*/

            props.put("lat", point.getLatitude());
            props.put("lng", point.getLongitude());
            proxy.fireEvent("singleTapOnMap", props);

        }

        protected void customCallBack(LatLng point,String info) {

            KrollDict props = new KrollDict();
            props.put("lat", point.getLatitude());
            props.put("lng", point.getLongitude());
            props.put("site_info", info);
            proxy.fireEvent("tapOnAnnotation", props);
        }
    }

    // Constructor
    public MapViewProxy() {
        super();
    }

    @Override
    public TiUIView createView(Activity activity) {

        MapViewFragment view = new MapViewFragment(this, activity);
        view.getLayoutParams().autoFillsHeight = true;
        view.getLayoutParams().autoFillsWidth = true;
        return view;
    }

    // Handle creation options
    @Override
    public void handleCreationDict(KrollDict options) {
        Log.e(TAG, "***** handleCreationDict");

        super.handleCreationDict(options);

        if (options.containsKey("styleUrl")) {
            styleUrl = TiConvert.toString(options.get("styleUrl"));
        }

        if (options.containsKey("lat") && options.containsKey("lng")) {
            lat = TiConvert.toFloat(options.get("lat"));
            lng = TiConvert.toFloat(options.get("lng"));
        }

        if (options.containsKey("zoom")) {
            zoom = TiConvert.toInt(options.get("zoom"));
        }
    }


    public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
        // This method is one of the initializers for the proxy class. The
        // arguments
        // for the create call are passed as an array of objects. If your proxy
        // simply needs to handle a single KrollDict argument, use
        // handleCreationDict.
        // The superclass method calls the handleCreationDict if the first
        // argument
        // to the create method is a dictionary object.

        Log.d(LCAT, "VIEWPROXY LIFECYCLE EVENT] handleCreationArgs ");

        for (int i = 0; i < args.length; i++) {
            Log.d(LCAT, "VIEWPROXY LIFECYCLE EVENT] args[" + i + "] " + args[i]);
        }

        super.handleCreationArgs(createdInModule, args);
    }


    @Override
    public boolean handleMessage(Message msg) {
        AsyncResult result = null;
        switch (msg.what) {

            case MSG_SET_ANNOTATIONS: {
                result = (AsyncResult) msg.obj;
                handleSetAnnotations((Object[]) result.getArg());
                result.setResult(null);
                return true;
            }

            default: {
                return super.handleMessage(msg);
            }
        }
    }


    @Kroll.setProperty(retain = false)
    @Kroll.method
    public void setAnnotations(final Object annotations) {
        Log.i(LCAT, "[VIEWPROXY LIFECYCLE EVENT] Property Set: setAnnotations "
              + annotations);
        if (!(annotations instanceof Object[])) {
            Log.e(LCAT,
                  "Object parameter passed to setAnnotation is not an array.");
            return;
        }

        Object[] annotationsArray = (Object[]) annotations;
        if (TiApplication.isUIThread()) {
            handleSetAnnotations(annotationsArray);
        } else {
            TiMessenger.sendBlockingMainMessage(
                                                getMainHandler().obtainMessage(MSG_SET_ANNOTATIONS),
                                                annotationsArray);
        }

        setProperty("annotations", annotations, true);
    }

    public void handleSetAnnotations(Object[] annotations) {


        MapViewFragment mapView = (MapViewFragment) view;
        if (!(mapView instanceof MapViewFragment)) {
            Log.e(LCAT,
                  "MapView View Object hasn't been instantiated yet; Unable to set annotations.");
            return;
        }
        mapView.setAnnotations(annotations);


    }

    // Methods
    @Kroll.method
    public void addAnnotation(final Object json) {

        MapViewFragment mapView = (MapViewFragment) view;
        if (!(mapView instanceof MapViewFragment)) {
            Log.e(LCAT,
                  "MapView View Object hasn't been instantiated yet; Unable to set annotations.");
            return;
        }
        mapView.addAnnotation(json);

    }

    // Methods
    @Kroll.method
    public void removeAnnotation(final Object json) {

        MapViewFragment mapView = (MapViewFragment) view;
        if (!(mapView instanceof MapViewFragment)) {
            Log.e(LCAT,
                  "MapView View Object hasn't been instantiated yet; Unable to set annotations.");
            return;
        }
        mapView.removeAnnotation(json);

    }




    @Kroll.method
    public void animateCameraTo(final Object json){

        HashMap<String, Object> cameraOpts = (HashMap<String, Object>) json;
        float _lat = 0;
        float _lng = 0;
        int _zoom = 0;
        int bearing = 0;
        int tilt = 0;

        if (cameraOpts.containsKey(PROPERTY_LATITUDE)) {
            lat = (TiConvert.toFloat(cameraOpts, PROPERTY_LATITUDE));
        }
        if (cameraOpts.containsKey(PROPERTY_LONGITUDE)) {
            lng = (TiConvert.toFloat(cameraOpts,
                                     PROPERTY_LONGITUDE));
        }

        if (cameraOpts.containsKey(PROPERTY_ZOOM)) {
            _zoom = (TiConvert.toInt(cameraOpts,
                                     PROPERTY_ZOOM));
        }

        if (cameraOpts.containsKey(PROPERTY_BEARING)) {
            bearing = (TiConvert.toInt(cameraOpts,
                                       PROPERTY_BEARING));
        }

        if (cameraOpts.containsKey(PROPERTY_TILT)) {
            tilt = (TiConvert.toInt(cameraOpts,
                                    PROPERTY_TILT));
        }

        LatLng latlong = new LatLng(lat, lng);

        CameraPosition position = new CameraPosition.Builder()
        .target(latlong) // Sets the new camera position
        .zoom(_zoom) // Sets the zoom
        .bearing(bearing) // Rotate the camera
        .tilt(tilt) // Set the camera tilt
        .build(); // Creates a CameraPosition from the builder

        if (mapboxMap != null) {
            mapboxMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(position), 7000);
        }

    }

    // Methods
    @Kroll.method
    public void setStyleUrl(String url) {
        if (mapboxMap != null) {
            mapboxMap.setStyleUrl(url);
        }

    }



}
