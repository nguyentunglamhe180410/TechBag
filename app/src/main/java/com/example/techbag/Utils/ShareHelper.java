package com.example.techbag.Utils;
import com.example.techbag.Models.Items;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ShareHelper {
    public static String encodeItems(List<Items> itemsList) throws IOException {
        // 1) Turn your list into JSON bytes
        String json = new Gson().toJson(itemsList);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

        // 2) GZIP-compress
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
            gos.write(jsonBytes);
        }
        byte[] compressed = baos.toByteArray();

        // 3) URL-safe Base64 without padding
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(compressed);
    }

    public static List<Items> decodeItems(String encoded) throws IOException {
        // 1) URL-safe Base64 decode
        byte[] compressed = Base64.getUrlDecoder().decode(encoded);

        // 2) GZIP-decompress with manual read-loop
        ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
        String json;
        try (GZIPInputStream gis = new GZIPInputStream(bais)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] tmp = new byte[4096];
            int n;
            while ((n = gis.read(tmp)) != -1) {
                buffer.write(tmp, 0, n);
            }
            json = buffer.toString(StandardCharsets.UTF_8.name());
        }

        // 3) Back into your list
        return new Gson().fromJson(
                json, new TypeToken<List<Items>>(){}.getType()
        );
    }

}
