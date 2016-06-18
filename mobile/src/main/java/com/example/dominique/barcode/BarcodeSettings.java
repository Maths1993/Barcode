package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class BarcodeSettings extends Activity implements View.OnClickListener {

    private String oldCode;

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

        oldCode = getIntent().getStringExtra("code");
        text_barcode_edit.setText(oldCode);
        text_description_edit.setText(getIntent().getStringExtra("description"));
        text_price_edit.setText(getIntent().getStringExtra("price"));
        text_location_edit.setText(getIntent().getStringExtra("location"));
    }

    @Override
    public void onClick(View v) {
        if(!text_barcode_edit.getText().toString().equals("")) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("oldCode", oldCode);
            returnIntent.putExtra("code", text_barcode_edit.getText().toString());
            returnIntent.putExtra("description", text_description_edit.getText().toString());
            returnIntent.putExtra("price", text_price_edit.getText().toString());
            returnIntent.putExtra("location", text_location_edit.getText().toString());
            if(getIntent().hasExtra("itemClick")) setResult(BarcodeListViewer.OVERWRITE, returnIntent);
            else setResult(BarcodeListViewer.ADD, returnIntent);
            finish();
        }
    }
}
