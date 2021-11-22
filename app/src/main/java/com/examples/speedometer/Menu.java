package com.examples.speedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Gauge;
import com.github.anastr.speedviewlib.ProgressiveGauge;
import com.github.anastr.speedviewlib.SpeedView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;

public class Menu extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    FirebaseDatabase database;
    DatabaseReference referenceMeter;
    DatabaseReference referenceId;
    TextView location_txt,accel_txt;
    ProgressiveGauge speedometer;
    boolean acceleration_flag = false;
    int prev_speed,start_speed,end_speed,last_speed = 0;
    long start_time,finish_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        database = FirebaseDatabase.getInstance();
        referenceMeter = database.getReference("meter");
        referenceId = database.getReference("id");
        startLocationUpdates();
        location_txt = findViewById(R.id.location_txt);
        accel_txt = findViewById(R.id.accel_txt);
        speedometer = findViewById(R.id.speedView);

    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.finish();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0,
                Menu.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(Menu.this);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        int speed = 0;

        if (location.hasSpeed()) {
            speed=(int) ((location.getSpeed()*3600)/1000);
        }

        location_txt.setText("Latitude:" + location.getLatitude() + "\nLongitude:" + location.getLongitude());
        speedometer.speedTo(speed);

        int tmp_speed = (speed - prev_speed);
        if (tmp_speed == 0 && acceleration_flag) {
            acceleration_flag = false;
            finish_time = System.currentTimeMillis();
            double d_time = (double) (finish_time - start_time) / 1000;
            double acceleration = last_speed / d_time;

            if (acceleration >= 25 || acceleration <= -25){ // ± 50khm in 2 sec acceleration
                accel_txt.setText("acceleraton: " +  String.format("%.2f", acceleration) + "Khm/h*s");
            }

        } else if (tmp_speed !=0 && !acceleration_flag) {
            start_time = System.currentTimeMillis();
            acceleration_flag = true;
            last_speed = tmp_speed;
        }


        prev_speed = speed;
        paint_meter(speed);

    }

    private void paint_meter(int speed) {
        if (speed >= 80 && speed <120) {
            speedometer.setSpeedTextColor(ContextCompat.getColor(this, R.color.orange));
            speedometer.setUnitTextColor(ContextCompat.getColor(this, R.color.orange));
            speedometer.setSpeedometerColor(ContextCompat.getColor(this, R.color.orange));
        } else if (speed >= 120){
            speedometer.setSpeedTextColor(ContextCompat.getColor(this, R.color.red));
            speedometer.setUnitTextColor(ContextCompat.getColor(this, R.color.red));
            speedometer.setSpeedometerColor(ContextCompat.getColor(this, R.color.red));
        } else {
            speedometer.setSpeedTextColor(ContextCompat.getColor(this, R.color.green));
            speedometer.setUnitTextColor(ContextCompat.getColor(this, R.color.green));
            speedometer.setSpeedometerColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("Latitude","disable");
    }

}

// notes
// acceleration (speed_teliko - speed_arxiko) / dt