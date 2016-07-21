package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class SensorActivity extends Activity implements SensorEventListener {

    public static final int CONNECTION_FAIL = 1;
    public static final int NO_TARGETS = 2;
    public static final int ALL_RECEIVED = 3;
    public static final int NOT_ALL_RECEIVED = 4;
    public static final int CONNECTION_SUSPEND = 5;
    public static final int requestCode = 0;
    private Button button_record;
    private Button button_stopScan;
    boolean recording = false;
    boolean isSaving = false;

    private SensorEventThread sensorThread;

    private SensorManager mSensorManager;
    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private int mCount;
    private GoogleApiClient googleClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        button_record = (Button) findViewById(R.id.button_record);
        button_stopScan = (Button) findViewById(R.id.button_stopScan);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorThread = new SensorEventThread("SensorThread");

        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {
                    mSensorManager.registerListener(sensorThread,
                            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST, sensorThread.getHandler());
                    mSensorManager.registerListener(sensorThread,
                            mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, sensorThread.getHandler());
                    button_record.setText("Stop recognition");
                } else {
                    mSensorManager.unregisterListener(sensorThread);
                    button_record.setText("Start recognition");
                }
                recording = !recording;
            }
        });

        button_stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Stop scanning";
                new SendToDataLayerThread("/stop", message).start();
            }
        });


        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleClient.connect();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    public void sendToPhone() {
        Log.w("SENT", "TOPHONE");

        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

        String message = "Gesture recognized";
        new SendToDataLayerThread("/path", message).start();

    }

    @Override
    public void onActivityResult(int receivedCode, int resultCode, Intent data) {
        if (receivedCode == requestCode) {
            if (resultCode == CONNECTION_FAIL) printMessage("Connection failure");
            else if (resultCode == CONNECTION_SUSPEND) printMessage("Connection suspended");
            else if (resultCode == NO_TARGETS) printMessage("No targets. Pair watch with target");
            else if (resultCode == ALL_RECEIVED) printMessage("All targets received the signal");
            else if (resultCode == NOT_ALL_RECEIVED)
                printMessage("Some targets didn't receive the signal");
        }
    }

    public void printMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    class SensorEventThread extends HandlerThread implements SensorEventListener {

        Handler handler;
        ArrayList<Float> xOriValues;
        ArrayList<Float> yOriValues;
        ArrayList<Float> zOriValues;
        double FIRST_THRESHOLD = 3;
        double SECOND_THRESHOLD = 0.5;
        int cntOver = 0;
        int cntUnder = 0;
        private boolean isBlocked = false;
        private boolean recognized = false;

        public SensorEventThread(String name) {
            super(name);
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {

            new Thread(new Runnable() {
                public void run() {


                    float[] data = null;

                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                        data = mGData;

                        float xAcc = event.values[0];
                        float yAcc = event.values[1];
                        float zAcc = event.values[2];

                        // Log.w("ACC_MEAN", Float.toString(norm(xAcc,yAcc,zAcc)));

                        if (!isBlocked) {
                            if (norm(xAcc, yAcc, zAcc) > FIRST_THRESHOLD) {
                                //Log.w("TAG2", "OVER THRESHOLD");
                                cntOver++;
                                //  Log.w("CNT_OVER", Integer.toString(cntOver));
                                if (cntOver > 60) {
                                    Log.w("TAG", "OVER THRESHOLD");
                                    isSaving = true;
                                    cntOver = 0;
                                }
                            } else if (norm(xAcc, yAcc, zAcc) < SECOND_THRESHOLD) {
                                cntUnder++;
                                //  Log.w("CNT_UNDER", Integer.toString(cntUnder));
                                if (cntUnder > 5) {
                                    if (isSaving) {
                                        Log.w("TAG", "UNDER THRESHOLD");
                                        try {
                                            isBlocked = true;
                                            isSaving = false;
                                            cntUnder = 0;
                                            cntOver = 0;
                                            Thread.sleep(5000);
                                            isBlocked = false;
                                            recognized = false;
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        isSaving = false;
                                    }
                                    cntUnder = 0;
                                    cntOver = 0;
                                }
                            }
                        }
                    } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                        data = mMData;
                    } else return;

                    for (int i = 0; i < 3; i++) {
                        data[i] = event.values[i];
                    }
                    SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
                    SensorManager.getOrientation(mR, mOrientation);
                    float incl = SensorManager.getInclination(mI);
                    if (mCount++ > 80) {
                        final float rad2deg = (float) (180.0f / Math.PI);
                        mCount = 0;
                        if (isBlocked && !recognized) {
                            int roll = (int) (mOrientation[2] * rad2deg);
                            if (roll < 100 && roll > 20) {
                                recognized = true;
                                sendToPhone();
                                //  Log.w("TAG", "Recognized");
                                Looper.prepare();
                                Toast.makeText(SensorActivity.this.getApplicationContext(), "Recognized", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public Handler getHandler() {
            return handler;
        }

        public void quitLooper() {
            if (sensorThread.isAlive()) {
                sensorThread.getLooper().quit();
            }
        }

        public Float norm(Float x, Float y, Float z) {
            float norm = (float) Math.sqrt(x * x + y * y + z * z) - 9f;
            return norm;
        }
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
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            if (nodes.getNodes().isEmpty()) {
                return;
            }
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient,
                        node.getId(), path, message.getBytes()).await();
                if (!result.getStatus().isSuccess()) {

                }
            }
        }
    }
}