package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class PhoneToDatabase extends Activity {

    private static final String responseName = "1";
    private static final int requestCode = 1;
    public static final int OK = 1;
    public static final int NOT_OK = 2;

    private Firebase database;
    private String barcode;
    private String description;
    private String price;
    private String location;

    private TextView text_barcode;

    public static String[] map = { "1", "2",
            "3", "4" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_to_database);
/*
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.row_layout, map);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);*/

        if(getIntent().hasExtra("barcode")) {
            // Get barcode
            barcode = getIntent().getStringExtra("barcode");

            text_barcode = (TextView) findViewById(R.id.text_barcode);
            text_barcode.setText("Scanned code: " + barcode);

            // Set up database
            Firebase.setAndroidContext(this);
            database = new Firebase("https://torrid-heat-574.firebaseio.com/");
        } else finish();

    }

    public void storeInDatabase(View view) {

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Barcode is already in database
                if(snapshot.hasChild(barcode)) {
                    Toast.makeText(getApplicationContext(), "Barc" +
                            "ode " + barcode + " already in database!" +
                            " You can edit and overwrite now", Toast.LENGTH_LONG).show();
                    Intent requestIntent = new Intent(getApplicationContext(), BarcodeSettings.class);

                    for(DataSnapshot snap : snapshot.child(barcode).getChildren()) {
                        requestIntent.putExtra(snap.getKey(), (String) snap.getValue());
                    }

                    requestIntent.putExtra("store", "duplicate");
                    startActivityForResult(requestIntent, requestCode);
                }

                // Barcode is not in database
                else {
                    Intent requestIntent = new Intent(getApplicationContext(), BarcodeSettings.class);
                    requestIntent.putExtra("store", "no duplicate");
                    startActivityForResult(requestIntent, requestCode);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void searchInDatabase(View view) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Barcode already in database
                if(snapshot.hasChild(barcode)) {
                    Toast.makeText(getApplicationContext(), "Barc" +
                            "ode " + barcode + " already in database!" +
                            " You can edit and overwrite now", Toast.LENGTH_LONG).show();
                    Intent requestIntent = new Intent(getApplicationContext(), BarcodeSettings.class);

                    for(DataSnapshot snap : snapshot.child(barcode).getChildren()) {
                        requestIntent.putExtra(snap.getKey(), (String) snap.getValue());
                    }

                    requestIntent.putExtra("search", "duplicate");
                    startActivityForResult(requestIntent, requestCode);
                }
                // Barcode not in database
                else {
                    Toast.makeText(getApplicationContext(), "Barcode not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    public void rejectFromDatabase(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(responseName, WatchToGlass.OK);
        setResult(WatchToGlass.OK, returnIntent);
        finish();
    }

    @Override
    public void onActivityResult(int receivedCode, int resultCode, Intent data) {
        if(receivedCode == requestCode) {
            if(resultCode == OK) {
                String description = data.getStringExtra("description");
                String price = data.getStringExtra("price");
                String location = data.getStringExtra("location");

                Barcodes obj = new Barcodes(barcode, description, price, location);
                Map<String, Object> map = new ObjectMapper().convertValue(obj, Map.class);
                database.child(barcode).updateChildren(map);

                // database.setValue();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(responseName, WatchToGlass.OK);
                setResult(WatchToGlass.OK, returnIntent);
            }
        }
    }
}

