package com.example.techbag;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techbag.Adapter.Adapter;
import com.example.techbag.Constants.MyConstants;
import com.example.techbag.Data.AppData;
import com.example.techbag.Database.RoomDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    List<String> titles;
    List<Integer> images;
    Adapter adapter;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    RoomDb database;

    private void persistAppData(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        database = RoomDb.getInstance(this);
        AppData appData = new AppData(database);
        int last = prefs.getInt(AppData.LAST_VERSION, 0);
        if(!prefs.getBoolean(MyConstants.FIRST_TIME_CAMEL_CASE, false)){
            appData.persistAllData();
            editor.putBoolean(MyConstants.FIRST_TIME_CAMEL_CASE, true);
            editor.commit();
        } else if (last<AppData.NEW_VERSION) {
            database.mainDao().deleteAllSystemItems(MyConstants.SYSTEM_SMALL);
            appData.persistAllData();
            editor.putInt(AppData.LAST_VERSION, AppData.NEW_VERSION);
            editor.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        recyclerView = findViewById(R.id.recyclerView);
        addAddTitles();
        addAllImages();
        persistAppData();
        database = RoomDb.getInstance(this);
        System.out.println("---------------------------------" + database.mainDao().getAllSelected(false).get(0).getItemname());

        adapter = new Adapter(this, titles, images, MainActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            private static final int TIME_INTERVAL = 2000;
            private long mBackPressed;
            @Override
            public void handleOnBackPressed() {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
                    mBackPressed = System.currentTimeMillis();
                }
            }
        });
    }






    private void addAddTitles(){
        titles = new ArrayList<>();
        titles.add(MyConstants.BASIC_NEEDS_CAMEL_CASE);
        titles.add(MyConstants.CLOTHING_CAMEL_CASE);
        titles.add(MyConstants.PERSONAL_CARE_CAMEL_CASE);
        titles.add(MyConstants. BABY_NEEDS_CAMEL_CASE);
        titles.add(MyConstants.HEALTH_CAMEL_CASE);
        titles.add(MyConstants.TECHNOLOGY_CAMEL_CASE);
        titles.add(MyConstants.FOOD_CAMEL_CASE);
        titles.add(MyConstants.BEACH_SUPPLIES_CAMEL_CASE);
        titles.add(MyConstants.CAR_SUPPLIES_CAMEL_CASE);
        titles.add(MyConstants.NEEDS_CAMEL_CASE);
        titles.add(MyConstants.MY_LIST_CAMEL_CASE);
        titles.add(MyConstants.MY_SELECTIONS_CAMEL_CASE);
    }
    private void addAllImages(){
        images = new ArrayList<>();
        images.add(R.drawable.p1);
        images.add(R.drawable.p2);
        images.add(R.drawable.p3);
        images.add(R.drawable.p4);
        images.add(R.drawable.p5);
        images.add(R.drawable.p6);
        images.add(R.drawable.p7);
        images.add(R.drawable.p8);
        images.add(R.drawable.p9);
        images.add(R.drawable.p10);
        images.add(R.drawable.p11);
        images.add(R.drawable.p12);
    }
}