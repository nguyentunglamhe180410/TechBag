package com.example.techbag.Utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Util {
    public static String removeVietnameseTones(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        str = pattern.matcher(str).replaceAll("");
        str = str.replaceAll("đ", "d");
        str = str.replaceAll("Đ", "D");
        return str;
    }
}
