package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BarcodeSettings extends Activity implements View.OnClickListener {

    private static final String responseName = "0";

    private Button button_apply;
    private EditText text_description_edit;
    private EditText text_price_edit;
    private EditText text_location_edit;
    private TextView text_description;
    private TextView text_price;
    private TextView text_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_settings);

        button_apply = (Button) findViewById(R.id.button_apply);
        init();
    }

    public void init() {

        text_description_edit = (EditText) findViewById(R.id.text_description_edit);
        text_price_edit = (EditText) findViewById(R.id.text_price_edit);
        text_location_edit = (EditText) findViewById(R.id.text_location_edit);
        button_apply = (Button) findViewById(R.id.button_apply);
        // Set default values for already existing barcode
        if((getIntent().hasExtra("store") && getIntent().getStringExtra("store").equals("duplicate"))
                || getIntent().hasExtra("search")) {
                text_description_edit.setText(getIntent().getStringExtra("description"));
                text_price_edit.setText(getIntent().getStringExtra("price"));
                text_location_edit.setText(getIntent().getStringExtra("location"));
        }
    }

    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("description", text_description_edit.getText().toString());
        returnIntent.putExtra("price", text_price_edit.getText().toString());
        returnIntent.putExtra("location", text_location_edit.getText().toString());
        setResult(PhoneToDatabase.OK, returnIntent);
        Toast.makeText(getApplicationContext(), text_description_edit.getText(), Toast.LENGTH_LONG).show();
        finish();
    }
}
