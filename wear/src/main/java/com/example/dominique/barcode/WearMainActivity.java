package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class WearMainActivity extends Activity implements SensorEventListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private static final String TAG = "TAG";

    public static final int CONNECTION_FAIL = 1;
    public static final int NO_TARGETS = 2;
    public static final int ALL_RECEIVED  = 3;
    public static final int NOT_ALL_RECEIVED = 4;
    public static final int CONNECTION_SUSPEND = 5;
    public static final int requestCode = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Button button;
    private boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_main);

        button = (Button) findViewById(R.id.button_gesture);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long oldTime = 0;
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Code for sampling data comes here
            float x_acc = event.values[0];
            float y_acc = event.values[1];
            float z_acc = event.values[2];
            long actualTime = event.timestamp/1000000;

            // Write values to console
            Log.e(TAG, "x: "+x_acc+" y: "+y_acc+" z: "+z_acc+" time: "+ (actualTime));

            if(gestureRecognized()) {
                button.setText("START GESTURE RECOGNITION");
                sensorManager.unregisterListener(this, accelerometer);
                sendToPhone();
            }
        }
    }

    public boolean gestureRecognized() {
        // TODO: Code for gesture recognition
        return true;
    }

    public void sendToPhone() {
        Intent requestIntent = new Intent(this, SendToPhone.class);
        startActivityForResult(requestIntent, requestCode);
    }

    @Override
    public void onActivityResult(int receivedCode, int resultCode, Intent data) {
        if(receivedCode == requestCode) {
            if(resultCode == CONNECTION_FAIL) {
                Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
            }
            if(resultCode == CONNECTION_SUSPEND) {
                Toast.makeText(getApplicationContext(), "Connection suspended", Toast.LENGTH_LONG).show();
            }
            if(resultCode == NO_TARGETS) {
                Toast.makeText(getApplicationContext(), "No target device." +
                        "Make sure watch is paired with target devices", Toast.LENGTH_LONG).show();
            }
            if(resultCode == ALL_RECEIVED) {
                Toast.makeText(getApplicationContext(), "All targets received the signal", Toast.LENGTH_LONG).show();
            }
            if(resultCode == NOT_ALL_RECEIVED) {
                Toast.makeText(getApplicationContext(), "Some targets didn't receive the signal", Toast.LENGTH_LONG).show();
            }
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
        if(active) {
            sensorManager.unregisterListener(this, accelerometer);
            active = false;
        }
    }
}