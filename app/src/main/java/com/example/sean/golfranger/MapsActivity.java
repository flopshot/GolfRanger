package com.example.sean.golfranger;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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

import static com.example.sean.golfranger.R.id.map;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 101;
    private boolean permissionIsGranted = false;
    private GoogleMap mMap = null;
    Location location;
    LatLng cord;
    Marker markerN, markerNE, markerE, markerSE, markerS,
            markerSW, markerW, markerNW, golferMarker, marker;
    CameraPosition cameraPosition = null;
    Location golferLocation, markerLocationN, markerLocationNE, markerLocationE, markerLocationSE, markerLocationS, markerLocationSW, markerLocationW, markerLocationNW, markerLocation;


    /**
     * In order to update to API 23, we need to execute runtime permissions check
     * We do this be executing the onRequestPermissionsResults, the body of requestLocationUpdates,
     * and onResume/onPause declarations
     */

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
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
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        //TODO: SAVE MAP STATE ON SCREEN ROTATE
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                mMap.clear();

                //PUT MARKER AT SPOT OF CLICK
                marker = mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );

                //GET USER LOCATION
                try {
                    location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                    cord = new LatLng(location.getLatitude(), location.getLongitude());
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                //MOVE CAMERA TO USER LOCATION
                cameraPosition = new CameraPosition.Builder()
                        .target(cord)      // Sets the center of the map to location user
                        .zoom(20)          // Sets the zoom
                        .build();          // Creates a CameraPosition from the builder

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                //GET GOLFER LAT LONG
                double golferLat = location.getLatitude();
                double golferLng = location.getLongitude();

                //GET GOLFER VICINITY COORDINATES WITHIN 6 YARD RADIUS
                // TODO: RADIUS TO BE VARIABLE IN FUTURE RELEASE
                // TODO: CREATE LOOP CONSTRUCT TO ITERATE THROUGH MARKER CREATION
                double[] cordN = getCoordinate(golferLat, golferLng, 0, 6.0 / 1.09361);
                double[] cordNE = getCoordinate(golferLat, golferLng, 45, 6.0 / 1.09361);
                double[] cordE = getCoordinate(golferLat, golferLng, 90, 6.0 / 1.09361);
                double[] cordSE = getCoordinate(golferLat, golferLng, 135, 6.0 / 1.09361);
                double[] cordS = getCoordinate(golferLat, golferLng, 180, 6.0 / 1.09361);
                double[] cordSW = getCoordinate(golferLat, golferLng, 225, 6.0 / 1.09361);
                double[] cordW = getCoordinate(golferLat, golferLng, 270, 6.0 / 1.09361);
                double[] cordNW = getCoordinate(golferLat, golferLng, 315, 6.0 / 1.09361);

                //Create Markers
                golferMarker = mMap.addMarker(new MarkerOptions()
                        .position(cord)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerN = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordN[0], cordN[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerNE = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordNE[0], cordNE[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerE = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordE[0], cordE[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerSE = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordSE[0], cordSE[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerS = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordS[0], cordS[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerSW = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordSW[0], cordSW[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerW = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordW[0], cordW[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                markerNW = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cordNW[0], cordNW[1]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
                );

                //Instantiate Location Objects for distance calc
                golferLocation = new Location("golferLocation");
                golferLocation.setLatitude(location.getLatitude());
                golferLocation.setLongitude(location.getLongitude());

                markerLocation = new Location("markerLocation");
                markerLocation.setLatitude(marker.getPosition().latitude);
                markerLocation.setLongitude(marker.getPosition().longitude);

                markerLocationN = new Location("markerLocationN");
                markerLocationN.setLatitude(markerN.getPosition().latitude);
                markerLocationN.setLongitude(markerN.getPosition().longitude);

                markerLocationNE = new Location("markerLocationN");
                markerLocationNE.setLatitude(markerNE.getPosition().latitude);
                markerLocationNE.setLongitude(markerNE.getPosition().longitude);

                markerLocationE = new Location("markerLocationN");
                markerLocationE.setLatitude(markerE.getPosition().latitude);
                markerLocationE.setLongitude(markerE.getPosition().longitude);

                markerLocationSE = new Location("markerLocationN");
                markerLocationSE.setLatitude(markerSE.getPosition().latitude);
                markerLocationSE.setLongitude(markerSE.getPosition().longitude);

                markerLocationS = new Location("markerLocationN");
                markerLocationS.setLatitude(markerS.getPosition().latitude);
                markerLocationS.setLongitude(markerS.getPosition().longitude);

                markerLocationSW = new Location("markerLocationN");
                markerLocationSW.setLatitude(markerSW.getPosition().latitude);
                markerLocationSW.setLongitude(markerSW.getPosition().longitude);

                markerLocationW = new Location("markerLocationN");
                markerLocationW.setLatitude(markerW.getPosition().latitude);
                markerLocationW.setLongitude(markerW.getPosition().longitude);

                markerLocationNW = new Location("markerLocationN");
                markerLocationNW.setLatitude(markerNW.getPosition().latitude);
                markerLocationNW.setLongitude(markerNW.getPosition().longitude);

                //Calculate Distance
                String distance = String.valueOf(Math.round(golferLocation.distanceTo(markerLocation) * 1.09361));
                String distanceN = String.valueOf(Math.round(markerLocationN.distanceTo(markerLocation) * 1.09361));
                String distanceNE = String.valueOf(Math.round(markerLocationNE.distanceTo(markerLocation) * 1.09361));
                String distanceE = String.valueOf(Math.round(markerLocationE.distanceTo(markerLocation) * 1.09361));
                String distanceSE = String.valueOf(Math.round(markerLocationSE.distanceTo(markerLocation) * 1.09361));
                String distanceS = String.valueOf(Math.round(markerLocationS.distanceTo(markerLocation) * 1.09361));
                String distanceSW = String.valueOf(Math.round(markerLocationSW.distanceTo(markerLocation) * 1.09361));
                String distanceW = String.valueOf(Math.round(markerLocationW.distanceTo(markerLocation) * 1.09361));
                String distanceNW = String.valueOf(Math.round(markerLocationNW.distanceTo(markerLocation) * 1.09361));

                //Write Distances to marker titles
                marker.setTitle(distance);

                markerN.setTitle(distanceN);

                markerNE.setTitle(distanceNE);

                markerE.setTitle(distanceE);
                markerE.showInfoWindow();

                markerSE.setTitle(distanceSE);

                markerS.setTitle(distanceS);
                markerS.showInfoWindow();

                markerSW.setTitle(distanceSW);

                markerW.setTitle(distanceW);

                markerNW.setTitle(distanceNW);

                golferMarker.setTitle(distance);
                golferMarker.showInfoWindow();

            }
        });
    }

    /**
     * @param lat0      in latitude
     * @param lon0      in longitude
     * @param direction in degrees clockwise from north
     * @param dist      in meters
     * @return double array of new latlng
     */
    double[] getCoordinate(double lat0, double lon0, double direction, double dist) {
        final double[] latlng = new double[2];

        double d = dist / 6378137;
        double brng = Math.toRadians(direction);
        double lat1 = Math.toRadians(lat0);
        double lon1 = Math.toRadians(lon0);

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(brng));
        double a = Math.atan2(Math.sin(brng) * Math.sin(d) * Math.cos(lat1), Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));
        double lon2 = lon1 + a;

        latlng[0] = Math.toDegrees(lat2);
        latlng[1] = Math.toDegrees(lon2);
        return latlng;
    }
}
