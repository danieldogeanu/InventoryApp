package com.danieldogeanu.android.inventoryapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.danieldogeanu.android.inventoryapp.data.Data;

/**
 * Activity class that allows the user to create a new product, or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    // Data Holder
    private Data mData;

    // EditText Fields
    private EditText mProductNameEditText;
    private EditText mProductAuthorEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    // Existing Product ID
    private static final int NO_ID = -1;
    private int mExistingProductID = NO_ID;

    /**
     * Overrides the onCreate method to assemble and display the Editor Activity.
     * @param savedInstanceState The saved instance state Bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get Data Class Instance
        mData = Data.getInstance();

        // Get EditText Fields
        mProductNameEditText = findViewById(R.id.product_name);
        mProductAuthorEditText = findViewById(R.id.product_author);
        mProductPriceEditText = findViewById(R.id.product_price);
        mProductQuantityEditText = findViewById(R.id.product_quantity);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        // Check if it's an existing product and pre-populate the EditText fields.
        if (getIntent().hasExtra("product_id")) {
            mExistingProductID = (int) getIntent().getSerializableExtra("product_id");
            openExistingProduct(mExistingProductID);
        }

        // Save or update the product and exit the Editor Activity.
        FloatingActionButton saveFab = findViewById(R.id.save_fab);
        saveFab.setOnClickListener(view -> {
            if (mExistingProductID > NO_ID) updateProduct(mExistingProductID);
            else saveProduct();
            finish();
        });
    }

    /**
     * Override the onCreateOptionsMenu method in order to
     * create the options menu for the Editor Activity.
     * @param menu The menu to create.
     * @return Returns true if the menu was created successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Override the onOptionsItemSelected method in order to
     * detect which menu item was clicked and to execute the selected option.
     * @param item The item that was clicked in the menu.
     * @return Executes the action and returns the clicked item from the menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (mExistingProductID > NO_ID) updateProduct(mExistingProductID);
                else saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                if (mExistingProductID > NO_ID) deleteProduct();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to save the data into the database as a new product.
     */
    private void saveProduct() {
        // Get data from the EditText fields.
        Product product = getEditTextData(NO_ID);
        // Insert the new product into the database.
        mData.insertData(EditorActivity.this, product);
    }

    /**
     * Method to update the existing product details.
     * @param existingProductId The ID of the product that needs to be updated.
     */
    private void updateProduct(int existingProductId) {
        // Get data from the EditText fields.
        Product product = getEditTextData(existingProductId);
        // Update the existing product from the database with the new data.
        mData.updateData(EditorActivity.this, product);
    }

    /** Method to delete the product from the database. */
    private void deleteProduct() {
        mData.deleteData(EditorActivity.this, mExistingProductID);
    }

    /**
     * Method to extract the text entered by the user into the EditText fields.
     * @param productID The ID of the Product, in case it's an existing one.
     * @return Returns a new Product to insert into the database.
     */
    private Product getEditTextData(int productID) {
        Product product;

        // Get the text from EditText fields.
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productAuthorString = mProductAuthorEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Convert the text to appropriate types.
        if (productPriceString.isEmpty()) productPriceString = "0";
        if (productQuantityString.isEmpty()) productQuantityString = "0";
        float productPriceFloat = Float.parseFloat(productPriceString);
        int productQuantityInt = Integer.parseInt(productQuantityString);

        // Create a new Product with the text entered/modified by the user.
        if (productID == NO_ID) {
            product = new Product(
                    productNameString,
                    productAuthorString,
                    productPriceFloat,
                    productQuantityInt,
                    supplierNameString,
                    supplierPhoneString
            );
        } else {
            product = new Product(
                    productID,
                    productNameString,
                    productAuthorString,
                    productPriceFloat,
                    productQuantityInt,
                    supplierNameString,
                    supplierPhoneString
            );
        }

        // Return the Product with the new data.
        return product;
    }

    /**
     * Method to populate the EditText fields with existing product data.
     * @param productID The ID of the product from which to read data.
     */
    private void openExistingProduct(int productID) {
        // Get the product from the database via the ID.
        Product product = mData.getData(EditorActivity.this, productID);

        // Fill the EditText fields with the existing product details.
        // Please ignore the String.format warnings below. We don't need formatting.
        // We need the data to be in the exact format that was introduced into the database.
        mProductNameEditText.setText(product.getProductName(), TextView.BufferType.EDITABLE);
        mProductAuthorEditText.setText(product.getProductAuthor(), TextView.BufferType.EDITABLE);
        mProductPriceEditText.setText(Float.toString(product.getProductPrice()), TextView.BufferType.EDITABLE);
        mProductQuantityEditText.setText(Integer.toString(product.getProductQuantity()), TextView.BufferType.EDITABLE);
        mSupplierNameEditText.setText(product.getSupplierName(), TextView.BufferType.EDITABLE);
        mSupplierPhoneEditText.setText(product.getSupplierPhone(), TextView.BufferType.EDITABLE);
    }

}
