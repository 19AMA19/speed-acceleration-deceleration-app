package com.examples.speedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    static final int REQ_LOC_CODE=23; // Gps request location code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button get_started_btn = findViewById(R.id.get_started_btn);
        get_started_btn.setOnClickListener(v -> {
            // Check if gps permission granted
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // open menu
                open_menu();
                // if is not permission granted ask to
            }else ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOC_CODE);
        });
    }

    // Method, run after when permission granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOC_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            open_menu();
        }
    }

    public void open_menu(){
        Intent intent = new Intent(this,Menu.class);
        startActivity(intent);
    }
}

// notes
// acceleration dx/dt