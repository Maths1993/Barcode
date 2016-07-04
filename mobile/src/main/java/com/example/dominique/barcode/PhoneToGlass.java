package com.example.dominique.barcode;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessaging;
import com.motioncoding.firebaseserver.FirebaseServer;


public class PhoneToGlass extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("central");

        //new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4").sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE", "status", "NOK", "value", "Stinkender Dödel"));
        try {
        BluetoothHelper.getInstance(getApplicationContext()).connect();
    } catch (Exception e) {
            new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4")
                    .sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE",
                            "status", "ERROR", "value", "Scanner not connected"));
        }

      //  GPSTracker gps = new GPSTracker(PhoneToGlass.this);

      //  String address = gps.getAddress();
    }
}
