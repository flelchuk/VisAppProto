package com.example.christian.vappprotoneu;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {
    public static int randomID = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        randomID = new Random(Calendar.getInstance().getTimeInMillis()).nextInt(1000);
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(String.format("Ihre Benutzer-ID: %d", randomID));
    }

    public void goScan(View view)
    {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    public void goDisplay(View view)
    {
        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }
}
