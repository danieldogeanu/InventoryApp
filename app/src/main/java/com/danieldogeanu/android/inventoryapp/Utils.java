package com.danieldogeanu.android.inventoryapp;

import android.content.Context;
import android.widget.Toast;

public final class Utils {

    private Utils() {}

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
