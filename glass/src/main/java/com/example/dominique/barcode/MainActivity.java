package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.motioncoding.firebaseserver.FirebaseServer;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("glass");
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBarcode();
    }

    private void requestBarcode() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if( ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI ) {
            new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4").sendDataToTopic("central", FirebaseServer.stringToMap("cmd", "SCAN"));
        }
        else {
            Toast.makeText(MainActivity.this, "No WIFI connection", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
