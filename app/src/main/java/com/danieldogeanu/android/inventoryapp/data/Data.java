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

/**
 * Singleton class that interfaces between the app and the database.
 */
public class Data {

    // Constant used for debugging.
    private static final String LOG_TAG = Data.class.getSimpleName();

    // Constant used to indicate that no product ID is present.
    private static final int NO_ID = -1;

    // Class instance initialized as soon as possible to be thread-safe.
    private static Data INSTANCE = new Data();

    /**
     * Private constructor, so it can't be accessed from outside.
     * We need only one instance of this class to provide data for the entire app.
     */
    private Data() {}

    /** @return Returns the existing class instance. */
    public static Data getInstance() {
        return INSTANCE;
    }

    /**
     * Method to get all data from the database.
     * @param context The context in which this method is called.
     * @return Returns an ArrayList of Products.
     */
    public ArrayList<Product> getData(Context context) {
        Cursor cursor = queryData(context, NO_ID);
        return extractData(cursor);
    }

    /**
     * Method overload to get a single Product from the database.
     * @param context The context in which this method is called.
     * @param productId The ID of the Product to be retrieved.
     * @return Returns the Product from the database.
     */
    public Product getData(Context context, int productId) {
        Cursor cursor = queryData(context, productId);
        ArrayList<Product> products = extractData(cursor);
        return products.get(0);
    }

    /**
     * Method to query (read) the database and return containing data.
     * @param context The context in which this method is called.
     * @param productId The ID of the Product to be retrieved, if we need a single product.
     * @return Returns a Cursor object with the data retrieved.
     */
    private Cursor queryData(Context context, int productId) {
        // Get the database in read mode.
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        // Select which columns to return from the database.
        String[] projection = {
                TableEntry._ID,
                TableEntry.COL_PRODUCT_NAME,
                TableEntry.COL_AUTHOR,
                TableEntry.COL_PRICE,
                TableEntry.COL_QUANTITY,
                TableEntry.COL_SUPPLIER_NAME,
                TableEntry.COL_SUPPLIER_PHONE
        };

        // Filter the results by ID, if we need a single product.
        String selection = TableEntry._ID + " = ?";
        String[] selectionArgs = { Integer.toString(productId) };

        // Query the database with selected options.
        if (productId > NO_ID) {
            // Query for the case in which we filter by ID.
            cursor = db.query(
                    TableEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
        } else {
            // Query to return all data from database.
            cursor = db.query(
                    TableEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        // Cursor object with data retrieved.
        return cursor;
    }

    /**
     * Method to extract the data from the Cursor object provided by the queryData method.
     * @param cursor The Cursor object to extract data from.
     * @return Returns an ArrayList of Products.
     */
    private ArrayList<Product> extractData(Cursor cursor) {
        // Initialize the Products ArrayList.
        ArrayList<Product> products = new ArrayList<>();

        // Extract data from Cursor.
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
                float currentPrice = cursor.getFloat(indexColPrice);
                int currentQuantity = cursor.getInt(indexColQuantity);
                String currentSupplierName = cursor.getString(indexColSupplierName);
                String currentSupplierPhone = cursor.getString(indexColSupplierPhone);

                // Create a new Product and add the values to the Products ArrayList.
                products.add(new Product(currentID, currentProductName, currentAuthor, currentPrice, currentQuantity, currentSupplierName, currentSupplierPhone));
            }
        } finally {
            // Close the cursor.
            cursor.close();
        }

        // Return the ArrayList of Products.
        return products;
    }

    /**
     * Method to insert data into the database.
     * @param context The context in which this method is called.
     * @param products A Product or an array of Products to insert into the database.
     */
    public void insertData(Context context, Product ...products) {
        // Get the database in write mode.
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get messages for Toasts and Logs.
        String successMsg = context.getResources().getString(R.string.insert_msg_success);
        String successMsgMultiple = context.getResources().getString(R.string.insert_msg_dummy);
        String errorMsg = context.getResources().getString(R.string.insert_msg_error);
        int timesDisplayed = 0;

        // Iterate through all the products and insert them into the database.
        for (Product product : products) {
            // Extract data from the Product object.
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
                if (products.length == 1) {
                    Utils.showToast(context, successMsg + newRowID);
                } else if ((products.length > 1) && (timesDisplayed == 0)) {
                    Utils.showToast(context, successMsgMultiple);
                    timesDisplayed++;
                }
            } else {
                Log.e(LOG_TAG, errorMsg);
                Utils.showToast(context, errorMsg);
            }
        }
    }

    /**
     * Method to insert dummy (demo) data into the database.
     * @param context The context in which this method is called.
     */
    public void insertDummyData(Context context) {
        // Initialize the products array.
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

    /**
     * Method to update the Product data in the database.
     * @param context The context in which this method is called.
     * @param product The Product that we want to update.
     */
    public void updateData(Context context, Product product) {
        // Get the database in write mode.
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get messages for Toasts and Logs.
        String successMsg = context.getResources().getString(R.string.update_msg_success);
        String errorMsg = context.getResources().getString(R.string.update_msg_error);

        // Extract data from the Product object.
        String productId = Integer.toString(product.getProductID());
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

        // Select which row to update, based on the Product ID.
        String selection = TableEntry._ID + " = ?";
        String[] selectionArgs = { productId };

        // Update the existing row in the database, returning the numbers of rows affected.
        int count = db.update(
                TableEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        // Show Toasts and Log the errors.
        if (count != 0) {
            Log.i(LOG_TAG, successMsg);
            Utils.showToast(context, successMsg);
        } else {
            Log.e(LOG_TAG, errorMsg);
            Utils.showToast(context, errorMsg);
        }
    }

    /**
     * Method to delete data from the database.
     * This method is used to delete a single row (product).
     * @param context The context in which this method is called.
     * @param productId The ID of the Product we need to delete.
     */
    public void deleteData(Context context, int productId) {
        // Get the database in write mode.
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get messages for Toasts and Logs.
        String successMsg = context.getResources().getString(R.string.delete_msg_success);
        String errorMsg = context.getResources().getString(R.string.delete_msg_error);

        // Select the row to delete, based on product ID.
        String selection = TableEntry._ID + " = ?";
        String[] selectionArgs = { Integer.toString(productId) };

        // Delete the existing row in the database, returning the number of rows deleted.
        int deleted = db.delete(
                TableEntry.TABLE_NAME,
                selection,
                selectionArgs
        );

        // Show Toasts and Log the errors.
        if (deleted != 0) {
            Log.i(LOG_TAG, successMsg);
            Utils.showToast(context, successMsg);
        } else {
            Log.e(LOG_TAG, errorMsg);
            Utils.showToast(context, errorMsg);
        }
    }

    /**
     * Method to delete all the data from the database.
     * This method is used to drop the current table and create a new one.
     * @param context The context in which this method is called.
     */
    public void deleteAllData(Context context) {
        // Get the database in write mode.
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Drop the old table and create a new one.
        dbHelper.onUpgrade(db, 0, 1);

        // Show Toast and Log the message.
        String deletionMsg = context.getResources().getString(R.string.delete_msg_all);
        Utils.showToast(context, deletionMsg);
        Log.i(LOG_TAG, deletionMsg);
    }

}
