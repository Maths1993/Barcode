package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class PhoneToGlass extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent requestIntent = new Intent(getApplicationContext(), BarcodeHandler.class);
        requestIntent.putExtra("barcode", "5449");
        startActivity(requestIntent);
    }
}
