/*
 * GestureTrainer.java
 *
 * Created: 18.08.2011
 *
 * Copyright (C) 2011 Robert Nesselrath
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
import android.widget.EditText;
import android.widget.Toast;

import de.dfki.ccaal.gestures.Distribution;
import de.dfki.ccaal.gestures.GestureRecognitionService;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;

public class TestActivity extends Activity {

    IGestureRecognitionService recognitionService;
    String trainingName = "training1";
    String gestureName = "gesture1";
    boolean classificationOn = false;

    private Button button_learning;
    private Button button_recognizing;
    private EditText text_trainingName_edit;
    private EditText text_gestureNameLearn_edit;
    private EditText text_gestureNameRecognize_edit;

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
            Toast.makeText(TestActivity.this, String.format("Gesture " + gestureName + " learned", gestureName), Toast.LENGTH_SHORT).show();
            System.err.println("Gesture " + gestureName + " learned");
        }

        @Override
        public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
            Toast.makeText(TestActivity.this, String.format("Training set " + trainingSet +
                    " deleted", trainingSet), Toast.LENGTH_SHORT).show();
            System.err.println(String.format("Training set " + trainingSet + " deleted", trainingSet));
        }

        @Override
        public void onGestureRecognized(final Distribution distribution) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TestActivity.this, String.format("%s: %f",
                            distribution.getBestMatch(),
                            distribution.getBestDistance()), Toast.LENGTH_LONG).show();
                    System.err.println(String.format("%s: %f",
                            distribution.getBestMatch(),
                            distribution.getBestDistance()));
                }
            });
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture);

        button_learning = (Button) findViewById(R.id.button_learning);
        button_recognizing = (Button) findViewById(R.id.button_recognizing);
        text_gestureNameLearn_edit = (EditText) findViewById(R.id.text_gestureName1_edit);
        text_gestureNameRecognize_edit = (EditText) findViewById(R.id.text_gestureName2_edit);
        text_trainingName_edit = (EditText) findViewById(R.id.text_trainingName_edit);

        button_learning.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recognitionService != null) {
                    try {
                        if(classificationOn) {
                            Toast.makeText(TestActivity.this, "First deactivate recognition mode!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (!recognitionService.isLearning()) {
                                button_learning.setText("Stop learning");
                                recognitionService.startLearnMode(trainingName, gestureName);
                            } else {
                                button_learning.setText("Start learning");
                                recognitionService.stopLearnMode();
                            }
                        }
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
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
                        if(recognitionService.isLearning()) {
                            Toast.makeText(TestActivity.this, "First deactivate learn mode!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (!classificationOn) {
                                recognitionService.startClassificationMode(trainingName);
                                classificationOn = true;
                                button_recognizing.setText("Stop recognition");
                            } else {
                                // First unregister listener
                                try {
                                    recognitionService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                recognitionService = null;
                                unbindService(serviceConnection);
                                // Then resume service to let the user put on the learn mode
                                bindService(new Intent(TestActivity.this, GestureRecognitionService.class),
                                        serviceConnection, Context.BIND_AUTO_CREATE);
                                classificationOn = false;
                                button_recognizing.setText("Start recognition");
                            }
                        }

                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onPause() {
        if (recognitionService != null) {
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
        bindService(new Intent(TestActivity.this, GestureRecognitionService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

}
