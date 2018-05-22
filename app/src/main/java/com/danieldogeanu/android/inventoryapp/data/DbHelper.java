package com.danieldogeanu.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.danieldogeanu.android.inventoryapp.data.Contract.InventoryEntry;

public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "inventory.db";

    // SQL for table creation and deletion.
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                    InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    InventoryEntry.COL_PRODUCT_NAME + " TEXT NOT NULL," +
                    InventoryEntry.COL_PRICE + " INTEGER," +
                    InventoryEntry.COL_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                    InventoryEntry.COL_SUPPLIER_NAME + " TEXT," +
                    InventoryEntry.COL_SUPPLIER_PHONE + " TEXT NOT NULL)";
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
