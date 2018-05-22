package com.danieldogeanu.android.inventoryapp.data;

public class InventoryData {

    private static final String LOG_TAG = InventoryData.class.getSimpleName();

    private static InventoryData INSTANCE = new InventoryData();

    private InventoryData() {}

    public static InventoryData getInstance() {
        return INSTANCE;
    }

}
