package com.example.christian.vappprotoneu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    final int REQUEST_PERMISSION_CAMERA = 1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scan);
        //setupToolbar();

        //Rechte fuer Kamera vorhanden?
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            //Wenn nicht dann Zugriffsrecht auf Kamera anfragen,
            //Erzeugt Benutzerdialog
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA);
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        ArrayList<BarcodeFormat> list = new ArrayList<BarcodeFormat>();
        list.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(list);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    public void goShowContact()//View view)
    {
        Intent intent = new Intent(this, ShowContactActivity.class);
        startActivity(intent);
    }

    public void done(View view)
    {
        finish();
    }

    //@Override
    public void handleResult(Result rawResult) {
        /*new AlertDialog.Builder(this)
                .setTitle("QR Code")
                .setMessage(rawResult.toString())
                .setPositiveButton("OK", null)
                .show();*/
        goShowContact();
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanActivity.this);
            }
        }, 2000);
    }
}
