package com.example.techbag;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.techbag.Constants.MyConstants;
import com.example.techbag.Database.RoomDb;
import com.example.techbag.Models.Items;
import com.example.techbag.Utils.CaptureAct;
import com.example.techbag.Utils.MyCaptureActivity;
import com.example.techbag.Utils.ShareHelper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.RGBLuminanceSource;

import java.io.IOException;
import java.util.List;

public class ScanQRActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 100;
    RoomDb database;
    Button btnChooseImage, btnToggleFlash, btnBackScan;
    boolean flashEnabled = false;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        database = RoomDb.getInstance(this);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnToggleFlash = findViewById(R.id.btnToggleFlash);
        btnBackScan = findViewById(R.id.btnBackScan);

        btnBackScan.setOnClickListener(v -> finish());

        btnToggleFlash.setOnClickListener(v -> {
            flashEnabled = !flashEnabled;
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Quét mã QR chứa danh sách");
            integrator.setBeepEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.setCaptureActivity(MyCaptureActivity.class);
            integrator.setTorchEnabled(flashEnabled);
            integrator.initiateScan();
        });

        btnChooseImage.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickPhoto);
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            scanQRFromBitmap(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(this, "Không đọc được ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        ensureCameraPermission();
    }
    private void ensureCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
        } else {
            startQRScan();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScan();
            } else {
                Toast.makeText(this,
                        "Camera permission is required to scan QR codes",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    private void startQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Quét mã QR chứa danh sách");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(MyCaptureActivity.class);
        integrator.setTorchEnabled(flashEnabled);
        integrator.initiateScan();
    }

    private void scanQRFromBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bBitmap);
            handleScannedData(result.getText());
        } catch (NotFoundException | ChecksumException | FormatException e) {
            Toast.makeText(this, "Không đọc được mã QR từ ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleScannedData(String encodedData) {
        try {
            List<Items> sharedItems = ShareHelper.decodeItems(encodedData);
            for (Items itm : sharedItems) {
                Items newItem = new Items(itm.getItemname(), MyConstants.MY_LIST_CAMEL_CASE, MyConstants.USER_SMALL, true);
                database.mainDao().saveItem(newItem);
            }
            Toast.makeText(this, "Đã nhập " + sharedItems.size() + " mục từ QR", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "QR không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            handleScannedData(result.getContents());
        } else {
            Toast.makeText(this, "Không có dữ liệu được quét", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
