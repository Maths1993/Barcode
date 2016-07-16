package com.example.dominique.barcode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SensorActivity extends Activity implements SensorEventListener {

    public static final int CONNECTION_FAIL = 1;
    public static final int NO_TARGETS = 2;
    public static final int ALL_RECEIVED  = 3;
    public static final int NOT_ALL_RECEIVED = 4;
    public static final int CONNECTION_SUSPEND = 5;
    public static final int requestCode = 0;
    private Button button_record;
    private Button button_stopScan;
    boolean recording = false;
    boolean isSaving = false;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float EPSILON = 0.1f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    private SensorManager sensorManager;
    private SensorEventThread sensorThread;

    private GLSurfaceView mGLSurfaceView;
    private SensorManager mSensorManager;
    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private ByteBuffer mIndexBuffer;
    private float[] mOrientation = new float[3];
    private int mCount;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        button_record = (Button) findViewById(R.id.button_record);
        button_stopScan = (Button) findViewById(R.id.button_stopScan);

       // sendToPhone();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorThread = new SensorEventThread("SensorThread");

        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recording) {
                    mSensorManager.registerListener(sensorThread,
                            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, sensorThread.getHandler());
                    mSensorManager.registerListener(sensorThread,
                            mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST, sensorThread.getHandler());
                   /* sensorManager.registerListener(sensorThread,
                            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL, sensorThread.getHandler());*/
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
                Intent requestIntent = new Intent(SensorActivity.this, SendToPhone.class);
                requestIntent.putExtra("data", "stop");
                startActivityForResult(requestIntent, requestCode);
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
                requestIntent.putExtra("data", "recognized");
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
        ArrayList<Float> rollValues;
        double FIRST_THRESHOLD = 3;
        double SECOND_THRESHOLD = 0.0;
        double xMinDiff = -15;
        double xMaxDiff = 15;
        double yMinDiff = -20;
        double yMaxDiff = 15;
        double zMinDiff = 0;
        double zMaxDiff = 50;
        private boolean isFinishing = false;
        private boolean isBlocked = false;
        private float startValue = 1000;
        private float endValue = -1000;

        public SensorEventThread(String name) {
            super(name);
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {

            float[] data = null;
        //    Log.w("TAG", Float.toString(1.111f));

            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                data = mGData;

                new Thread(new Runnable() {
                    public void run() {

                        float xAcc = event.values[0];
                        float yAcc = event.values[1];
                        float zAcc = event.values[2];

                       // Log.w("TAG", Float.toString(xAcc));

                        // Log.w("ACC_MEAN", Float.toString(norm(xAcc,yAcc,zAcc)));


                        if(!isBlocked) {
                        if(!isSaving) {
                            if(norm(xAcc, yAcc, zAcc) > FIRST_THRESHOLD) {
                                isSaving = true;
                                rollValues = new ArrayList<Float>();
                               /* xOriValues = new ArrayList<Float>();
                                yOriValues = new ArrayList<Float>();
                                zOriValues = new ArrayList<Float>();*/
                                Log.w("ENTRANCE", Float.toString(norm(xAcc, yAcc, zAcc)));
                            }
                        } else {
                            if(norm(xAcc, yAcc, zAcc) < SECOND_THRESHOLD) {
                                isFinishing = true;
                                if(rollValues.size() != 0) {
                                    float rollValue = rollValues.get(rollValues.size() - 1);
                                    if (-60 > rollValue && rollValue > -90) {
                                        Log.w("TAG", "Gesture recognized!");
                                        //mSensorManager.unregisterListener(sensorThread);
                                        try {
                                            isBlocked = true;
                                            Thread.sleep(5000);
                                            isBlocked = false;
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Log.w("TAG", "again here");
                                    }
                                    Log.w("OUTRANCE", Float.toString(norm(xAcc, yAcc, zAcc)));
                                    if (checkConditions()) {
                                        Log.w("TAG", "Gesture recognized!");
                                        sendToPhone();
                                    }
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
                          /*  Log.w("XData", Float.toString(xOri));
                            Log.w("YData", Float.toString(yOri));
                            Log.w("ZData", Float.toString(zOri));*/
                        }
                    }
                }).start();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                data = mMData;
            } else return;
            for (int i=0 ; i<3 ; i++) data[i] = event.values[i];
            SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
            SensorManager.getOrientation(mR, mOrientation);
            float incl = SensorManager.getInclination(mI);
            if (mCount++ > 80) {
                final float rad2deg = (float)(180.0f/Math.PI);
                mCount = 0;
                Log.d("Compass", "yaw: " + (int)(mOrientation[0]*rad2deg) +
                        "  pitch: " + (int)(mOrientation[1]*rad2deg) +
                        "  roll: " + (int)(mOrientation[2]*rad2deg) +
                        "  incl: " + (int)(incl*rad2deg)
                );
                if(isSaving) {
                    rollValues.add(mOrientation[2]*rad2deg);
                    if(isFinishing) {
                        isSaving = false;
                        isFinishing = false;
                    }
                }
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

            if(rollValues.size() != 0) {
                float rollDiff = (180 + rollValues.get(rollValues.size() - 1)) + rollValues.get(0);
                Log.w("RollDiff", Float.toString(rollValues.size()-1));
            }

           /* int xSize = xOriValues.size();
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
            }*/

            return false;
        }
    }

}
