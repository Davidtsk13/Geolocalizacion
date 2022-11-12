package com.amto_dev.uso_de_geolocalizacion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnObtenerUbicacion, btnMaps, SendUbication, btn2;
    TextView tvLatitud, tvLongitud, tvDireccion;
    public static final int CODIGO_UBICACION = 100;
    private static final int REQUEST_CODIGO_CAMARA=200;


    Location location;
    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMaps = findViewById(R.id.btnMaps);
        btnObtenerUbicacion = findViewById(R.id.btnUbicacion);
        tvLatitud = findViewById(R.id.tvLatitud);
        tvLongitud = findViewById(R.id.tvLongitud);
        tvDireccion = findViewById(R.id.tvDireccion);




        btnObtenerUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerUbicacion();
            }

        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });

    }


    private void obtenerUbicacion() {
        verificarPermisosUbicacion();
    }

    private void verificarPermisosUbicacion() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, }, 100);
        }else{
            iniciarUbicacion();
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CODIGO_UBICACION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                iniciarUbicacion();
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void iniciarUbicacion() {
        LocationManager objGestorUbicacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Localizacion ubicador = new Localizacion();
        ubicador.setMainActivity(this);
        final boolean gpsEnabled = objGestorUbicacion.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, }, CODIGO_UBICACION);
            return;
        }

        objGestorUbicacion.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,(LocationListener) ubicador);
        objGestorUbicacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, (LocationListener) ubicador);


        Toast.makeText(MainActivity.this, "Localizacion Inicializada", Toast.LENGTH_SHORT).show();
        tvLatitud.setText("");
        tvLongitud.setText("");
        tvDireccion.setText("");
    }


    public class Localizacion implements LocationListener{
        MainActivity mainActivity;
        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(@NonNull Location loc) {
            tvLatitud.setText(String.valueOf(loc.getLatitude()));
            tvLongitud.setText(String.valueOf(loc.getLongitude()));
            this.mainActivity.obtenerUbicacion(loc);

            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

        }

        @Override
        public void onLocationChanged(@NonNull List<Location> locations) {
            LocationListener.super.onLocationChanged(locations);
        }

        @Override
        public void onFlushComplete(int requestCode) {
            LocationListener.super.onFlushComplete(requestCode);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status){
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");

            }
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

            Log.i("EstatusGPS", "GPS Activado");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

            Log.i("EstatusGPS", "GPS Desactivado");
        }
    }




    private void obtenerUbicacion(Location ubicacion) {
        if (ubicacion.getLatitude() != 0.0 && ubicacion.getLongitude() != 0.0){
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        ubicacion.getLatitude(), ubicacion.getLongitude(), 1
                );
                if (!list.isEmpty()){
                    Address DirCalle = list.get(0);
                    tvDireccion.setText(DirCalle.getAddressLine(0));
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

