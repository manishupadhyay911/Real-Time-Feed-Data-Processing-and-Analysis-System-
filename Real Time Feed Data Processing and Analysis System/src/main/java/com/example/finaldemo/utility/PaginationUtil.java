package com.example.finaldemo.utility;

import java.util.Base64;

public class PaginationUtil {

    public static String encode(String original) {
        return Base64.getEncoder().encodeToString(original.getBytes());
    }

    public static String decode(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString));
    }

    private PaginationUtil() {
    }
}
