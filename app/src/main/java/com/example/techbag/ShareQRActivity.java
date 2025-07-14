// Updated ShareQRActivity.java
package com.example.techbag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ShareQRActivity extends AppCompatActivity {

    TextView textView;
    ImageView qrImageView;
    Button copyBtn, saveBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_qr);

        textView = findViewById(R.id.textEncoded);
        qrImageView = findViewById(R.id.qrImage);
        copyBtn = findViewById(R.id.btnCopy);
        saveBtn = findViewById(R.id.btnSave);
        backBtn = findViewById(R.id.btnBack);

        String encodedData = getIntent().getStringExtra("encoded_data");
        if (encodedData != null) {
            textView.setText(encodedData);
            generateQR(encodedData);
        }

        copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("shared_data", textView.getText()));
            Toast.makeText(this, "\u0110\u00e3 sao ch\u00e9p li\u00ean k\u1ebft!", Toast.LENGTH_SHORT).show();
        });

        saveBtn.setOnClickListener(v -> {
            BitmapDrawable drawable = (BitmapDrawable) qrImageView.getDrawable();
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                String savedURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "TechBag_QR", "QR Code for TechBag shared data");
                if (savedURL != null)
                    Toast.makeText(this, "\u0110\u00e3 l\u01b0u v\u00e0o th\u01b0 vi\u1ec7n", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Kh\u00f4ng l\u01b0u \u0111\u01b0\u1ee3c QR", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void generateQR(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 600, 600);
            qrImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Kh\u00f4ng t\u1ea1o \u0111\u01b0\u1ee3c QR", Toast.LENGTH_SHORT).show();
        }
    }
}
