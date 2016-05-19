package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class PhoneToDatabase extends Activity {

    private static final String responseName = "0";
    private Firebase database;
    private String barcode;
    private Button button_store;
    private Button button_search;
    private Button button_reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_to_database);

        if(getIntent().hasExtra("barcode")) {
            button_reject = (Button) findViewById(R.id.button_reject);
            button_search = (Button) findViewById(R.id.button_search_database);
            button_store = (Button) findViewById(R.id.button_store_database);
            // Set up database
            Firebase.setAndroidContext(this);
            database = new Firebase("https://torrid-heat-574.firebaseio.com/").child("Barcodes");
        }
        if(!getIntent().hasExtra("barcode")) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(responseName, WatchToGlass.NOT_OK);
            setResult(WatchToGlass.NOT_OK, returnIntent);
            finish();
        }

    }

    public void storeInDatabase(View view) {
        String strCode = getIntent().getStringExtra("barcode");

       // database.setValue();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(responseName, WatchToGlass.OK);
        setResult(WatchToGlass.OK, returnIntent);
        finish();
    }

    public void searchInDatabase(View view) {
        database.child("0001").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
               // Toast.makeText(getApplicationContext(), (String) snapshot.getValue(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(FirebaseError error) {

            }
        });
        String s = getIntent().getStringExtra("barcode");
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

    }

    public void rejectFromDatabase(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(responseName, WatchToGlass.OK);
        setResult(WatchToGlass.OK, returnIntent);
        finish();
    }
}

