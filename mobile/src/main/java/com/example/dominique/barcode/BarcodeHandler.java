package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarcodeHandler extends Activity {

    private static final int requestCode = 1;
    public static final int ADD = 1;
    public static final int OVERWRITE = 2;
    public static final int DELETE = 3;
    public static final int RETRIEVE = 4;
    public static final String CODE = "code";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String LOCATION = "location";

    private Firebase database;

    private TextView text_barcode;
    private EditText text_barcode_edit;
    private ListView listView;
    private SimpleAdapter adapter;

    private final String[] from = {"code", "description", "price", "location"};
    private final int[] to = {R.id.code, R.id.description, R.id.price, R.id.location};
    private List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
    private Map<String, Map> cache = new HashMap<String, Map>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_handler);

        if(getIntent().hasExtra("barcode")) {
            // Set up database
            Firebase.setAndroidContext(this);
            database = new Firebase("https://torrid-heat-574.firebaseio.com/");

            initVisualElements();
            setListeners();
        } else finish();

    }

    public void initVisualElements() {
        text_barcode = (TextView) findViewById(R.id.text_barcode);
        text_barcode.setText("Code:");
        text_barcode_edit = (EditText) findViewById(R.id.text_code_edit);
        String code = getIntent().getStringExtra("barcode");
        text_barcode_edit.setText(code);
        listView = (ListView) findViewById(R.id.listView);

        // Set up list view and adapter to show contents
        adapter = new SimpleAdapter(this, data, R.layout.row_layout, from, to);
        listView.setAdapter(adapter);
    }

    public void setListeners() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = (String) ((TextView) view.findViewById(R.id.code)).getText();
                String description = (String) ((TextView) view.findViewById(R.id.description)).getText();
                String price = (String) ((TextView) view.findViewById(R.id.price)).getText();
                String location = (String) ((TextView) view.findViewById(R.id.location)).getText();

                Intent intent = getIntent(BarcodeSettings.class, code, description, price, location);
                intent.putExtra("itemClick", "itemClick");
                startActivityForResult(intent, requestCode);
            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String code = (String) ((TextView) view.findViewById(R.id.code)).getText().toString();
                Intent intent = getIntent(AskToDelete.class, code, "", "", "");
                startActivityForResult(intent, requestCode);
                return true;
            }

        });

        database.addChildEventListener(new ChildEventListener() {

            String code;
            String description;
            String price;
            String location;

            @Override
            public void onChildAdded(DataSnapshot dSnap, String s) {
                init(dSnap);
                // Add new entry
                adapter.notifyDataSetChanged();
                data.add(createRow(code, description, price, location));
            }

            @Override
            public void onChildChanged(DataSnapshot dSnap, String s) {
                init(dSnap);
                // Remove old entry
                adapter.notifyDataSetChanged();
                data.remove(cache.get(code));
                // Add new entry
                adapter.notifyDataSetChanged();
                data.add(createRow(code, description, price, location));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                init(dataSnapshot);
                // Remove old entry
                adapter.notifyDataSetChanged();
                data.remove(cache.get(code));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.w("MOVED", "moved");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w("CANCELED", "canceled");
            }

            public void init(DataSnapshot dSnap) {
                code = (String) dSnap.child(CODE).getValue().toString();
                description = (String) dSnap.child(DESCRIPTION).getValue().toString();
                price = (String) dSnap.child(PRICE).getValue().toString();
                location = (String) dSnap.child(LOCATION).getValue().toString();
            }

        });
    }

    private Map<String, ?> createRow(String code, String description, String price, String location) {
        Map<String, String> row = new HashMap<String, String>();
        row.put(CODE, code);
        row.put(DESCRIPTION, description);
        row.put(PRICE, price);
        row.put(LOCATION, location);
        cache.put(code, row);
        return row;
    }

    public void storeInDatabase(View view) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String code = text_barcode_edit.getText().toString();

                if(code.equals("")) return;

                // Barcode is already in database
                if(snapshot.hasChild(code)) {
                    Intent requestIntent = new Intent(getApplicationContext(), BarcodeSettings.class);

                    for(DataSnapshot snap : snapshot.child(code).getChildren()) {
                        requestIntent.putExtra(snap.getKey(), (String) snap.getValue());
                    }

                    startActivityForResult(requestIntent, requestCode);
                } else {
                    Intent intent = getIntent(BarcodeSettings.class, code, "", "", "");
                    startActivityForResult(intent, requestCode);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w("TAG", "CANCELLED");
            }
        });
    }

    public Intent getIntent(Class cl, String code, String description, String price, String location) {
        Intent intent = new Intent(getApplicationContext(), cl);
        intent.putExtra(CODE, code);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(PRICE, price);
        intent.putExtra(LOCATION, location);
        return intent;
    }

    public void retrieveBarcodeInfo(View view) {
        String code = text_barcode_edit.getText().toString();
        Intent intent = getIntent(BarcodeInfoRetrieval.class, code, "", "", "");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int receivedCode, int resultCode, Intent data) {
        if(receivedCode == requestCode) {
            if(resultCode == ADD || resultCode == OVERWRITE) {
                if(resultCode == OVERWRITE) {
                    String oldCode = data.getStringExtra("oldCode");
                    database.child(oldCode).removeValue();
                }

                String code = data.getStringExtra("code");
                String description = data.getStringExtra("description");
                String price = data.getStringExtra("price");
                String location = data.getStringExtra("location");

                Barcodes obj = new Barcodes(code, description, price, location);
                Map<String, Object> map = new ObjectMapper().convertValue(obj, Map.class);
                database.child(code).updateChildren(map);
            }
            else if(resultCode == DELETE) {
                String code = data.getStringExtra("code");
                database.child(code).removeValue();
            }
            else if(resultCode == RETRIEVE) {
                String code = text_barcode_edit.getText().toString();
                String info = data.getStringExtra("info");
                Intent intent = getIntent(BarcodeSettings.class, code, info, "", "");
                startActivityForResult(intent, requestCode);
            }
        }
    }
}