package com.example.techbag.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techbag.Constants.MyConstants;
import com.example.techbag.Database.RoomDb;
import com.example.techbag.Models.Items;
import com.example.techbag.R;

import java.util.List;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListViewHolder> {

    Context context;
    List<Items> itemsList;
    RoomDb database;
    String show;
    private Toast toastMessage; // dùng để huỷ toast trước đó

    public CheckListAdapter() {}

    public CheckListAdapter(Context context, List<Items> itemsList, RoomDb database, String show) {
        this.context = context;
        this.itemsList = itemsList;
        this.database = database;
        this.show = show;

        if (itemsList.size() == 0) {
            Toast.makeText(context.getApplicationContext(), "Danh sách hiện đang trống", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public CheckListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CheckListViewHolder(LayoutInflater.from(context).inflate(R.layout.check_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckListViewHolder holder, int position) {
        Items item = itemsList.get(position);
        holder.checkBox.setText(item.getItemname());
        holder.checkBox.setChecked(item.getChecked());

        if (MyConstants.FALSE_STRING.equals(show)) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_one));
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            if (item.getChecked()) {
                holder.layout.setBackgroundColor(Color.parseColor("#8e546f"));
            } else {
                holder.layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_one));
            }
        }

        holder.checkBox.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            Items currentItem = itemsList.get(currentPosition);
            boolean isChecked = holder.checkBox.isChecked();

            database.mainDao().checkUncheck(currentItem.getID(), isChecked);
            ((Activity) context).setResult(Activity.RESULT_OK);

            if (MyConstants.FALSE_STRING.equals(show)) {
                itemsList = database.mainDao().getAllSelected(true);
                notifyDataSetChanged();
            } else {
                currentItem.setChecked(isChecked);
                notifyItemChanged(currentPosition);

                // cancel toast cũ nếu đang hiển thị
                if (toastMessage != null) toastMessage.cancel();

                String message = isChecked
                        ? "Đã thêm (" + currentItem.getItemname() + ") vào danh sách"
                        : "Đã xóa (" + currentItem.getItemname() + ") khỏi danh sách";

                toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toastMessage.show();
            }
        });

        holder.btnDelete.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            Items currentItem = itemsList.get(currentPosition);

            new AlertDialog.Builder(context)
                    .setTitle("Xoá mục: " + currentItem.getItemname())
                    .setMessage("Bạn có chắc chắn muốn xoá mục này?")
                    .setPositiveButton("Xác nhận", (dialogInterface, i) -> {
                        database.mainDao().delete(currentItem);
                        itemsList.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                    })
                    .setNegativeButton("Huỷ", (dialogInterface, i) ->
                            Toast.makeText(context, "Đã huỷ xoá", Toast.LENGTH_SHORT).show())
                    .setIcon(R.drawable.ic_delete)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}

class CheckListViewHolder extends RecyclerView.ViewHolder {
    LinearLayout layout;
    CheckBox checkBox;
    Button btnDelete;

    public CheckListViewHolder(@NonNull View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.linearLayout);
        checkBox = itemView.findViewById(R.id.checkbox);
        btnDelete = itemView.findViewById(R.id.btnDelete);
    }
}
