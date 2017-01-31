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
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
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
