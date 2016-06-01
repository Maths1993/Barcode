package com.example.dominique.barcode;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import java.util.HashSet;
import java.util.Set;


public class PhoneToGlass extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private String nodeId;

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
        //boolean wearAvailable = mGoogleApiClient.hasConnectedApi(Wearable.API);
        //Toast.makeText(this, "wearAvailable: " + wearAvailable,Toast.LENGTH_LONG).show();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

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


    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Toast.makeText(this, "start connection" ,Toast.LENGTH_LONG).show();
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
       // if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Toast.makeText(this, "API Unavailable", Toast.LENGTH_LONG).show();
        Log.d("error", connectionResult.toString());
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
