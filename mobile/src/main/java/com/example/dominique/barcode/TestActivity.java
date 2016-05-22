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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import de.dfki.ccaal.gestures.Distribution;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;

public class TestActivity extends Activity {

    IGestureRecognitionService recognitionService;
    String trainingName = "training0";
    String gestureName = "gesture0";

    private Button button_learning;
    private Button button_recognizing;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            recognitionService = IGestureRecognitionService.Stub.asInterface(service);
            try {
                recognitionService.startClassificationMode(trainingName);
                recognitionService.registerListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
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
            Toast.makeText(TestActivity.this, String.format("Gesture %s learned", gestureName), Toast.LENGTH_SHORT).show();
            System.err.println("Gesture %s learned");
        }

        @Override
        public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
            Toast.makeText(TestActivity.this, String.format("Training set %s deleted", trainingSet), Toast.LENGTH_SHORT).show();
            System.err.println(String.format("Training set %s deleted", trainingSet));
        }

        @Override
        public void onGestureRecognized(final Distribution distribution) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TestActivity.this, String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()), Toast.LENGTH_LONG).show();
                    System.err.println(String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()));
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

        button_learning.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (recognitionService != null) {
                    try {
                        if (!recognitionService.isLearning()) {
                            button_learning.setText("Stop learning");
                            recognitionService.startLearnMode(trainingName, gestureName);
                        } else {
                            button_learning.setText("Start learning");
                            recognitionService.stopLearnMode();
                        }
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        button_recognizing.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (recognitionService != null) {
                    try {
                        recognitionService.startClassificationMode(trainingName);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        try {
            recognitionService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        recognitionService = null;
        unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    protected void onResume() {
        bindService(new Intent(TestActivity.this,
                        GestureService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);

       // Intent bindIntent = new Intent("Recognizer");
       // bindIntent.setPackage(this.getPackageName());
       // getApplicationContext().bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }
}
