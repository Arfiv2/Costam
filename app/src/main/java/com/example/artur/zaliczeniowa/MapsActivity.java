package com.example.artur.zaliczeniowa;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Context context;
    Intent intent;

    private GoogleMap mMap;
    double odleglosc = 0;
    Boolean juz = false;
    float[] result = new float[1];
    List<Double> dlugosc = new ArrayList<Double>();
    List<Double> szerokosc = new ArrayList<Double>();
    long millis;
    int zamiana;
    int seconds;
    int minutes;
    int numer = 0;
    String przekaz,przekaz1,przekaz2;
    DecimalFormat REAL_FORMATTER = new DecimalFormat("0.##");

    TextView timerTextView;
    TextView kmTextView;
    long startTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable()
    {
        @Override
        public void run() {
        millis = System.currentTimeMillis() - startTime;
        seconds = (int) (millis / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;

        timerTextView.setText(String.format("%d:%02d", minutes, seconds));
        timerHandler.postDelayed(this, 500);
    }
    };

    public void kilometry(int kolejnosc)
    {
        if(dlugosc.size() > 1)
        {
            Location.distanceBetween(
                    szerokosc.get(kolejnosc-1),dlugosc.get(kolejnosc-1),
                    szerokosc.get(kolejnosc), dlugosc.get(kolejnosc), result);
            odleglosc = odleglosc + result[0];
        }
    }

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FullScreencall();

        timerTextView = (TextView) findViewById(R.id.timerTextView);
        kmTextView = (TextView) findViewById(R.id.kmTextView);

        Button b = (Button) findViewById(R.id.button1);
        b.setText("Rozpocznij trening");
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("Zakończ trening"))
                {
                    timerHandler.removeCallbacks(timerRunnable);
                    juz = false;
                    odleglosc = Math.round(odleglosc);
                    zamiana = (int)odleglosc;
                    przekaz = String.valueOf(zamiana);
                    przekaz1 = String.valueOf(minutes);
                    przekaz2 = String.valueOf(seconds);

                    context = getApplicationContext();
                    intent = new Intent(context, PoTreninguActivity.class);
                    intent.putExtra("name", przekaz);
                    intent.putExtra("minuty", przekaz1);
                    intent.putExtra("sekundy", przekaz2);
                    startActivity(intent);

                } else {
                    startTime = System.currentTimeMillis();
                    mMap.clear();
                    juz = true;
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("Zakończ trening");
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    if(juz == true)
                    {
                        dlugosc.add(longitude);
                        szerokosc.add(latitude);
                        kilometry(numer);
                        numer++;
                        kmTextView.setText(REAL_FORMATTER.format(odleglosc));
                    }
                    LatLng latLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + ",";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.2f));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
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
            });
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    if(juz == true)
                    {
                        dlugosc.add(longitude);
                        szerokosc.add(latitude);
                        kilometry(numer);
                        numer++;
                        kmTextView.setText(REAL_FORMATTER.format(odleglosc));
                    }
                    LatLng latLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + ",";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.2f));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
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
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        FullScreencall();
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }

    public void FullScreencall()
    {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19)
        {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        }
        else if(Build.VERSION.SDK_INT >= 19)
        {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}


