package com.project.speedometer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBase {

    FirebaseDatabase database;
    DatabaseReference reference;

    public DatabaseReference getReference(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        return reference;
    }
}
