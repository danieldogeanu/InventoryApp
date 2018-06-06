package com.danieldogeanu.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
