package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.example.dominique.barcode.MyFirebaseInstanceIDService;
import com.example.dominique.barcode.MyFirebaseMessagingService;
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

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return new GTINDatabase().query("9780452296122");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(PhoneToGlass.this, "item: " +s, Toast.LENGTH_LONG).show();

            }
        }.execute();


        FirebaseMessaging.getInstance().subscribeToTopic("test");
        //boolean wearAvailable = mGoogleApiClient.hasConnectedApi(Wearable.API);
        //Toast.makeText(this, "wearAvailable: " + wearAvailable,Toast.LENGTH_LONG).show();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }
}
