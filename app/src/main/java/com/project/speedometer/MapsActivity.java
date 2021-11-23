package com.project.speedometer;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.speedometer.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    DatabaseReference reference = new FireBase().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_in_night));
        getData();
    }

    private void getData() {
        // calling add value event listener method
        // for getting the values from database.
        reference.child("measures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    // take in object type measure data
                    Measures measures = messageSnapshot.getValue(Measures.class);
                    // Be sure measures are not null
                    assert measures != null;
                    // Take latitude, longitude and create new point
                    LatLng point = new LatLng(measures.latitude , measures.longitude);
                    // Create values type String and BitmapDescriptor
                    BitmapDescriptor color;
                    String title;
                    // If acceleration is positive -> acceleration (color Green)
                    if (measures.acceleration > 0){
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        title = "Acceleration: ";
                    // Else acceleration negative -> declaration (color Red)
                    }else{
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        title = "Declaration: ";
                    }
                    // Add mark to map using specs we already set up
                    mMap.addMarker(new MarkerOptions()
                            .icon(color) // Color of point
                            .position(point) // Location of point
                            .title(title + String.format("%.2f", measures.acceleration)) // Title of point
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point)); // Locate camera onto it

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MapsActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }

        });
    }

}