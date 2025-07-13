package com.example.techbag;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techbag.Database.RoomDb;
import com.example.techbag.Models.Items;
import com.example.techbag.Constants.MyConstants;
import com.example.techbag.Utils.CaptureAct;
import com.example.techbag.Utils.ShareHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class ScanQRActivity extends AppCompatActivity {

    RoomDb database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = RoomDb.getInstance(this);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Quét mã QR chứa danh sách");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String encodedData = result.getContents();
            try {
                List<Items> sharedItems = ShareHelper.decodeItems(encodedData);
                for (Items item : sharedItems) {
                    item.setChecked(false);
                    item.setAddedby(MyConstants.USER_SMALL);
                    item.setCategory(MyConstants.MY_LIST_CAMEL_CASE);
                    database.mainDao().saveItem(item);
                }
                Toast.makeText(this, "Đã nhập " + sharedItems.size() + " mục từ QR", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "QR không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không có dữ liệu được quét", Toast.LENGTH_SHORT).show();
        }

        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
