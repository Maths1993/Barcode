package com.example.dominique.barcode;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ReceiveFromWatch extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/path")) {
            final String message = new String(messageEvent.getData());
            // TODO: How to handle gesture recognition in smart phone
            // ...
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}
