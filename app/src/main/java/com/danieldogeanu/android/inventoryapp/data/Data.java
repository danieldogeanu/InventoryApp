package com.danieldogeanu.android.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.danieldogeanu.android.inventoryapp.Product;
import com.danieldogeanu.android.inventoryapp.R;
import com.danieldogeanu.android.inventoryapp.data.Contract.InventoryEntry;

import java.util.ArrayList;

public class Data {

    private static final String LOG_TAG = Data.class.getSimpleName();

    private static Data INSTANCE = new Data();

    private Data() {}

    public static Data getInstance() {
        return INSTANCE;
    }

    public ArrayList<Product> queryData(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Product> products = new ArrayList<>();

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COL_PRODUCT_NAME,
                InventoryEntry.COL_PRICE,
                InventoryEntry.COL_QUANTITY,
                InventoryEntry.COL_SUPPLIER_NAME,
                InventoryEntry.COL_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        try {
            // Get the index of each column.
            int indexColID = cursor.getColumnIndex(InventoryEntry._ID);
            int indexColProductName = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_NAME);
            int indexColPrice = cursor.getColumnIndex(InventoryEntry.COL_PRICE);
            int indexColQuantity = cursor.getColumnIndex(InventoryEntry.COL_QUANTITY);
            int indexColSupplierName = cursor.getColumnIndex(InventoryEntry.COL_SUPPLIER_NAME);
            int indexColSupplierPhone = cursor.getColumnIndex(InventoryEntry.COL_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor.
            while (cursor.moveToNext()) {
                // Extract values at current column index.
                int currentID = cursor.getInt(indexColID);
                String currentProductName = cursor.getString(indexColProductName);
                int currentPrice = cursor.getInt(indexColPrice);
                int currentQuantity = cursor.getInt(indexColQuantity);
                String currentSupplierName = cursor.getString(indexColSupplierName);
                String currentSupplierPhone = cursor.getString(indexColSupplierPhone);

                // Add the values to the products ArrayList.
                products.add(new Product(currentID, currentProductName, currentPrice, currentQuantity, currentSupplierName, currentSupplierPhone));
            }
        } finally {
            cursor.close();
        }

        return products;
    }

    public void insertData(Context context, Product ...products) {
        // Get the database in write mode.
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get messages for Toasts and Logs.
        String successMsg = context.getResources().getString(R.string.insert_msg_success);
        String errorMsg = context.getResources().getString(R.string.insert_msg_error);

        for (int i = 0; i < products.length; i++) {
            // Get current product.
            Product product = products[i];

            // Extract data from product.
            String productName = product.getProductName();
            int productPrice = product.getProductPrice();
            int productQuantity = product.getProductQuantity();
            String productSupplierName = product.getSupplierName();
            String productSupplierPhone = product.getSupplierPhone();

            // Create the ContentValues object.
            // Column names are the keys, product attributes are the values.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COL_PRODUCT_NAME, productName);
            values.put(InventoryEntry.COL_PRICE, productPrice);
            values.put(InventoryEntry.COL_QUANTITY, productQuantity);
            values.put(InventoryEntry.COL_SUPPLIER_NAME, productSupplierName);
            values.put(InventoryEntry.COL_SUPPLIER_PHONE, productSupplierPhone);

            // Insert a new row in the database, returning the ID of that new row.
            long newRowID = db.insert(InventoryEntry.TABLE_NAME, null, values);

            // Show Toasts and Log the errors.
            if (newRowID != -1) {
                Log.i(LOG_TAG, successMsg + newRowID);
                Toast.makeText(context, successMsg + newRowID, Toast.LENGTH_SHORT).show();
            } else {
                Log.e(LOG_TAG, errorMsg);
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
