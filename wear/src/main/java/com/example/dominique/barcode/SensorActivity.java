package com.example.dominique.barcode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class SensorActivity extends Activity implements SensorEventListener {

    public static final int CONNECTION_FAIL = 1;
    public static final int NO_TARGETS = 2;
    public static final int ALL_RECEIVED  = 3;
    public static final int NOT_ALL_RECEIVED = 4;
    public static final int CONNECTION_SUSPEND = 5;
    public static final int requestCode = 0;
    private Button button_record;
    boolean recording = false;
    boolean isSaving = false;

    private SensorManager sensorManager;
    private SensorEventThread sensorThread;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        button_record = (Button) findViewById(R.id.button_record);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorThread = new SensorEventThread("SensorThread");

        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recording) {
                    sensorManager.registerListener(sensorThread,
                            sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL, sensorThread.getHandler());
                    sensorManager.registerListener(sensorThread,
                            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, sensorThread.getHandler());
                    button_record.setText("Start recognition");
                } else {
                    sensorManager.unregisterListener(sensorThread);
                    button_record.setText("Stop recognition");
                }
                recording = !recording;
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
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

        new Thread(new Runnable() {
            public void run() {
                Intent requestIntent = new Intent(SensorActivity.this, SendToPhone.class);
                startActivityForResult(requestIntent, requestCode);
            }
        }).start();
    }

    @Override
    public void onActivityResult(int receivedCode, int resultCode, Intent data) {
        if(receivedCode == requestCode) {
            if(resultCode == CONNECTION_FAIL) printMessage("Connection failure");
            else if(resultCode == CONNECTION_SUSPEND) printMessage("Connection suspended");
            else if(resultCode == NO_TARGETS) printMessage("No targets. Pair watch with target");
            else if(resultCode == ALL_RECEIVED) printMessage("All targets received the signal");
            else if(resultCode == NOT_ALL_RECEIVED) printMessage("Some targets didn't receive the signal");
        }
    }

    public void printMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    class SensorEventThread extends HandlerThread implements
            SensorEventListener {

        Handler handler;
        ArrayList<Float> xOriValues;
        ArrayList<Float> yOriValues;
        ArrayList<Float> zOriValues;
        double FIRST_THRESHOLD = 2;
        double SECOND_THRESHOLD = 0.2;
        double xMinDiff = -15;
        double xMaxDiff = 15;
        double yMinDiff = -20;
        double yMaxDiff = 15;
        double zMinDiff = 0;
        double zMaxDiff = 50;

        public SensorEventThread(String name) {
            super(name);
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                new Thread(new Runnable() {
                    public void run() {
                        float xAcc = event.values[0];
                        float yAcc = event.values[1];
                        float zAcc = event.values[2];

                        // Log.w("ACC_MEAN", Float.toString(norm(xAcc,yAcc,zAcc)));

                        if(!isSaving) {
                            if(norm(xAcc, yAcc, zAcc) > FIRST_THRESHOLD) {
                                isSaving = true;
                                xOriValues = new ArrayList<Float>();
                                yOriValues = new ArrayList<Float>();
                                zOriValues = new ArrayList<Float>();
                            }
                        } else {
                            if(norm(xAcc, yAcc, zAcc) < SECOND_THRESHOLD) {
                                isSaving = false;
                                if(checkConditions()) {
                                    Log.w("TAG", "Gesture recognized!");
                                    sendToPhone();
                                }
                                /*int xSize = xOriValues.size();
                                int ySize = yOriValues.size();
                                int zSize = zOriValues.size();

                                if(xSize != 0 && ySize != 0 && zSize != 0) {
                                    float xDiff = xOriValues.get(xSize - 1) - xOriValues.get(0);
                                    float yDiff = yOriValues.get(ySize - 1) - yOriValues.get(0);
                                    float zDiff = zOriValues.get(zSize - 1) - zOriValues.get(0);
                                    Log.w("XData", Float.toString(xDiff));
                                    Log.w("YData", Float.toString(yDiff));
                                    Log.w("ZData", Float.toString(zDiff));
                                }*/
                            }
                        }
                    }
                }).start();
            } else if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

                new Thread(new Runnable() {
                    public void run() {
                        if(isSaving) {
                            float xOri = event.values[0];
                            float yOri = event.values[1];
                            float zOri = event.values[2];
                            saveDataToFile(xOri, yOri, zOri);
                        }
                    }
                }).start();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            handler = new Handler(sensorThread.getLooper());
        }

        public Handler getHandler() {
            return handler;
        }

        public void quitLooper() {
            if (sensorThread.isAlive()) {
                sensorThread.getLooper().quit();
            }
        }

        public void saveDataToFile(Float xOri, Float yOri, Float zOri) {
            xOriValues.add(xOri);
            yOriValues.add(yOri);
            zOriValues.add(zOri);
        }

        public Float norm(Float x, Float y, Float z) {
            float norm = (float) Math.sqrt(x * x + y * y + z * z) - 9f;
            return norm;
        }

        public boolean checkConditions() {

            int xSize = xOriValues.size();
            int ySize = yOriValues.size();
            int zSize = zOriValues.size();

            if(xSize != 0 && ySize != 0 && zSize != 0) {
                float xDiff = xOriValues.get(xSize - 1) - xOriValues.get(0);
                float yDiff = yOriValues.get(ySize - 1) - yOriValues.get(0);
                float zDiff = zOriValues.get(zSize - 1) - zOriValues.get(0);

                if(xMinDiff < xDiff && xMaxDiff > xDiff) {
                    if(yMinDiff < yDiff && yMaxDiff > yDiff) {
                        if(zMinDiff < zDiff && zMaxDiff > zDiff) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

}
