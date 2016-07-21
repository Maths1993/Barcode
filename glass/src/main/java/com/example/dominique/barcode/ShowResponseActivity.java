package com.example.dominique.barcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;


public class ShowResponseActivity extends Activity {

    private static final long TIMEOUT = 4 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_response);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onResume() {
        super.onResume();

        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        String value = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        ((TextView) findViewById(R.id.textView)).setTextColor(title.equals( "OK" ) ? Color.WHITE : Color.RED);
        ((TextView) findViewById(R.id.textView)).setText(value);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowResponseActivity.this.finishAffinity();
            }
        }, TIMEOUT);

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finishAffinity(); // collapse Mainactivity as well.
    }

    public static void start(Context context, String status, String value) {
        Intent intent = new Intent(context, ShowResponseActivity.class);
        intent.putExtra(Intent.EXTRA_TITLE, status);
        intent.putExtra(Intent.EXTRA_TEXT, value);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
