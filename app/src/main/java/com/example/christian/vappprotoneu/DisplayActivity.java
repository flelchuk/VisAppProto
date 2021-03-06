package com.example.christian.vappprotoneu;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import ezvcard.*;

import java.util.HashMap;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_display);

        QRCodeWriter wr = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        VCard card = (new Profile()).toVCard();

        try {
            int width = 256;
            int height = 256;
            BitMatrix matrix = wr.encode(card.write(), BarcodeFormat.QR_CODE, 256, 256, hints);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ImageView view = (ImageView) findViewById(R.id.imageView);
            view.setImageBitmap(bmp);
        } catch (WriterException e) {
        }
    }

    public void done(View view)
    {
        finish();
    }
}
