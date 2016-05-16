package com.example.dominique.barcode;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import
        com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WatchToGlass extends Activity implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final long CONNECTION_TIME_OUT_MS = 100;


    private String nodeId;

    private GoogleApiClient mGoogleApiClient;


    private  void startApiClient() {
    mGoogleApiClient = getGoogleApiClient(this);
    retrieveDeviceNode();
    }

    private void retrieveDeviceNode() {
        Toast.makeText(this, "retrieveDeviceNode() entered", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                List<Node> nodes = result.getNodes();

                if (nodes.size() > 0) {
                    Log.d("node detected", nodes.get(0).getDisplayName());
                    nodeId = nodes.get(0).getId();
                }
                mGoogleApiClient.disconnect();
            }
        }).start();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    startApiClient();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }




    private Asset createAnAsset(int message) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(message);
        return Asset.createFromBytes(outputStream.toByteArray());

    }

    private void putInDataStore() {
        Asset asset = createAnAsset(1);
        PutDataRequest request = PutDataRequest.create("/glassconnection");
        request.putAsset("connection", asset);
        Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }



    public void onStart() {
        super.onStart();



        mGoogleApiClient.connect();

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Toast.makeText(this, "DataChanged!", Toast.LENGTH_LONG).show();
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals("/image")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
                Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                // Do something with bitmap
                Toast.makeText(this, "DataChanged!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        ConnectionResult result = mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }

        // Convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w("TAG", "Requested an unknown Asset.");
            return null;
        }

        // Decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Toast.makeText(this, "AddedListener!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "ConnectionSuspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "ConnectionFailed", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
