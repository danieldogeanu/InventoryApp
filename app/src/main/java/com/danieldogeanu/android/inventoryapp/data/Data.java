package com.danieldogeanu.android.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.danieldogeanu.android.inventoryapp.Product;
import com.danieldogeanu.android.inventoryapp.R;
import com.danieldogeanu.android.inventoryapp.Utils;
import com.danieldogeanu.android.inventoryapp.data.Contract.TableEntry;

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
                TableEntry._ID,
                TableEntry.COL_PRODUCT_NAME,
                TableEntry.COL_AUTHOR,
                TableEntry.COL_PRICE,
                TableEntry.COL_QUANTITY,
                TableEntry.COL_SUPPLIER_NAME,
                TableEntry.COL_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                TableEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        try {
            // Get the index of each column.
            int indexColID = cursor.getColumnIndex(TableEntry._ID);
            int indexColProductName = cursor.getColumnIndex(TableEntry.COL_PRODUCT_NAME);
            int indexColAuthor = cursor.getColumnIndex(TableEntry.COL_AUTHOR);
            int indexColPrice = cursor.getColumnIndex(TableEntry.COL_PRICE);
            int indexColQuantity = cursor.getColumnIndex(TableEntry.COL_QUANTITY);
            int indexColSupplierName = cursor.getColumnIndex(TableEntry.COL_SUPPLIER_NAME);
            int indexColSupplierPhone = cursor.getColumnIndex(TableEntry.COL_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor.
            while (cursor.moveToNext()) {
                // Extract values at current column index.
                int currentID = cursor.getInt(indexColID);
                String currentProductName = cursor.getString(indexColProductName);
                String currentAuthor = cursor.getString(indexColAuthor);
                int currentPrice = cursor.getInt(indexColPrice);
                int currentQuantity = cursor.getInt(indexColQuantity);
                String currentSupplierName = cursor.getString(indexColSupplierName);
                String currentSupplierPhone = cursor.getString(indexColSupplierPhone);

                // Add the values to the products ArrayList.
                products.add(new Product(currentID, currentProductName, currentAuthor, currentPrice, currentQuantity, currentSupplierName, currentSupplierPhone));
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

        for (Product product : products) {
            // Extract data from product.
            String productName = product.getProductName();
            String productAuthor = product.getProductAuthor();
            float productPrice = product.getProductPrice();
            int productQuantity = product.getProductQuantity();
            String productSupplierName = product.getSupplierName();
            String productSupplierPhone = product.getSupplierPhone();

            // Create the ContentValues object.
            // Column names are the keys, product attributes are the values.
            ContentValues values = new ContentValues();
            values.put(TableEntry.COL_PRODUCT_NAME, productName);
            values.put(TableEntry.COL_AUTHOR, productAuthor);
            values.put(TableEntry.COL_PRICE, productPrice);
            values.put(TableEntry.COL_QUANTITY, productQuantity);
            values.put(TableEntry.COL_SUPPLIER_NAME, productSupplierName);
            values.put(TableEntry.COL_SUPPLIER_PHONE, productSupplierPhone);

            // Insert a new row in the database, returning the ID of that new row.
            long newRowID = db.insert(TableEntry.TABLE_NAME, null, values);

            // Show Toasts and Log the errors.
            if (newRowID != -1) {
                Log.i(LOG_TAG, successMsg + newRowID);
                Utils.showToast(context, successMsg + newRowID);
            } else {
                Log.e(LOG_TAG, errorMsg);
                Utils.showToast(context, errorMsg);
            }
        }

    }

    public void insertDummyData(Context context) {
        Product[] products = new Product[5];

        // Get dummy data string arrays.
        String[] dummyProductTitles = context.getResources().getStringArray(R.array.dummy_prod_titles);
        String[] dummyProductAuthors = context.getResources().getStringArray(R.array.dummy_prod_authors);
        String[] dummyProductPrices = context.getResources().getStringArray(R.array.dummy_prod_prices);
        String[] dummyProductQuantities = context.getResources().getStringArray(R.array.dummy_prod_quantity);
        String[] dummySupplierNames = context.getResources().getStringArray(R.array.dummy_supl_names);
        String[] dummySupplierPhones = context.getResources().getStringArray(R.array.dummy_supl_phones);

        // Extract dummy data for each product.
        for (int i = 0; i < dummyProductTitles.length; i++) {
            products[i] = new Product(
                    dummyProductTitles[i],
                    dummyProductAuthors[i],
                    Float.parseFloat(dummyProductPrices[i]),
                    Integer.parseInt(dummyProductQuantities[i]),
                    dummySupplierNames[i],
                    dummySupplierPhones[i]
            );
        }

        // Insert dummy data into the database.
        if (products.length != 0) {
            insertData(context, products);
        }
    }

}
