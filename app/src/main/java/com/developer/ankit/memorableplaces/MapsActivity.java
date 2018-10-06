package com.developer.ankit.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.Exception;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager mLocationManager ;
    LocationListener mLocationListener ;

    public void centerMapOnLocation(Location location, String title){
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        if(title != "Your Location"){
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                centerMapOnLocation(location, "Your Location");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
        mMap = googleMap;
         mMap.setOnMapLongClickListener(this);


        Intent intent = getIntent();
        if(intent.getIntExtra("placeNumber",0)==0){
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerMapOnLocation(location, " Your Location");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }else{
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,mLocationListener);
                Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                centerMapOnLocation(location,"Your Location");
            }
        }else{
            Location mLocation = new Location(LocationManager.NETWORK_PROVIDER);
            mLocation.setLatitude(MainActivity.location.get(intent.getIntExtra("placeNumber",0)).latitude);
            mLocation.setLongitude(MainActivity.location.get(intent.getIntExtra("placeNumber",0)).longitude);
            centerMapOnLocation(mLocation,MainActivity.data.get(intent.getIntExtra("placeNumber",0)));

        }

    }


    @Override
    public void onMapLongClick(LatLng latLng) throws Exception{
        Geocoder mGeoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String result = "";
        

            List<Address> list = mGeoCoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(list!=null && list.size()>0){
                if(list.get(0).getSubThoroughfare()!=null)
                    result+=list.get(0).getSubThoroughfare();
                if(list.get(0).getThoroughfare()!=null)
                    result+=list.get(0).getThoroughfare();


        if(result== ""){
            SimpleDateFormat sdf = new SimpleDateFormat("HH : mm yyyy-MM-dd");
            result = sdf.format(new Date());
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(result));
        MainActivity.data.add(result);
        MainActivity.location.add(latLng);
        MainActivity.mArrayAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = this.getSharedPreferences("com.developer.ankit.memorableplaces",Context.MODE_PRIVATE);
        try {
            ArrayList<String> latitudes = new ArrayList<>();
            ArrayList<String> longitudes = new ArrayList<>();
            for(LatLng coordinates : MainActivity.location){
                latitudes.add(Double.toString(coordinates.latitude));
                longitudes.add(Double.toString(coordinates.longitude));
            }
            prefs.edit().putString("places", ObjectSerializer.serialize(MainActivity.data)).apply();
            prefs.edit().putString("lat", ObjectSerializer.serialize(latitudes)).apply();
            prefs.edit().putString("lng", ObjectSerializer.serialize(longitudes)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
