package com.example.sean.golfranger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;

import java.text.DecimalFormat;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.example.sean.golfranger.R.id.map;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback {

    static String distance;
    private static final int LOCATION_REQUEST_CODE = 101;
    private boolean permissionIsGranted = false;
    private GoogleMap mMap = null;
    Location location;
    LatLng cord;
    Marker golferMarker = null, marker = null;
    CameraPosition cameraPosition = null;
    Location golferLocation, markerLocationN, markerLocationNE, markerLocationE, markerLocationSE, markerLocationS, markerLocationSW, markerLocationW, markerLocationNW, markerLocation;


    /**
     * In order to update to API 23, we need to execute runtime permissions check
     * We do this be executing the onRequestPermissionsResults, the body of requestLocationUpdates,
     * and onResume/onPause declarations
     */

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                permissionIsGranted = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionIsGranted = true;
                } else {
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), "Range Distance Requires " +
                            "Location Permissions", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        if (!permissionIsGranted) {
            requestLocationUpdates();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Criteria criteria = new Criteria();

        /**
         INSTANTIATE GOOGLE MAP OBJECT WITH VALUE FROM onMapReady callback
         */
        mMap = googleMap;

        /**
         ENABLE LOCATION MARKER
         */
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        /**
         GET USER LOCATION
         */
        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            cord = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        /**
         POSITION MAP CAMERA AT USER LOCATION
         */
        if (location != null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(cord)      // Sets the center of the map to location user
                    .zoom(17.3f)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                mMap.clear();

                //PUT MARKER AT SPOT OF CLICK
                marker = mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
            }
        });

        //TODO: SAVE MAP STATE ON SCREEN ROTATE
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (golferMarker != null & marker != null) {
                    golferMarker.remove();
                } else if (golferMarker == null & marker != null) {
                    /**
                     GET USER LOCATION
                     */
                    try {
                        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                        cord = new LatLng(location.getLatitude(), location.getLongitude());
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                    /**
                     POSITION MAP CAMERA AT USER LOCATION
                     */
                    if (location != null) {
                        cameraPosition = new CameraPosition.Builder()
                                .target(cord)      // Sets the center of the map to location user
                                .zoom(19.3f)                   // Sets the zoom
                                .build();                   // Creates a CameraPosition from the builder

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }

                if (marker != null) {
                    //Create Markers
                    golferMarker = mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                    );

                    //Instantiate Location Objects for distance calc
                    golferLocation = new Location("golferLocation");
                    golferLocation.setLatitude(golferMarker.getPosition().latitude);
                    golferLocation.setLongitude(golferMarker.getPosition().longitude);

                    markerLocation = new Location("markerLocation");
                    markerLocation.setLatitude(marker.getPosition().latitude);
                    markerLocation.setLongitude(marker.getPosition().longitude);

                    //Get DISTANCE
                    distance = String.valueOf(Math.round(golferLocation.distanceTo(markerLocation) * 1.09361));

                    DecimalFormat f = new DecimalFormat("##.000000");

                    String[] cords = new String[]
                            {f.format(golferMarker.getPosition().latitude)
                                    , f.format(golferMarker.getPosition().longitude)
                                    , f.format(marker.getPosition().latitude)
                                    , f.format(marker.getPosition().longitude)};

                    //
                    //ADD ASYNC TASK EXECUTE FOR ELEVATION

                    new ElevationTask().execute(cords);

                }
            }
        });
    }

    class ElevationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... locs) {


            RequestElevationData elevations = new RequestElevationData(getApplicationContext());
            return elevations.getElevationData(locs);
        }


        @Override
        protected void onPostExecute(String jsonElevationData) {
            //Write Distances to marker titles
            ElevationDataParser dataParser = new ElevationDataParser();

            String elevationDiff = String.valueOf(Math.round((dataParser.getMarkerElevation(jsonElevationData)
                    - dataParser.getGolferElevation(jsonElevationData)) * 3.28083));

            golferMarker.setTitle("D:" + distance + "yrd  E:" + elevationDiff + "ft");
            golferMarker.showInfoWindow();
            }
    }


    /**
     * @param lat0      in latitude
     * @param lon0      in longitude
     * @param direction in degrees clockwise from north
     * @param dist      in meters
     * @return double array of new latlng
     */
    Double[] getCoordinate(double lat0, double lon0, double direction, double dist) {
        final Double[] latlng = new Double[2];

        Double d = dist / 6378137;
        Double brng = Math.toRadians(direction);
        Double lat1 = Math.toRadians(lat0);
        Double lon1 = Math.toRadians(lon0);

        Double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(brng));
        Double a = Math.atan2(Math.sin(brng) * Math.sin(d) * Math.cos(lat1), Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));
        Double lon2 = lon1 + a;

        latlng[0] = Math.toDegrees(lat2);
        latlng[1] = Math.toDegrees(lon2);
        return latlng;
    }
}
