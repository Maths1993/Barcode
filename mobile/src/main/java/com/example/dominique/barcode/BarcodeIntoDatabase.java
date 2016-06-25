package com.example.dominique.barcode;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;

import java.util.Map;

public class BarcodeIntoDatabase {

    private Firebase database;

    public BarcodeIntoDatabase(Context context) {
        Firebase.setAndroidContext(context);
        database = new Firebase("https://torrid-heat-574.firebaseio.com/");
    }

    public void store(String code, String description, String price, String location) {
        BarcodeLayout obj = new BarcodeLayout(code, description, price, location);
        Map<String, Object> map = new ObjectMapper().convertValue(obj, Map.class);
        database.child(code).updateChildren(map);
    }

}
