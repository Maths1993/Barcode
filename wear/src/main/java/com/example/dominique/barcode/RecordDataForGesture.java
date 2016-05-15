package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecordDataForGesture extends Activity implements SensorEventListener {

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
        setContentView(R.layout.record_test_data);

        button = (Button) findViewById(R.id.button_recording);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(active) {
            sensorManager.unregisterListener(this, accelerometer);
            active = false;
        }
    }

    public void handleClick(View view) {
        if(!active) {
            if(accelerometerAccessible()) startRecording();
            button.setText("END RECORDING");
            active = true;
        } else {
            sensorManager.unregisterListener(this, accelerometer);
            endRecording();
            button.setText("START RECORDING");
            active = false;
        }
    }

    public boolean accelerometerAccessible() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            Toast.makeText(getApplicationContext(), "Couldn't acquire sensor manager", Toast.LENGTH_LONG).show();
            return false;
        }
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            Toast.makeText(getApplicationContext(), "Accelerometer doesn't exist", Toast.LENGTH_LONG).show();
            return false;
        }
        boolean result = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (!result) {
            Toast.makeText(getApplicationContext(), "Couldn't register listener for accelerometer", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public void startRecording() {
        // Use http://developer.android.com/training/basics/data-storage/shared-preferences.html
    }

    public void endRecording() {
        // Use http://developer.android.com/training/basics/data-storage/shared-preferences.html
    }
}
