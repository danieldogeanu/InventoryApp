package com.danieldogeanu.android.inventoryapp;

public class Product {

    private String mProductName;
    private int mProductPrice;
    private int mProductQuantity;
    private String mSupplierName;
    private String mSupplierPhone;

    public Product(String productName, int price, int quantity, String supplierName, String supplierPhone) {
        mProductName = productName;
        mProductPrice = price;
        mProductQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
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

    @Override
    public String toString() {
        return "Product {" +
                "mProductName='" + mProductName + "', " +
                "mProductPrice=" + mProductPrice + ", " +
                "mProductQuantity=" + mProductQuantity + ", " +
                "mSupplierName='" + mSupplierName + "', " +
                "mSupplierPhone='" + mSupplierPhone + "'}";
    }
}
