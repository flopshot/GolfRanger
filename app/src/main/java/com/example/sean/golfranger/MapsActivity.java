package com.example.sean.golfranger;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    Location location;
    LatLng cord;
    Marker marker = null;
    CameraPosition cameraPosition = null;
    Location golferLocation, markerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

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

        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            location = null;
            cord = new LatLng(-151, 38);
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition cameraPosition = mMap.getCameraPosition();
                marker.setPosition(cameraPosition.target);

                try {
                    location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                } catch (SecurityException e) {
                    location = null;
                    cord = new LatLng(-151, 38);
                }

                golferLocation = new Location("golferLocation");
                golferLocation.setLatitude(location.getLatitude());
                golferLocation.setLongitude(location.getLongitude());

                markerLocation = new Location("markerLocation");
                markerLocation.setLatitude(marker.getPosition().latitude);
                markerLocation.setLongitude(marker.getPosition().longitude);

                String distance = String.valueOf(Math.round(golferLocation.distanceTo(markerLocation) * 1.09361));

                marker.setTitle(distance);
                marker.showInfoWindow();
            }
        });


        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            cord = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (SecurityException e) {
            location = null;
            cord = new LatLng(-151, 38);
        }

        if (location != null) {
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cord, 13));

            cameraPosition = new CameraPosition.Builder()
                    .target(cord)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        marker = mMap.addMarker(new MarkerOptions()
                .position(cord)
        );
    }
}
