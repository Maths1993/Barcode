package com.example.dominique.barcode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import de.dfki.ccaal.gestures.Distribution;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;

public class GestureHelper {

    IBinder gestureListenerStub = new IGestureRecognitionListener.Stub() {

        @Override
        public void onGestureLearned(String gestureName) throws RemoteException {
            System.out.println("Gesture" + gestureName + "learned!");
        }

        @Override
        public void onGestureRecognized(Distribution distribution) throws RemoteException {
            System.out.println(String.format("%s %f", distribution.getBestMatch(),distribution.getBestDistance()));
        }

        @Override
        public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
            System.out.println("Training Set " + trainingSet + " deleted!");
        }
    };

    private IGestureRecognitionService recognitionService;

    private final ServiceConnection gestureConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            recognitionService = IGestureRecognitionService.Stub.asInterface(service);
            try {
                recognitionService.registerListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        public void onServiceDisconnected(ComponentName className) {
        }
    };

    public void bindService(Activity activity) {
        Intent gestureBindIntent = new Intent("de.dfki.ccaal.gestures.GESTURE_RECOGNIZER");
        activity.bindService(gestureBindIntent, gestureConnection, Context.BIND_AUTO_CREATE);
    }
}
