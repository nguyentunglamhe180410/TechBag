package com.example.techbag;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import androidx.appcompat.app.ActionBar;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.techbag.Adapter.CheckListAdapter;
import com.example.techbag.Constants.MyConstants;
import com.example.techbag.Data.AppData;
import com.example.techbag.Database.RoomDb;
import com.example.techbag.Models.Items;
import com.example.techbag.Utils.ShareHelper;
import com.example.techbag.Utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckList extends AppCompatActivity {
    private static final int REQUEST_SCAN_QR = 200;

    RecyclerView recyclerView;
    CheckListAdapter checkListAdapter;
    RoomDb database;
    List<Items> itemsList = new ArrayList<>();
    String header;
    String show;
    EditText txtAdd;
    Button btnAdd;
    LinearLayout linearLayout;
    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one, menu);
        if (MyConstants.MY_SELECTIONS.equals(header)) {
            menu.getItem(0).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
        } else if (MyConstants.MY_LIST_CAMEL_CASE.equals(header)) {
            menu.getItem(1).setVisible(false);
        }
        MenuItem menuItem = menu.findItem(R.id.btnSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Items> finalList = new ArrayList<>();
                String query = Util.removeVietnameseTones(newText.toLowerCase());

                for (Items item : itemsList) {
                    String itemName = Util.removeVietnameseTones(item.getItemname().toLowerCase());
                    if (itemName.contains(query)) {
                        finalList.add(item);
                    }
                }
                updateRecycler(finalList);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, CheckList.class);
        AppData appData = new AppData(database, this);

        if (item.getItemId() == R.id.btnMySelection) {
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_SELECTIONS_CAMEL_CASE);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.TRUE_STRING);
            activityResultLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.btnCustomList) {
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_LIST_CAMEL_CASE);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.TRUE_STRING);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.btnDeleteDefault) {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa tất cả dữ liệu hệ thống")
                    .setMessage("Bạn có chắc chắn không?" +
                            "\n\nLàm điều này sẽ xóa tất cả dữ liệu hệ thống ở (" + header + ") " +
                            "\nNhững mục bạn thêm vào sẽ không bị xóa đi")
                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            appData.persistDataByCategory(header, true);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);
                        }
                    }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                        }
                    }).setIcon(R.drawable.ic_warning)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.btnReset) {
            new AlertDialog.Builder(this)
                    .setTitle("Khôi phục dữ liệu gốc")
                    .setMessage("Bạn có chắc chắn không?\n\nLàm điều này sẽ cài lại tất cả dữ liệu hệ thống" +
                            "và xóa tất cả dữ liệu cá nhân của bạn ở (" + header + ") ")
                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            appData.persistDataByCategory(header, false);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);
                        }
                    }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                        }
                    }).setIcon(R.drawable.ic_warning)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.btnSourceCode) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/nguyentunglamhe180410/TechBag"));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.btnExit) {
            this.finishAffinity();
            Toast.makeText(this, "Tech bag\nThoát thành công", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.btnShareData) {
            List<Items> selectedItems;
            if (isMySelectionCategory()) {
                selectedItems = database.mainDao().getAllSelected(true);
            } else {
                selectedItems = new ArrayList<>();
                for (Items i : itemsList) {
                    if (Boolean.TRUE.equals(i.getChecked())) {
                        selectedItems.add(i);
                    }
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Không có mục nào được chọn để chia sẻ", Toast.LENGTH_SHORT).show();
                return true;
            }

            try {
                String encodedData = ShareHelper.encodeItems(selectedItems);
                Intent shareIntent = new Intent(this, ShareQRActivity.class);
                shareIntent.putExtra("encoded_data", encodedData);
                startActivity(shareIntent);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,
                        "Lỗi khi tạo dữ liệu chia sẻ, vui lòng thử lại",
                        Toast.LENGTH_SHORT
                ).show();
            }
            return true;
        } else if (item.getItemId() == R.id.btnImportData) {
            EditText input = new EditText(this);
            input.setHint("Dán chuỗi chia sẻ vào đây...");

            new AlertDialog.Builder(this)
                    .setTitle("Nhập dữ liệu chia sẻ")
                    .setView(input)
                    .setPositiveButton("Xác nhận", (dialog, which) -> {
                        String data = input.getText().toString().trim();
                        try {
                            List<Items> importedItems = ShareHelper.decodeItems(data);
                            int count = 0;
                            for (Items itm : importedItems) {
                                Items newItem = new Items(itm.getItemname(),MyConstants.MY_LIST_CAMEL_CASE,MyConstants.USER_SMALL, true);
                                database.mainDao().saveItem(newItem);
                                count++;
                            }
                            Toast.makeText(this, "Đã nhập " + count + " mục từ chia sẻ", Toast.LENGTH_SHORT).show();
                            if (isMySelectionCategory()) {
                                itemsList = database.mainDao().getAllSelected(true);
                            } else {
                                itemsList = database.mainDao().getAll(header);
                            }
                            updateRecycler(itemsList);
                        } catch (Exception e) {
                            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Huỷ", null)
                    .setIcon(R.drawable.ic_import)
                    .show();

            return true;
        }
        else if (item.getItemId() == R.id.btnScanQR) {
            Intent scan = new Intent(this, ScanQRActivity.class);
            startActivityForResult(scan, REQUEST_SCAN_QR);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN_QR && resultCode == RESULT_OK) {
            header = MyConstants.MY_LIST_CAMEL_CASE;
            getSupportActionBar().setTitle(header);
            itemsList = database.mainDao().getAll(header);
            updateRecycler(itemsList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Handle the result here
                        Intent data = result.getData();
                        itemsList = database.mainDao().getAll(header);
                        updateRecycler(itemsList);
                    }
                }
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        header = intent.getStringExtra(MyConstants.HEADER_SMALL);
        show = intent.getStringExtra(MyConstants.SHOW_SMALL);

        getSupportActionBar().setTitle(header);

        txtAdd = findViewById(R.id.txtAdd);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayout = findViewById(R.id.linearLayout);

        database = RoomDb.getInstance(this);

        if (isMySelectionCategory()) {
            // If it is "Lựa chọn của tôi", show only selected items
            itemsList = database.mainDao().getAllSelected(true);
            linearLayout.setVisibility(View.GONE); // Hide "add" bar
        } else {
            if (MyConstants.FALSE_STRING.equals(show)) {
                // This is likely for sub-screens where only checked items are shown (optional)
                itemsList = database.mainDao().getAllSelected(true);
                linearLayout.setVisibility(View.GONE);
            } else {
                itemsList = database.mainDao().getAll(header);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
        updateRecycler(itemsList);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = txtAdd.getText().toString();
                if (itemName != null && !itemName.isEmpty()) {
                    addNewItem(itemName);
                    Toast.makeText(CheckList.this, "Vật phẩm đã được thêm vào", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CheckList.this, "Không có gì để thêm.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addNewItem(String itemName) {
        Items item = new Items();
        item.setChecked(false);
        item.setCategory(header);
        item.setItemname(itemName);
        item.setAddedby(MyConstants.USER_SMALL);

        database.mainDao().saveItem(item);
        itemsList = database.mainDao().getAll(header);
        updateRecycler(itemsList);
        recyclerView.scrollToPosition(checkListAdapter.getItemCount() - 1);
        txtAdd.setText("");
    }


    private void updateRecycler(List<Items> itemsList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        checkListAdapter = new CheckListAdapter(CheckList.this, itemsList, database, show);
        recyclerView.setAdapter(checkListAdapter);
    }

    private boolean isMySelectionCategory() {
        return MyConstants.MY_SELECTIONS_CAMEL_CASE.equals(header);
    }

}