package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BarcodeSettings extends Activity implements View.OnClickListener {

    private static final String responseName = "0";

    private EditText text_barcode_edit;
    private EditText text_description_edit;
    private EditText text_price_edit;
    private EditText text_location_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_settings);
        init();
    }

    public void init() {
        text_barcode_edit = (EditText) findViewById(R.id.text_barcode_edit);
        text_description_edit = (EditText) findViewById(R.id.text_description_edit);
        text_price_edit = (EditText) findViewById(R.id.text_price_edit);
        text_location_edit = (EditText) findViewById(R.id.text_location_edit);
        text_barcode_edit.setText(getIntent().getStringExtra("code"));

        // Set default values for already existing barcode
        if((getIntent().hasExtra("store") && getIntent().getStringExtra("store").equals("duplicate"))
                || getIntent().hasExtra("itemClick")) {
            text_description_edit.setText(getIntent().getStringExtra("description"));
            text_price_edit.setText(getIntent().getStringExtra("price"));
            text_location_edit.setText(getIntent().getStringExtra("location"));
        }
    }

    @Override
    public void onClick(View v) {
        if(!text_barcode_edit.getText().toString().equals("")) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("code", text_barcode_edit.getText().toString());
            returnIntent.putExtra("description", text_description_edit.getText().toString());
            returnIntent.putExtra("price", text_price_edit.getText().toString());
            returnIntent.putExtra("location", text_location_edit.getText().toString());
            setResult(PhoneToDatabase.OK, returnIntent);
            finish();
        } else Toast.makeText(getApplicationContext(), "Code mustn't be empty", Toast.LENGTH_LONG);
    }
}
