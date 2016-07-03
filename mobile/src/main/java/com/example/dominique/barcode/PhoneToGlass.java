package com.example.dominique.barcode;

import android.app.Activity;
import android.os.Bundle;


public class PhoneToGlass extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothHelper.getInstance(getApplicationContext()).connect();

       // FirebaseMessaging.getInstance().subscribeToTopic("central");

        //new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4").sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE", "status", "NOK", "value", "Stinkender Dödel"));
       // BluetoothHelper.getInstance(getApplicationContext()).connect();

      //  GPSTracker gps = new GPSTracker(PhoneToGlass.this);

      //  String address = gps.getAddress();
    }
}
