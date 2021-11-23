package com.examples.speedometer;

import static java.lang.Math.round;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class Menu extends AppCompatActivity implements LocationListener {
    FirebaseDatabase database;
    DatabaseReference reference;
    LocationManager locationManager;
    TextView location_txt,accel_txt;
    ProgressiveGauge speedometer;
    boolean acceleration_flag = false;
    int prev_speed,start_speed = 0;
    long start_time,finish_time;

    List<Integer> last_speed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        setContentView(R.layout.activity_menu);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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

        if (location.hasSpeed()) { // if vehicle moving
            speed=(int) ((location.getSpeed()*3600)/1000); // take and convert speed to khm/h
        }
        double latitude = location.getLatitude(); // get latitude
        double longitude = location.getLongitude(); // get longitude
        location_txt.setText("Latitude:" + latitude  + "\nLongitude:" + longitude); // display using location_txt the longitude,latitude
        speedometer.speedTo(speed); // display current speed using speedview

        int tmp_speed = (speed - prev_speed); // calculate speed difference between current state and previews

        if (tmp_speed == 0 && acceleration_flag) { // if speed difference = 0 and flag = true then acceleration from something goes to 0
            acceleration_flag = false; // change flag status
            finish_time = System.currentTimeMillis(); // take finishing acceleration time
            double d_time = (double) (finish_time - start_time) / 1000; // calculate duration and convert it from ms to sec
            double acceleration = findAverage(last_speed) / d_time; // calculate acceleration using khm/h and sec
            last_speed.clear(); // clear list

            if (acceleration >= 25 || acceleration <= -25){ // ± 50khm in 2 sec acceleration
                accel_txt.setText("acceleraton: " +  String.format("%.2f", acceleration) + "Khm/h*s");
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                writeNewMeasure(acceleration,currentDate,currentTime,latitude,longitude,start_speed);
            }

        } else if (tmp_speed !=0) { // if speed difference start not equal 0
            if (!acceleration_flag){ // if is first time (flag = false)
                start_speed = prev_speed; // save speed when acceleration starts
                start_time = System.currentTimeMillis(); // save starting time
            }
            acceleration_flag = true; // change flag into true
            last_speed.add(tmp_speed); // add this state with previews difference speed to list
        }


        prev_speed = speed; // re-sign previews speed with current speed to have it in next state
        paint_meter(speed); // color speedview

    }

    public void writeNewMeasure(double acceleration, String date, String time, double latitude, double longitude, int start_speed) {
        Measures measures = new Measures(UUID.randomUUID().toString().replace("-", ""),acceleration,date,time,latitude,longitude,start_speed);
        reference.child("measures").push().setValue(measures);
    }

    private static int findAverage(List<Integer> nums) {
        // sum all the elements and divide by the amount of elements
        int total = 0;

        for(int i = 0; i<nums.size(); i++)
            total += nums.get(i);

        return round(total / nums.size());
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