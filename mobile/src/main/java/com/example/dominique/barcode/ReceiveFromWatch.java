package com.example.dominique.barcode;

//import com.google.android.gms.wearable.MessageEvent;
//import com.google.android.gms.wearable.WearableListenerService;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.motioncoding.firebaseserver.FirebaseServer;

public class ReceiveFromWatch extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/path")) {

            try {
                BluetoothHelper.getInstance(getApplicationContext()).connect();
            } catch (Exception e) {
                new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4")
                        .sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE",
                                "status", "ERROR", "value", "Scanner not connected"));
            }

        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
