package com.example.gpsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView Latitude, Longitude, address, distance, elapsedTime;
    double latitude, longitude;
    Location destination;
    float tempDist;
    double finalDist;
    LocationManager LocationManager;
    LocListener locListener = new LocListener();
    SystemClock clock;
    Long  startTime = (clock.elapsedRealtime())/1000, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Latitude = findViewById(R.id.id_lat);
        Longitude = findViewById(R.id.id_long);
        address = findViewById(R.id.id_address);
        distance = findViewById(R.id.id_distance);
        elapsedTime = findViewById(R.id.id_time);


        LocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }

        LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locListener);
    }

    public class LocListener implements LocationListener
    {
         @Override
            public void onLocationChanged(@NonNull Location location)
            {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                if(destination == null)
                {
                    destination = location;
                }

                tempDist += location.distanceTo(destination);

                finalDist = MeterstoMiles(tempDist);
                double finalTotalDistance = Math.round(finalDist*1000.0)/1000.0;

                destination = location;

                Latitude.setText("Latitude: "+latitude);
                Longitude.setText("Longitude: "+longitude);
                address.setText("Address: "+getAddress(latitude, longitude));
                distance.setText("Total Distance Traveled: "+finalTotalDistance+" miles");

                currentTime = (clock.elapsedRealtime())/1000;
                elapsedTime.setText(""+(currentTime-startTime)+" seconds");
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

        }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

        }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public String getAddress(double latitude, double longitude)
        {
            String addressString = "";
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                List <Address> AddressArray = geocoder.getFromLocation(latitude, longitude, 1);
                if (AddressArray != null) {
                    Address returnedAddress = AddressArray.get(0);
                    addressString = returnedAddress.getAddressLine(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return addressString;
        }

        public double MeterstoMiles (double meters)
        {
            double miles = meters/1609.344;
            return miles;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locListener);
        }
    }

    public void requestLocationUpdates (String provider, long minTimeMs, float minDistanceM, LocationListener LocationListener)
    {
        provider = LocationManager.GPS_PROVIDER;
    }
}