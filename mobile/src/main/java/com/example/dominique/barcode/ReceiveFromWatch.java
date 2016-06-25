package com.example.dominique.barcode;

//import com.google.android.gms.wearable.MessageEvent;
//import com.google.android.gms.wearable.WearableListenerService;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ReceiveFromWatch extends WearableListenerService {
    static String message;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/path")) {
            message = new String(messageEvent.getData());
            // TODO: How to handle gesture recognition in smart phone
            BluetoothHelper.getInstance(getApplicationContext()).startBarcodeScan(); //TODO

            Log.w("TAG", message);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
