package com.danieldogeanu.android.inventoryapp;

/**
 * Class that models the Product object.
 */
public class Product {

    // Placeholder constant for the ID.
    private static final int NO_ID = -1;

    // Product Fields
    private int mProductID = NO_ID;
    private String mProductName;
    private String mProductAuthor;
    private float mProductPrice;
    private int mProductQuantity;
    private String mSupplierName;
    private String mSupplierPhone;

    /**
     * The Product object constructor for the cases we have products in the database.
     * @param productID The product ID.
     * @param productName The name of the product.
     * @param productAuthor The product author.
     * @param price The product price.
     * @param quantity How many products we have.
     * @param supplierName The name of the supplier.
     * @param supplierPhone The supplier's phone number.
     */
    public Product(int productID, String productName, String productAuthor, float price, int quantity, String supplierName, String supplierPhone) {
        mProductID = productID;
        mProductName = productName;
        mProductAuthor = productAuthor;
        mProductPrice = price;
        mProductQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
    }

    /**
     * The Product object constructor for new products (we don't have an ID yet).
     * @param productName The name of the product.
     * @param productAuthor The product author.
     * @param price The product price.
     * @param quantity How many products we have.
     * @param supplierName The name of the supplier.
     * @param supplierPhone The supplier's phone number.
     */
    public Product(String productName, String productAuthor, float price, int quantity, String supplierName, String supplierPhone) {
        mProductName = productName;
        mProductAuthor = productAuthor;
        mProductPrice = price;
        mProductQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
    }

    /** @return Returns the Product ID. */
    public int getProductID() {
        return mProductID;
    }

    /** @return Returns the Product name. */
    public String getProductName() {
        return mProductName;
    }

    /** @return Returns the Product author. */
    public String getProductAuthor() {
        return mProductAuthor;
    }

    /** @return Returns the Product price. */
    public float getProductPrice() {
        return mProductPrice;
    }

    /** @return Returns the quantity of Product(s). */
    public int getProductQuantity() {
        return mProductQuantity;
    }

    /** @return Returns the supplier name for this Product. */
    public String getSupplierName() {
        return mSupplierName;
    }

    /** @return Returns the supplier's phone number. */
    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    /**
     * Overrides the toString method for debugging purposes.
     * @return Returns a concatenated string with all the fields contents.
     */
    @Override
    public String toString() {
        return "Product {" +
                "mProductID=" + mProductID + ", " +
                "mProductName='" + mProductName + "', " +
                "mProductAuthor='" + mProductAuthor + "', " +
                "mProductPrice=" + mProductPrice + ", " +
                "mProductQuantity=" + mProductQuantity + ", " +
                "mSupplierName='" + mSupplierName + "', " +
                "mSupplierPhone='" + mSupplierPhone + "'}";
    }
}
