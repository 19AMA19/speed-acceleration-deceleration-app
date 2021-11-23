package com.project.speedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    static final int REQ_LOC_CODE=23; // Gps request location code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get started button clicked
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

        // Map button clicked
        Button map_btn = findViewById(R.id.map_btn);
        map_btn.setOnClickListener(v ->{
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
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
