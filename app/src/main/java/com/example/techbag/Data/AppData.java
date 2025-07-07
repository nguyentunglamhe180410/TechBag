package com.example.techbag.Data;

import android.app.Application;
import android.content.Context;

import com.example.techbag.Database.RoomDb;
import com.example.techbag.Models.Items;
import com.example.techbag.Constants.MyConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData extends Application {
    RoomDb database;
    String category;
    Context context;
    public static final String LAST_VERSION = "LAST_VERSION";
    public static final int NEW_VERSION = 1;

    public AppData(RoomDb database) {
        this.database = database;
    }

    public AppData(RoomDb database, Context context) {
        this.database = database;
        this.context = context;
    }
    public List<Items> getBasicNeedsData() {
        String[] data = {"Nước uống đóng chai", "Thức ăn khô", "Đèn pin", "Bộ sơ cứu cá nhân", "Giấy vệ sinh", "Khăn giấy ướt", "Bật lửa hoặc diêm", "Túi nylon đa năng", "Khẩu trang y tế", "Xà phòng hoặc gel rửa tay"};
        return prepareItemsList(MyConstants.BASIC_NEEDS_CAMEL_CASE, data);
    }

    public List<Items> getClothingData() {
        String[] data = {"Áo khoác gió", "Quần dài", "Mũ nón che nắng", "Giày thể thao", "Áo mưa bộ", "Găng tay", "Vớ tất", "Đồ lót", "Áo thun", "Quần short"};
        return prepareItemsList(MyConstants.CLOTHING_CAMEL_CASE, data);
    }

    public List<Items> getPersonalCareData() {
        String[] data = {"Bàn chải đánh răng", "Kem đánh răng", "Nước súc miệng", "Lược chải tóc", "Kem dưỡng da", "Khăn mặt", "Sữa rửa mặt", "Sữa tắm", "Dầu gội", "Kem chống nắng"};
        return prepareItemsList(MyConstants.PERSONAL_CARE_CAMEL_CASE, data);
    }

    public List<Items> getBabyNeedsData() {
        String[] data = {"Tã bỉm", "Sữa công thức", "Đồ chơi mềm", "Khăn ướt cho bé", "Bình sữa", "Quần áo cho bé", "Chăn nhẹ", "Nón che thóp", "Kem chống hăm", "Dụng cụ hút mũi"};
        return prepareItemsList(MyConstants.BABY_NEEDS_CAMEL_CASE, data);
    }

    public List<Items> getHealthData() {
        String[] data = {"Thuốc hạ sốt", "Nhiệt kế điện tử", "Khẩu trang y tế", "Dung dịch sát khuẩn", "Băng gạc y tế", "Thuốc cảm cúm", "Thuốc tiêu chảy", "Cồn y tế", "Găng tay y tế", "Vitamin tổng hợp"};
        return prepareItemsList(MyConstants.HEALTH_CAMEL_CASE, data);
    }

    public List<Items> getTechnologyData() {
        String[] data = {"Điện thoại di động", "Sạc dự phòng", "Laptop hoặc máy tính bảng", "Tai nghe", "Cáp sạc đa năng", "Ổ cứng di động", "Pin dự phòng", "Đồng hồ thông minh", "Bộ phát WiFi", "Chuột không dây"};
        return prepareItemsList(MyConstants.TECHNOLOGY_CAMEL_CASE, data);
    }

    public List<Items> getFoodData() {
        String[] data = {"Bánh mì khô", "Trái cây tươi", "Nước đóng chai", "Đồ ăn liền", "Bánh quy dinh dưỡng", "Sữa hộp", "Thịt hộp", "Mì gói", "Snack khô", "Trái cây sấy"};
        return prepareItemsList(MyConstants.FOOD_CAMEL_CASE, data);
    }

    public List<Items> getBeachSuppliesData() {
        String[] data = {"Kính bơi", "Kem chống nắng", "Khăn tắm biển", "Đồ bơi", "Dép kẹp", "Nón rộng vành", "Túi chống nước", "Kính râm", "Chiếu hoặc ghế xếp", "Phao bơi"};
        return prepareItemsList(MyConstants.BEACH_SUPPLIES_CAMEL_CASE, data);
    }

    public List<Items> getCarSuppliesData() {
        String[] data = {"Bộ dụng cụ sửa xe", "Bơm lốp mini", "Dây câu bình", "Khăn lau xe", "Nước làm mát", "Bản đồ hoặc GPS offline", "Đèn cảnh báo khẩn cấp", "Lốp dự phòng", "Bộ kích điện", "Áo phản quang"};
        return prepareItemsList(MyConstants.CAR_SUPPLIES_CAMEL_CASE, data);
    }

    public List<Items> getNeedsData() {
        String[] data = {"Tiền mặt", "CMND/CCCD/Hộ chiếu", "Chìa khóa nhà/xe", "Sạc điện thoại", "Thẻ ATM", "Bút viết", "Sổ tay ghi chú", "Thẻ bảo hiểm y tế", "Mã QR cá nhân", "Ví hoặc túi đựng đồ cá nhân"};
        return prepareItemsList(MyConstants.NEEDS_CAMEL_CASE, data);
    }

    public List<Items> prepareItemsList(String category, String[] data){
        List<String> list = Arrays.asList(data);
        List<Items> dataList = new ArrayList<>();
        dataList.clear();
        for(int i = 0; i< list.size();i++){
            dataList.add(new Items(list.get(i),category,false));
        }
        return dataList;
    }
    public List<List<Items>> getAllData() {
        List<List<Items>> listOfAllItems = new ArrayList<>();
        listOfAllItems.clear();

        listOfAllItems.add(getBasicNeedsData());
        listOfAllItems.add(getClothingData());
        listOfAllItems.add(getPersonalCareData());
        listOfAllItems.add(getBabyNeedsData());
        listOfAllItems.add(getHealthData());
        listOfAllItems.add(getTechnologyData());
        listOfAllItems.add(getFoodData());
        listOfAllItems.add(getBeachSuppliesData());
        listOfAllItems.add(getCarSuppliesData());
        listOfAllItems.add(getNeedsData());

        return listOfAllItems;
    }
    public void persistAllData(){
        List<List<Items>> listOfAllItems = getAllData();
        for(List<Items> list : listOfAllItems){
            for(Items items : list){
                database.mainDao().saveItem(items);
            }
        }
        System.out.println("Data added.");
    }
}
