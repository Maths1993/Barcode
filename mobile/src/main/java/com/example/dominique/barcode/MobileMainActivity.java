package com.example.dominique.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.motioncoding.firebaseserver.FirebaseServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MobileMainActivity extends Activity {

    private Button button_connect;
    private Button button_show;

    private static final int requestCode = 1;
    public static final int ADD = 1;
    public static final int OVERWRITE = 2;
    public static final int DELETE = 3;
    public static final String CODE = "code";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String LOCATION = "location";

    private Firebase database;

    private ListView listView;
    private SimpleAdapter adapter;

    private final String[] from = {"code", "description", "price", "location"};
    private final int[] to = {R.id.code, R.id.description, R.id.price, R.id.location};
    private List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
    private Map<String, Map> cache = new HashMap<String, Map>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4").sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE", "status", "NOK", "value", "Stinkender Dödel"));
       /* try {
            BluetoothHelper.getInstance(getApplicationContext()).connect();
        } catch (Exception e) {
            new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4")
                    .sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE",
                            "status", "ERROR", "value", "Scanner not connected"));
        }*/

        Firebase.setAndroidContext(this);
        database = new Firebase("https://torrid-heat-574.firebaseio.com/");

        setupListView();
        setListeners();
    }

    public void startConnection(View view) {
        FirebaseMessaging.getInstance().subscribeToTopic("central");

        //new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4").sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE", "status", "NOK", "value", "Stinkender Dödel"));
        try {
            BluetoothHelper.getInstance(getApplicationContext()).connect();
        } catch (Exception e) {
            new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4")
                    .sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE",
                            "status", "ERROR", "value", "Scanner not connected"));
        }
    }

    public void setupListView() {
        listView = (ListView) findViewById(R.id.listView);
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
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()){
                            new AlertDialog.Builder(MobileMainActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("Delete barcode ?")
                                    .setCancelable(false)
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String code = (String) ((TextView) view.findViewById(R.id.code)).getText().toString();
                                            database.child(code).removeValue();
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create().show();
                        }
                    }
                });
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
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
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
                Intent intent = getIntent(BarcodeSettings.class, "", "", "", "");
                startActivityForResult(intent, requestCode);
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

                BarcodeLayout obj = new BarcodeLayout(code, description, price, location);
                Map<String, Object> map = new ObjectMapper().convertValue(obj, Map.class);
                database.child(code).updateChildren(map);
            }
            else if(resultCode == DELETE) {
                String code = data.getStringExtra("code");
                database.child(code).removeValue();
            }
        }
    }

}
