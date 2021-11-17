package com.examples.speedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Menu extends AppCompatActivity {
    LocationManager locationManager;
    FirebaseDatabase database;
    DatabaseReference referenceMeter;
    DatabaseReference referenceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        database = FirebaseDatabase.getInstance();
        referenceMeter = database.getReference("meter");
        referenceId = database.getReference("id");
        startLocationUpdates();
        TextView speed_txt = findViewById(R.id.speed_txt);
//        speed_txt.setText(String.valueOf(location.getSpeed()));

    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.finish();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0,
                (LocationListener) Menu.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates((LocationListener) Menu.this);
    }


}