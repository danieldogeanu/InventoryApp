package com.danieldogeanu.android.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.danieldogeanu.android.inventoryapp.Product;
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

}
