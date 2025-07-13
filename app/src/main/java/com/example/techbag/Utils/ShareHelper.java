package com.example.techbag.Utils;
import com.example.techbag.Models.Items;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Base64;
import java.util.List;
public class ShareHelper {
    public static String encodeItems(List<Items> itemsList) {
        Gson gson = new Gson();
        String json = gson.toJson(itemsList);
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    public static List<Items> decodeItems(String encoded) {
        String json = new String(Base64.getDecoder().decode(encoded));
        return new Gson().fromJson(json, new TypeToken<List<Items>>(){}.getType());
    }
}
