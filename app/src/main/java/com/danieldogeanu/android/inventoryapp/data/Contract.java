package com.danieldogeanu.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Static class that defines the table and columns names for the database.
 */
public final class Contract {

    public static abstract class TableEntry implements BaseColumns {

        // The name of the table.
        public static final String TABLE_NAME = "products";

        // The columns names for the table.
        public static final String _ID = BaseColumns._ID;
        public static final String COL_PRODUCT_NAME = "product_name";
        public static final String COL_AUTHOR = "author";
        public static final String COL_PRICE = "price";
        public static final String COL_QUANTITY = "quantity";
        public static final String COL_SUPPLIER_NAME = "supplier_name";
        public static final String COL_SUPPLIER_PHONE = "supplier_phone";

    }

}
