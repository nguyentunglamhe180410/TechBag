package com.example.techbag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techbag.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ShareQRActivity extends AppCompatActivity {

    TextView textView;
    ImageView qrImageView;
    Button copyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_qr);

        textView = findViewById(R.id.textEncoded);
        qrImageView = findViewById(R.id.qrImage);
        copyBtn = findViewById(R.id.btnCopy);

        String encodedData = getIntent().getStringExtra("encoded_data");
        if (encodedData != null) {
            textView.setText(encodedData);
            generateQR(encodedData);
        }

        copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("shared_data", textView.getText()));
            Toast.makeText(this, "Đã sao chép liên kết!", Toast.LENGTH_SHORT).show();
        });
    }

    private void generateQR(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 600, 600);
            qrImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không tạo được QR", Toast.LENGTH_SHORT).show();
        }
    }
}
