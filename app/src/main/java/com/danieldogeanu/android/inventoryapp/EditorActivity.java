package com.danieldogeanu.android.inventoryapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.danieldogeanu.android.inventoryapp.data.Data;

/**
 * Activity class that allows the user to create a new product, or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    // Constant used for debugging.
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

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
            if (canSave()) {
                if (isExistingProduct()) updateProduct(mExistingProductID);
                else saveProduct();
                finish();
            } else {
                showError();
            }
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
                if (canSave()) {
                    if (isExistingProduct()) updateProduct(mExistingProductID);
                    else saveProduct();
                    finish();
                } else {
                    showError();
                }
                return true;
            case R.id.action_delete:
                if (isExistingProduct()) deleteProduct();
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
        final String EMPTY_STR = "0";
        if (productPriceString.isEmpty()) productPriceString = EMPTY_STR;
        if (productQuantityString.isEmpty()) productQuantityString = EMPTY_STR;
        float productPriceFloat = Float.parseFloat(limitChars(productPriceString, 9));
        int productQuantityInt = Integer.parseInt(limitChars(productQuantityString, 9));

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

    /**
     * Method to check if this is an existing product.
     * @return Returns true, if we have a product ID.
     */
    private boolean isExistingProduct() {
        return mExistingProductID > NO_ID;
    }

    /**
     * Method to check if there aren't empty fields so we can save the data into the database.
     * @return Returns true if there aren't empty fields, false if there are.
     */
    private boolean canSave() {
        // Initialize canSave to true.
        boolean canSave = true;

        // Get the Product from user data.
        Product product = getEditTextData(NO_ID);

        // Check if the Product fields are empty.
        if (product.getProductName().isEmpty()) canSave = false;
        if (product.getProductAuthor().isEmpty()) canSave = false;
        if (product.getProductPrice() == 0) canSave = false;
        if (product.getProductQuantity() == 0) canSave = false;
        if (product.getSupplierName().isEmpty()) canSave = false;
        if (product.getSupplierPhone().isEmpty()) canSave = false;

        // Return the canSave value.
        return canSave;
    }

    /** Method to show the error message in case we can't save. */
    private void showError() {
        String errorMsg = getString(R.string.can_not_save);
        Utils.showToast(EditorActivity.this, errorMsg);
        Log.e(LOG_TAG, errorMsg);
    }

    /**
     * Method to limit the length of the string that is
     * intended to be converted to float or int.
     * @param string The String to limit.
     * @param limit The limit to set.
     * @return Returns the limited String.
     */
    public String limitChars(String string, int limit) {
        String limitedStr;
        String errorMsg = getString(R.string.number_too_large);
        if (string.length() > limit) {
            limitedStr = string.substring(0, limit);
            Utils.showToast(EditorActivity.this, errorMsg);
            Log.e(LOG_TAG, errorMsg);
        } else {
            limitedStr = string;
        }
        return limitedStr;
    }

}
