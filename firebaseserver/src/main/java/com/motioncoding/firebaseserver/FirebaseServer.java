package com.motioncoding.firebaseserver;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by paulgavrikov on 15/06/16.
 */
public class FirebaseServer {

    private String mAuthKey;
    private final String FCM_BASE = "https://gcm-http.googleapis.com/gcm/send";

    public FirebaseServer(String authKey) {
        mAuthKey = authKey;
    }


    public void sendNotificationToTopic(String topic, String title, String message) {

        HashMap<String, Object> map = stringToMap("to", "/topics/" + topic);
        HashMap<String, Object> data = stringToMap("title", title, "body", message);
        map.put("notification", data);
        JSONObject json = new JSONObject(map);
        send(json.toString());

    }


    public void sendDataToTopic(String topic, HashMap<String, Object> data) {
        HashMap<String, Object> map = stringToMap("to", "/topics/" + topic);
        map.put("data", data);
        JSONObject json = new JSONObject(map);
        send(json.toString());
    }

    /**
     * Folds a sequence of Strings into a Hashmap. Each even string is a key, and it's
     * successor - and thus odd - value is the corresponding value.
     *
     * @param items
     * @return
     */
    public static HashMap<String, Object> stringToMap(String... items) {
        if (items.length % 2 != 0)
            throw new Error("Missing value");

        HashMap<String, Object> map = new HashMap<>();
        for (int x = 0; x < items.length; x += 2) {
            map.put(items[x], items[x + 1]);
        }

        return map;
    }

    /**
     * Sends the data to the FCM server. Assumes a valid authkey.
     *
     * @param data the HTTP Body
     */
    private void send(final String data) {

        Log.e("json", data);

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, data);
                Request request = new Request.Builder()
                        .url("https://gcm-http.googleapis.com/gcm/send")
                        .post(body)
                        .addHeader("authorization", "key=" + mAuthKey)
                        .addHeader("content-type", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    response.close();
                } catch (IOException e) {
                    Log.e("FCM-ERROR", e.toString());
                }
                return null;
            }
        }.execute();


    }
}
