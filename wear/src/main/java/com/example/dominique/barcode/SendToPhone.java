package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class SendToPhone extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleClient;
    private TextView text;
    private static final String responseName = "0";
    private static final int responseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_to_phone);


        Log.w("Herr", "jo");

        text = (TextView) findViewById(R.id.connectText);

        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(!(googleClient != null)) {
            Toast.makeText(getApplicationContext(), "Couldn't get GoogleAPIClient", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        text.setText("Connecting ...");
        googleClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        text.setText("Connected");
        String message = "Gesture recognized";
        new SendToDataLayerThread("/path", message).start();
    }

    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int errorCode) {
        text.setText("Connection suspendened. Code: " + errorCode);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(responseName, SensorActivity.CONNECTION_SUSPEND);
        setResult(SensorActivity.CONNECTION_SUSPEND, returnIntent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        text.setText(connectionResult.getErrorCode()+"");

        String error = connectionResult.getErrorMessage();

        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(responseName, SensorActivity.CONNECTION_FAIL);
        setResult(SensorActivity.CONNECTION_FAIL, returnIntent);
        finish();
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
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            if(nodes.getNodes().isEmpty()) {
                setResult(SensorActivity.NO_TARGETS, returnIntent);
                finish();
                return;
            }
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient,
                        node.getId(), path, message.getBytes()).await();
                if(!result.getStatus().isSuccess()) {
                    setResult(SensorActivity.NOT_ALL_RECEIVED, returnIntent);
                    finish();
                }
            }
            setResult(SensorActivity.ALL_RECEIVED, returnIntent);
            finish();
        }
    }
}
