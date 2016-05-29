package com.example.dominique.barcode;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import
        com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.sharing.internal.ReceiveContentRequest;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PhoneToGlass extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private String nodeId;
    public static byte[] data = new byte[] { (byte)0xe0 };
    private GoogleApiClient mGoogleApiClient;
    Context context = this;
    private static final String responseName = "0";
    private static final int responseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        boolean wearAvailable = mGoogleApiClient.hasConnectedApi(Wearable.API);
        Toast.makeText(this, "wearAvailable: " + wearAvailable,Toast.LENGTH_LONG).show();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private Set<Node> getNodes() {
       Set<Node> setNode = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            setNode.add(node);
        }
        return setNode;
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }



    public void sendToGlass() {
        Wearable.MessageApi.sendMessage(mGoogleApiClient, "/glass",
               pickBestNodeId(getNodes()), data).setResultCallback(
                new ResultCallback() {
                    @Override
                    public void onResult(@NonNull Result result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.d("sending","failed");
                        }
                    }
                }
        );
    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
        Toast.makeText(this, "AddedListener!", Toast.LENGTH_LONG).show();
        if(ReceiveFromWatch.message != null) {
            new SendToDataLayerThread("/path", ReceiveFromWatch.message).start();
        }
        //for testing
        new SendToDataLayerThread("/path", "halloo").start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "ConnectionSuspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(this, "ConnectionFailed", Toast.LENGTH_LONG).show();
    if(connectionResult != null) {
        Log.d("connection error", connectionResult.toString());
    }
        else Log.d("connectionFailed", "null");
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    class SendToDataLayerThread extends Thread {

        private String path;
        private String message;
    // Constructor to send a message to the data layer
    SendToDataLayerThread(String p, String msg) {
        path = p;
        message = msg;
    }

    public void run() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(responseName, responseCode);
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        if(nodes.getNodes().isEmpty()) {
            Toast.makeText(context, "empty",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient,
                    node.getId(), path, message.getBytes()).await();
            if(!result.getStatus().isSuccess()) {
                Toast.makeText(context, "not all received",Toast.LENGTH_LONG).show();
                finish();
            }
        }
        Toast.makeText(context, "all received",Toast.LENGTH_LONG).show();
        finish();
    }
}
}
