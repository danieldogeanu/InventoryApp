package com.danieldogeanu.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.danieldogeanu.android.inventoryapp.data.Contract.TableEntry;

public class Provider extends ContentProvider {

    // Tag for Log messages.
    private static final String LOG_TAG = Provider.class.getSimpleName();

    // Database helper object.
    private DbHelper mDbHelper;

    // URI matcher codes for the content URI.
    private static final int CODE_PRODUCTS = 100; // Full Table
    private static final int CODE_PRODUCT = 101; // Single Item

    // UriMatcher object to match a content URI to a corresponding code.
    // The input passed into the constructor represents the code to return for the root URI.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer that is run the first time anything is called from this class.
    static {
        // Add all the content URI patterns that the provider should recognize.
        // All paths added to the UriMatcher have a corresponding code to return when a match is found.
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_TABLE, CODE_PRODUCTS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_TABLE + "/#", CODE_PRODUCT);
    }

    /** Initialize the provider and the database helper object. */
    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    /** Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order. */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database and declare the cursor.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_PRODUCTS:
                cursor = database.query(TableEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_PRODUCT:
                selection = TableEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(TableEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor.
        return cursor;
    }

    /** Insert new data into the provider with the given ContentValues. */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Figure out if the URI matcher can match the URI to a specific code.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_PRODUCTS:
                // Validate the data before sending it to the database.
                ContentValues validatedValues = validateData(contentValues);

                // Gets the database in write mode.
                SQLiteDatabase database = mDbHelper.getWritableDatabase();

                // Insert a new row for our product in the database, returning the ID of that new row.
                long id = database.insert(TableEntry.TABLE_NAME, null, validatedValues);

                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }

                // Notify all listeners that the data has changed for the product content URI.
                getContext().getContentResolver().notifyChange(uri, null);

                // Return the new URI with the ID (of the newly inserted row) appended at the end.
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /** Returns the MIME type of data for the content URI. */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_PRODUCTS:
                return TableEntry.CONTENT_LIST_TYPE;
            case CODE_PRODUCT:
                return TableEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Method used to validate the data before entering it to the database.
     * @param values The ContentValues to put into the database.
     * @return Returns the validated ContentValues.
     */
    private ContentValues validateData(ContentValues values) {
        // If there's no values, then we don't need to proceed with validation.
        if ((values != null) && (values.size() > 0)) {

            // Check that the product name is not be null or empty.
            if (values.containsKey(TableEntry.COL_PRODUCT_NAME)) {
                String prodName = values.getAsString(TableEntry.COL_PRODUCT_NAME);
                if (prodName == null || prodName.isEmpty()) {
                    throw new IllegalArgumentException("The product requires a name.");
                }
            }

            // Check that the product price doesn't have a negative value or beyond the Float max value.
            if (values.containsKey(TableEntry.COL_PRICE)) {
                Float prodPrice = values.getAsFloat(TableEntry.COL_PRICE);
                if ((prodPrice != null) && (prodPrice < 0 || prodPrice > Float.MAX_VALUE)) {
                    throw new IllegalArgumentException("The product requires a valid price.");
                }
            }

            // Check that the product quantity doesn't have a negative value or beyond the Integer max value.
            if (values.containsKey(TableEntry.COL_QUANTITY)) {
                Integer prodQuantity = values.getAsInteger(TableEntry.COL_QUANTITY);
                if ((prodQuantity != null) && (prodQuantity < 0 || prodQuantity > Integer.MAX_VALUE)) {
                    throw new IllegalArgumentException("The product requires a valid quantity.");
                }
            }

            // Check that the supplier phone is not be null or empty.
            if (values.containsKey(TableEntry.COL_SUPPLIER_PHONE)) {
                String prodSupplPhone = values.getAsString(TableEntry.COL_SUPPLIER_PHONE);
                if (prodSupplPhone == null || prodSupplPhone.isEmpty()) {
                    throw new IllegalArgumentException("The product requires a phone number for the supplier.");
                }
            }
        }

        // If the values are OK, we return them for further processing.
        return values;
    }
}
