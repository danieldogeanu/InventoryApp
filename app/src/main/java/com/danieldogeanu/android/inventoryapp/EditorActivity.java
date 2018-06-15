package com.danieldogeanu.android.inventoryapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
        // TODO: Complete saveProduct Method
    }

    /** Method to update the existing product details. */
    private void updateProduct() {
        // TODO: Complete updateProduct Method
    }

    /** Method to delete the product from the database. */
    private void deleteProduct() {
        // TODO: Complete deleteProduct Method
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
     * @return Returns true if there aren't empty fields, false if there are.
     */
    private boolean canSave() {
        boolean canSave = true;

        // TODO: Finish canSave Method

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
