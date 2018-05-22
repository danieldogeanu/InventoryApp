package com.danieldogeanu.android.inventoryapp.data;

public class Data {

    private static final String LOG_TAG = Data.class.getSimpleName();

    private static Data INSTANCE = new Data();

    private Data() {}

    public static Data getInstance() {
        return INSTANCE;
    }

}
