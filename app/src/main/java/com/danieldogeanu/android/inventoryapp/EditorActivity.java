package com.danieldogeanu.android.inventoryapp;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.danieldogeanu.android.inventoryapp.data.Contract.TableEntry;

/**
 * Activity class that allows the user to create a new product, or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EXISTING_PRODUCT_LOADER = 0;

    // Existing Product Uri
    private Uri mExistingProductUri;

    // EditText Fields
    private EditText mProductNameEditText, mProductAuthorEditText, mProductPriceEditText,
            mProductQuantityEditText, mSupplierNameEditText, mSupplierPhoneEditText;
    private FloatingActionButton mSaveFab;


    /**
     * Overrides the onCreate method to assemble and display the Editor Activity.
     * @param savedInstanceState The saved instance state Bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find the views.
        findAllViews();

        // Get the intent that was used to launch this activity and extract the product Uri.
        Intent intent = getIntent();
        mExistingProductUri = intent.getData();

        // If the intent doesn't contain a product URI, then we know that we are creating a new product.
        if (mExistingProductUri == null) {
            setTitle(R.string.empty_editor_title);
        } else {
            setTitle(R.string.full_editor_title);
            // If it's an existing product, initialize the Loader to read the product data from database.
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, EditorActivity.this);
        }

        // Save or update the product and exit the Editor Activity.
        mSaveFab.setOnClickListener(view -> {
            if (canSave()) {
                if (isExistingProduct()) updateProduct();
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
                    if (isExistingProduct()) updateProduct();
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
     * Overrides the onCreateLoader method in order to define
     * the projection that contains all columns from the products table.
     * @return Returns a Loader that will execute the query on a background thread.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TableEntry._ID,
                TableEntry.COL_PRODUCT_NAME,
                TableEntry.COL_AUTHOR,
                TableEntry.COL_PRICE,
                TableEntry.COL_QUANTITY,
                TableEntry.COL_SUPPLIER_NAME,
                TableEntry.COL_SUPPLIER_PHONE
        };

        return new CursorLoader(
                EditorActivity.this,
                mExistingProductUri,
                projection,
                null,
                null,
                null
        );
    }

    /**
     * Overrides the onLoadFinished method in order to read product data from
     * the database, and update the fields on the screen with the existing values.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the Cursor is null or there it contains less than 1 row.
        if (cursor == null || cursor.getCount() < 1) return;

        // Proceed with moving to the first row of the cursor and reading data from it.
        // This should be the only row in the cursor.
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in.
            int colIndexProductName = cursor.getColumnIndex(TableEntry.COL_PRODUCT_NAME);
            int colIndexProductAuthor = cursor.getColumnIndex(TableEntry.COL_AUTHOR);
            int colIndexProductPrice = cursor.getColumnIndex(TableEntry.COL_PRICE);
            int colIndexProductQuantity = cursor.getColumnIndex(TableEntry.COL_QUANTITY);
            int colIndexSupplierName = cursor.getColumnIndex(TableEntry.COL_SUPPLIER_NAME);
            int colIndexSupplierPhone = cursor.getColumnIndex(TableEntry.COL_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index.
            String productName = cursor.getString(colIndexProductName);
            String productAuthor = cursor.getString(colIndexProductAuthor);
            float productPrice = cursor.getFloat(colIndexProductPrice);
            int productQuantity = cursor.getInt(colIndexProductQuantity);
            String supplierName = cursor.getString(colIndexSupplierName);
            String supplierPhone = cursor.getString(colIndexSupplierPhone);

            // Update the views on the screen with the values from the database.
            mProductNameEditText.setText(productName);
            mProductAuthorEditText.setText(productAuthor);
            mProductPriceEditText.setText(Float.toString(productPrice));
            mProductQuantityEditText.setText(Integer.toString(productQuantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    /**
     * Overrides the onLoaderReset method in order to handle the case in which the Loader
     * is invalidated. If that happens, we need to clear out the data from the fields.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductAuthorEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    /** Method to find all necessary views that we need to populate with product data. */
    private void findAllViews() {
        mProductNameEditText = findViewById(R.id.product_name);
        mProductAuthorEditText = findViewById(R.id.product_author);
        mProductPriceEditText = findViewById(R.id.product_price);
        mProductQuantityEditText = findViewById(R.id.product_quantity);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);
        mSaveFab = findViewById(R.id.save_fab);
    }

    /** Method to save the data into the database as a new product. */
    private void saveProduct() {
        ContentValues values = getValidatedData();
        Uri newProductUri = getContentResolver().insert(TableEntry.CONTENT_URI, values);
        if (newProductUri == null) {
            Utils.showToastAndLog(EditorActivity.this, true, LOG_TAG, getString(R.string.insert_msg_error));
        } else {
            String saveSuccessMsg = getString(R.string.insert_msg_success) + String.valueOf(ContentUris.parseId(newProductUri));
            Utils.showToastAndLog(EditorActivity.this, false, LOG_TAG, saveSuccessMsg);
        }
    }

    /** Method to update the existing product details. */
    private void updateProduct() {
        ContentValues values = getValidatedData();
        int rowsAffected = getContentResolver().update(mExistingProductUri, values, null, null);
        if (rowsAffected == 0) Utils.showToastAndLog(EditorActivity.this, true, LOG_TAG, getString(R.string.update_msg_error));
        else Utils.showToastAndLog(EditorActivity.this, false, LOG_TAG, getString(R.string.update_msg_success));
    }

    /** Method to delete the product from the database. */
    private void deleteProduct() {
        if (mExistingProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mExistingProductUri, null, null);
            if (rowsDeleted == 0) Utils.showToastAndLog(EditorActivity.this, true, LOG_TAG, getString(R.string.delete_msg_error));
            else Utils.showToastAndLog(EditorActivity.this, false, LOG_TAG, getString(R.string.delete_msg_success));
        }
        finish();
    }

    /**
     * Method to extract the text entered by the user into the EditText fields.
     * @return Returns a new Product object to pass to other methods.
     */
    private Product getEditTextData() {
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

        // Return a new Product with the text entered/modified by the user.
        return new Product(
                productNameString,
                productAuthorString,
                productPriceFloat,
                productQuantityInt,
                supplierNameString,
                supplierPhoneString
        );
    }

    /**
     * Method to get and validate data from the EditText fields.
     * @return Return the validated ContentValues that can be saved to the database.
     */
    private ContentValues getValidatedData() {
        // Get data from the EditText fields.
        Product product = getEditTextData();

        // Extract data from the Product object.
        String productName = product.getProductName();
        String productAuthor = product.getProductAuthor();
        float productPrice = product.getProductPrice();
        int productQuantity = product.getProductQuantity();
        String supplierName = product.getSupplierName();
        String supplierPhone = product.getSupplierPhone();

        // Create the ContentValues object.
        // Column names are the keys, product attributes are the values.
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(productName)) values.put(TableEntry.COL_PRODUCT_NAME, productName);
        values.put(TableEntry.COL_AUTHOR, productAuthor); // Can be empty.
        if ((productPrice != 0) && (productPrice < Float.MAX_VALUE)) values.put(TableEntry.COL_PRICE, productPrice);
        if ((productQuantity != 0) && (productQuantity < Integer.MAX_VALUE)) values.put(TableEntry.COL_QUANTITY, productQuantity);
        if (!TextUtils.isEmpty(supplierName)) values.put(TableEntry.COL_SUPPLIER_NAME, supplierName);
        if (!TextUtils.isEmpty(supplierPhone)) values.put(TableEntry.COL_SUPPLIER_PHONE, supplierPhone);

        // Return validated values.
        return values;
    }

    /**
     * Method to check if this is an existing product.
     * @return Returns true, if we have a product Uri.
     */
    private boolean isExistingProduct() {
        return (mExistingProductUri != null);
    }

    /**
     * Method to check if there aren't empty fields so we can save the data into the database.
     * NOTE: The Author field is not required so we can ignore it.
     * @return Returns true if there aren't empty fields and we can save to the database.
     */
    private boolean canSave() {
        boolean canSave = true;

        // Get the Product from the user inputs.
        Product product = getEditTextData();

        // Check if the Product fields are empty.
        if (product.getProductName().isEmpty()) canSave = false;
        if (product.getProductPrice() == 0) canSave = false;
        if (product.getProductQuantity() == 0) canSave = false;
        if (product.getSupplierName().isEmpty()) canSave = false;
        if (product.getSupplierPhone().isEmpty()) canSave = false;

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
