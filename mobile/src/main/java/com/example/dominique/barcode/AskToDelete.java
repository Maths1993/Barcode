package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AskToDelete extends Activity {

    private final static String responseName = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_to_delete);
    }

    public void keepBarcode(View view) {
        finish();
    }

    public void deleteBarcode(View view) {
        Intent returnIntent = new Intent();
        String code = getIntent().getStringExtra("code");
        returnIntent.putExtra(responseName, MobileMainActivity.DELETE);
        returnIntent.putExtra("code", code);
        setResult(MobileMainActivity.DELETE, returnIntent);
        finish();
    }

}
