package com.danieldogeanu.android.inventoryapp;

public class Product {

    private static final int NO_ID = -1;

    private int mProductID = NO_ID;
    private String mProductName;
    private int mProductPrice;
    private int mProductQuantity;
    private String mSupplierName;
    private String mSupplierPhone;

    public Product(int productID, String productName, int price, int quantity, String supplierName, String supplierPhone) {
        mProductID = productID;
        mProductName = productName;
        mProductPrice = price;
        mProductQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
    }

    public Product(String productName, int price, int quantity, String supplierName, String supplierPhone) {
        mProductName = productName;
        mProductPrice = price;
        mProductQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
    }

    public int getProductID() {
        return mProductID;
    }

    public String getProductName() {
        return mProductName;
    }

    public int getProductPrice() {
        return mProductPrice;
    }

    public int getProductQuantity() {
        return mProductQuantity;
    }

    public String getSupplierName() {
        return mSupplierName;
    }

    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    public boolean hasID() {
        return mProductID > NO_ID;
    }

    @Override
    public String toString() {
        return "Product {" +
                "mProductID=" + mProductID + ", " +
                "mProductName='" + mProductName + "', " +
                "mProductPrice=" + mProductPrice + ", " +
                "mProductQuantity=" + mProductQuantity + ", " +
                "mSupplierName='" + mSupplierName + "', " +
                "mSupplierPhone='" + mSupplierPhone + "'}";
    }
}
