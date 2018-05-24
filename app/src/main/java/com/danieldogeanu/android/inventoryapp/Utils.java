package com.danieldogeanu.android.inventoryapp;

import android.content.Context;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public final class Utils {

    private Utils() {}

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String formatPrice(float rawPrice) {
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(Currency.getInstance(locale));
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(rawPrice);
    }

    public static String formatQuantity(float rawQuantity) {
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
        return numberFormat.format(rawQuantity);
    }

}
