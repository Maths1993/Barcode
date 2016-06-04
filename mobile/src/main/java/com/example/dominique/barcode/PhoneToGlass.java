package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessaging;


public class PhoneToGlass extends Activity {


    private String nodeId;

    Context context = this;
    private static final String responseName = "0";
    private static final int responseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        //boolean wearAvailable = mGoogleApiClient.hasConnectedApi(Wearable.API);
        //Toast.makeText(this, "wearAvailable: " + wearAvailable,Toast.LENGTH_LONG).show();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }
}
