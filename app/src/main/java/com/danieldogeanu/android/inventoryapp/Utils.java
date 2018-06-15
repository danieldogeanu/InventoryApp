package com.danieldogeanu.android.inventoryapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * General utility methods used across the app.
 */
public final class Utils {

    /** Private constructor, so we can't instantiate the class. */
    private Utils() {}

    /**
     * Method that displays short Toast messages in the app.
     * @param context The context from which this method is called.
     * @param message The message to show in the Toast.
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method that displays short Toast and logs messages to the console.
     * @param context The context from which this method is called.
     * @param error Flag to show if it's an error or not.
     * @param tag The tag title for the Logs.
     * @param message The message to show in the Toast and Log to the console.
     */
    public static void showToastAndLog(Context context, boolean error, String tag, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        if (error) Log.e(tag, message);
        else Log.i(tag, message);
    }

    /**
     * Method to format the price, according to the user's locale.
     * It allows only two decimal digits after the point and adds currency symbol.
     * Please don't use this method to insert the price into the database!
     * @param rawPrice The raw price received from the database.
     * @return Returns the formatted price.
     */
    public static String formatPrice(float rawPrice) {
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(Currency.getInstance(locale));
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(rawPrice);
    }

    /**
     * Method to format the quantity, according to the user's locale.
     * Please don't use this method to insert the quantity into the database!
     * @param rawQuantity The raw quantity received from the database.
     * @return Returns the formatted quantity.
     */
    public static String formatQuantity(float rawQuantity) {
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
        return numberFormat.format(rawQuantity);
    }

}
