package com.example.dominique.barcode;

//import com.google.android.gms.wearable.MessageEvent;
//import com.google.android.gms.wearable.WearableListenerService;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ReceiveFromWatch extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.w("TAG", messageEvent.getPath());
        if (messageEvent.getPath().equals("/path")) {
            try {
                BluetoothHelper.getInstance(getApplicationContext()).startBarcodeScan();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else if(messageEvent.getPath().equals("/stop")) {
            try {
                BluetoothHelper.getInstance(getApplicationContext()).sendStopCommand();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
