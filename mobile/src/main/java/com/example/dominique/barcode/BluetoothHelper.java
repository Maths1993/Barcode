package com.example.dominique.barcode;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.motioncoding.firebaseserver.FirebaseServer;

import java.util.UUID;

/**
 * Created by paulgavrikov on 17/06/16.
 */
public class BluetoothHelper {

    private static final String TAG = "BT-Helper";
    private final String TARGET_MAC = "";

    private static BluetoothHelper mObject = null;
    private Context context;
    private BluetoothGattCharacteristic mReadCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;


    private boolean isSystemReady = false;
    private boolean isScanningQueued = false;

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    private android.bluetooth.BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.e(TAG, "onConnectionStateChange " + status + " " + newState);

            if(newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                gatt.discoverServices();
            } else {
                isSystemReady = false;
                gatt.connect();
            }


        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            Log.e(TAG, "onServicesDiscovered " + status);

            if(status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService service : gatt.getServices()) {
                    Log.e(TAG, "service " + service.getUuid());
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        Log.e(TAG, "characteristic " + characteristic.getUuid());
                        if(characteristic.getUuid().toString().equals("00002221-0000-1000-8000-00805f9b34fb"))
                            mReadCharacteristic = characteristic;
                        else if(characteristic.getUuid().toString().equals("00002222-0000-1000-8000-00805f9b34fb"))
                            mWriteCharacteristic = characteristic;
                    }
                }

                registerNotification(gatt, mReadCharacteristic);
            } else
                gatt.discoverServices();

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, "onCharacteristicWrite " + status);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            String string = new String(characteristic.getValue());
            string = string.substring(0, string.length()-1);
            Log.e(TAG, "onCharacteristicChanged " + string);

            new AsyncTask<String, Void, String>() {

                @Override
                protected String doInBackground(String... params) {
                    return new GTINDatabase().query(params[0]);
                }

                @Override
                protected void onPostExecute(String s) {
                    Log.e(TAG, s);
                    super.onPostExecute(s);
                    new FirebaseServer("AIzaSyCXmt761UPr1z3DvHDY2t9Sfrne4lEnsD4")
                            .sendDataToTopic("glass", FirebaseServer.stringToMap("cmd", "SCAN_RESPONSE",
                                    "status", "OK", "value", s));
                }
            }.execute(string);

        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.e(TAG, "notification ready");
            isSystemReady = true;
            if(isScanningQueued)
                sendScanCommand();
        }

    };

    private void registerNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        gatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    private BluetoothGatt gatt;

    public static BluetoothHelper getInstance(Context context) {
        if(mObject == null) {
            mObject = new BluetoothHelper(context);
        }
        return mObject;
    }

    private BluetoothHelper(Context context) {
        this.context = context;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.enable();
    }

    public void startBarcodeScan() {
        if(!isSystemReady)
            isScanningQueued = true;
        else {
            isScanningQueued = false;
            sendScanCommand();
        }
    }

    private void sendScanCommand() {
        mWriteCharacteristic.setValue(new byte[]{0x01});
        boolean state = gatt.writeCharacteristic(mWriteCharacteristic);
        Log.e(TAG, "immediate state "+state);
    }

    public void connect() {
        if(isSystemReady)
            return;
        BluetoothDevice device = getBluetoothScanner();
        gatt = device.connectGatt(context, true, mGattCallback);
    }

    public void destroy() {
        gatt.disconnect();
        gatt.close();
    }

    private BluetoothDevice getBluetoothScanner() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.enable();
        for(BluetoothDevice device : adapter.getBondedDevices()) {
            if(device.getName().startsWith("BluetoothScann")) {
                return device;
            }
        }

        return null;
    }
}
