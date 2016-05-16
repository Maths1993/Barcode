package com.example.wearrecognition;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class GestureRecognition extends WearableActivity implements SensorEventListener {

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
            String x = Float.toString(event.values[0]);
            String y = Float.toString(event.values[1]);
            String z = Float.toString(event.values[2]);
            writeToFile(x,y,z);
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
            if(accelerometerAccessible()) button.setText("END RECORDING");
        } else {
            sensorManager.unregisterListener(this, accelerometer);
            endRecording();
            button.setText("START RECORDING");
        }
        active = !active;
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

    public void writeToFile(String x, String y, String z) {
        try {
            Context context = getApplicationContext();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("test.txt", Context.MODE_APPEND));
            outputStreamWriter.write(x);Log.w("TAG", "write...");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void endRecording() {
        // Use http://developer.android.com/training/basics/data-storage/shared-preferences.html
    }
}
