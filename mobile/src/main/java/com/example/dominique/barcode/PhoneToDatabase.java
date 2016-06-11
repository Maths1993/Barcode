package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneToDatabase extends Activity {

    private static final String responseName = "1";
    private static final int requestCode = 1;
    public static final int OK = 1;
    public static final int DELETE = 2;

    private Firebase database;
    private String barcode;

    private TextView text_barcode;
    private ListView listView;
    private SimpleAdapter adapter;

    private final String[] from = {"code", "description", "price", "location"};
    private final int[] to = {R.id.code, R.id.description, R.id.price, R.id.location};
    private List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
    private Map<String, Map> cache = new HashMap<String, Map>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_to_database);

        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://torrid-heat-574.firebaseio.com/");

        if(getIntent().hasExtra("barcode")) {
            // Get barcode
            barcode = getIntent().getStringExtra("barcode");

            // Set up barcode text view
            text_barcode = (TextView) findViewById(R.id.text_barcode);
            text_barcode.setText("Scanned code: " + barcode);
            listView = (ListView) findViewById(R.id.listView);

            // Set up database
            Firebase.setAndroidContext(this);
            database = new Firebase("https://torrid-heat-574.firebaseio.com/");

            database.authWithPassword("dominique.laurencelle@hotmail.de", "mathematicus93", new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.w("SUCCESS", "User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.w("FAILURE", "FAILURE");
                }
            });

            // Set up list view and adapter to show contents
            adapter = new SimpleAdapter(this, data, R.layout.row_layout, from, to);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String code = (String) ((TextView) view.findViewById(R.id.code)).getText();
                    String description = (String) ((TextView) view.findViewById(R.id.description)).getText();
                    String price = (String) ((TextView) view.findViewById(R.id.price)).getText();
                    String location = (String) ((TextView) view.findViewById(R.id.location)).getText();
                    Intent requestIntent = new Intent(getApplicationContext(), BarcodeSettings.class);
                    requestIntent.putExtra("itemClick", "itemClick");
                    requestIntent.putExtra("code", code);
                    requestIntent.putExtra("description", description);
                    requestIntent.putExtra("price", price);
                    requestIntent.putExtra("location", location);
                    startActivityForResult(requestIntent, requestCode);
                }

            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String code = (String) ((TextView) view.findViewById(R.id.code)).getText().toString();
                    Intent requestIntent = new Intent(getApplicationContext(), AskToDelete.class);
                    requestIntent.putExtra("delete", "delete");
                    requestIntent.putExtra("code", code);
                    startActivityForResult(requestIntent, requestCode);
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
                    code = (String) dSnap.child("code").getValue().toString();
                    description = (String) dSnap.child("description").getValue().toString();
                    price = (String) dSnap.child("price").getValue().toString();
                    location = (String) dSnap.child("location").getValue().toString();
                }

            });
        } else finish();
    }

    private Map<String, ?> createRow(String code, String description, String price, String location) {
        Map<String, String> row = new HashMap<String, String>();
        row.put("code", code);
        row.put("description", description);
        row.put("price", price);
        row.put("location", location);
        cache.put(code, row);
        return row;
    }

    public void storeInDatabase(View view) {

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Barcode is already in database
                if(snapshot.hasChild(barcode)) {
                    Toast.makeText(getApplicationContext(), "Barc" +
                            "ode " + barcode + " already in database!" +
                            " You can edit and overwrite now", Toast.LENGTH_LONG).show();
                    Intent requestIntent = new Intent(getApplicationContext(), BarcodeSettings.class);

                    for(DataSnapshot snap : snapshot.child(barcode).getChildren()) {
                        requestIntent.putExtra(snap.getKey(), (String) snap.getValue());
                    }

                    requestIntent.putExtra("store", "duplicate");
                    startActivityForResult(requestIntent, requestCode);
                }

                // Barcode is not in database
                else {
                    Intent requestIntent = new Intent(getApplicationContext(), BarcodeSettings.class);
                    requestIntent.putExtra("store", "no duplicate");
                    requestIntent.putExtra("code", barcode);
                    startActivityForResult(requestIntent, requestCode);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.w("TAG", "CANCELLED");
            }
        });
    }

    @Override
    public void onActivityResult(int receivedCode, int resultCode, Intent data) {

        if(receivedCode == requestCode) {
            if(resultCode == OK) {
                String code = data.getStringExtra("code");
                String description = data.getStringExtra("description");
                String price = data.getStringExtra("price");
                String location = data.getStringExtra("location");

                Barcodes obj = new Barcodes(code, description, price, location);
                Map<String, Object> map = new ObjectMapper().convertValue(obj, Map.class);
                database.child(code).updateChildren(map);

            }
            if(resultCode == DELETE) {
                database.child(data.getStringExtra("code")).removeValue();
            }
        }
    }
}