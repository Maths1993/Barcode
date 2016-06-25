package com.example.dominique.barcode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import de.dfki.ccaal.gestures.Distribution;
import de.dfki.ccaal.gestures.GestureRecognitionService;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;

public class WearMainActivity extends Activity {

    private static final String TAG = "TAG";
    public static final int CONNECTION_FAIL = 1;
    public static final int NO_TARGETS = 2;
    public static final int ALL_RECEIVED  = 3;
    public static final int NOT_ALL_RECEIVED = 4;
    public static final int CONNECTION_SUSPEND = 5;
    public static final int requestCode = 0;
    int i = 0;


    IGestureRecognitionService recognitionService;
    final String trainingName = "training";
    final String gestureName = "gesture";
    boolean classificationOn = false;
    boolean recognized = false;

    private Button button_learning;
    private Button button_recognizing;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            recognitionService = IGestureRecognitionService.Stub.asInterface(service);
            try {
                recognitionService.registerListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            recognitionService = null;
        }
    };

    IBinder gestureListenerStub = new IGestureRecognitionListener.Stub() {

        @Override
        public void onGestureLearned(String gestureName) throws RemoteException {
            printMessage("Gesture learned");
        }

        @Override
        public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
            printMessage("Training set deleted");
        }

        @Override
        public void onGestureRecognized(final Distribution distribution) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    printMessage(String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()));
                    if(isTrueGesture(distribution.getBestDistance())) {
                      //  reconfigureRecognition();
                        recognized = true;
                        sendToPhone();
                    }
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture);

        button_learning = (Button) findViewById(R.id.button_learning);
        button_recognizing = (Button) findViewById(R.id.button_recognizing);

        button_learning.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recognitionService != null) {
                    try {
                        if(!classificationOn) {
                            if (!recognitionService.isLearning()) {
                                button_learning.setText("Stop learning");
                                recognitionService.startLearnMode(trainingName, gestureName);
                            } else {
                                button_learning.setText("Start learning");
                                recognitionService.stopLearnMode();
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        button_recognizing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (recognitionService != null) {
                    try {
                        if(!recognitionService.isLearning()) {
                            if (!classificationOn) {
                                recognitionService.startClassificationMode(trainingName);
                                button_recognizing.setText("Stop recognition");
                            } else {
                                reconfigureRecognition();
                                button_recognizing.setText("Start recognition");
                            }
                            classificationOn = !classificationOn;
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    // TODO: Set thresholds for recognizing the gesture, ...
    public boolean isTrueGesture(Double distance) { return distance < 15; }

    public void reconfigureRecognition() {
        try {
            recognitionService.unregisterListener(IGestureRecognitionListener.Stub.
                    asInterface(gestureListenerStub));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        recognitionService = null;
        unbindService(serviceConnection);
        bindService(new Intent(WearMainActivity.this, GestureRecognitionService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void sendToPhone() {
        Intent requestIntent = new Intent(this, SendToPhone.class);
        startActivityForResult(requestIntent, requestCode);
    }

    @Override
    public void onActivityResult(int receivedCode, int resultCode, Intent data) {
        if(receivedCode == requestCode) {
            if(resultCode == CONNECTION_FAIL) printMessage("Connection failure");
            else if(resultCode == CONNECTION_SUSPEND) printMessage("Connection suspended");
            else if(resultCode == NO_TARGETS) printMessage("No targets. Pair watch with target");
            else if(resultCode == ALL_RECEIVED) printMessage("All targets received the signal");
            else if(resultCode == NOT_ALL_RECEIVED) printMessage("Some targets didn't receive the signal");
            recognized = false;
        }
    }

    public void printMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (recognitionService != null && !recognized) {
            try {
                recognitionService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            recognitionService = null;
            unbindService(serviceConnection);
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        bindService(new Intent(WearMainActivity.this, GestureRecognitionService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

}
