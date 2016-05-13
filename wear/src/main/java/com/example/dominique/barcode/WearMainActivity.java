package com.example.dominique.barcode;

import android.app.ActivityManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WearMainActivity extends WearableActivity implements SensorEventListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private static final String TAG = "TAG";

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Button button;
    private boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_main);
        setAmbientEnabled();

        button = (Button) findViewById(R.id.button_gesture);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
        Log.w(TAG, "enterAmb");
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Code for sampling data comes here
            float x_acc = event.values[0];
            float y_acc = event.values[1];
            float z_acc = event.values[2];
            // Write values to console
            Log.e(TAG, "x: "+x_acc+" y: "+y_acc+" z: "+z_acc);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void handleAccelerometer(View view) {
        if(!active) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager == null) {
                Toast.makeText(getApplicationContext(), "Couldn't acquire sensor manager", Toast.LENGTH_LONG).show();
                return;
            }
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer == null) {
                Toast.makeText(getApplicationContext(), "Accelerometer doesn't exist", Toast.LENGTH_LONG).show();
                return;
            }
            boolean result = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            if (!result) {
                Toast.makeText(getApplicationContext(), "Couldn't register listener for accelerometer", Toast.LENGTH_LONG).show();
                return;
            }
            button.setText("END GESTURE RECOGNITION");
            active = true;
        } else {
            sensorManager.unregisterListener(this, accelerometer);
            button.setText("START GESTURE RECOGNITION");
            active = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w(TAG, "pause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "resume");
    }
}
