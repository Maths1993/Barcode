package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class BarcodeInfoRetrieval extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncTask<Void, Void, String>() {

            String code = getIntent().getStringExtra("code");

            @Override
            protected String doInBackground(Void... params) {
                return new GTINDatabase().query(code);
            }

            @Override
            protected void onPostExecute(String info) {
                super.onPostExecute(info);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("info", info);
                setResult(BarcodeHandler.RETRIEVE, returnIntent);
                finish();
            }
        }.execute();
    }
}
