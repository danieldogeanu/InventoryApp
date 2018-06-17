package com.danieldogeanu.android.inventoryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    // Boolean to keep track if we can save or not.
    private boolean mCanSave = true;

    // Boolean flag that keeps track of whether or not the product has been edited.
    private boolean mHasChanged = false;

    // EditText Fields
    private EditText mProductNameEditText, mProductAuthorEditText, mProductPriceEditText,
            mProductQuantityEditText, mSupplierNameEditText, mSupplierPhoneEditText;
    private FloatingActionButton mSaveFab;

    // Touch listener to detect when the user modified the view.
    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener mTouchListener = (view, motionEvent) -> {
        mHasChanged = true;
        return false;
    };

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
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // It doesn't make sense to delete a product that hasn't been created yet.
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.full_editor_title);
            // If it's an existing product, initialize the Loader to read the product data from database.
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, EditorActivity.this);
        }

        // Save or update the product and exit the Editor Activity.
        mSaveFab.setOnClickListener(view -> {
            if (isExistingProduct()) updateProduct();
            else saveProduct();
        });

        // Set touch listeners for all EditText fields.
        setTouchListeners();
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mExistingProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
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
                if (isExistingProduct()) updateProduct();
                else saveProduct();
                return true;
            case R.id.action_delete:
                if (isExistingProduct()) deleteProduct();
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

    /**
     * Method to attach OnTouchListeners to all EditText inputs fields, so we can determine
     * if the user has touched or modified the field. This will let us know if there are
     * unsaved changes or not, if the user tries to leave the editor without saving.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListeners() {
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductAuthorEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
    }

    /** Method to save the data into the database as a new product. */
    private void saveProduct() {
        ContentValues values = getValidatedData();
        if (mCanSave) {
            Uri newProductUri = getContentResolver().insert(TableEntry.CONTENT_URI, values);
            if (newProductUri == null) {
                Utils.showToastAndLog(EditorActivity.this, true, LOG_TAG, getString(R.string.insert_msg_error));
            } else {
                String saveSuccessMsg = getString(R.string.insert_msg_success) + String.valueOf(ContentUris.parseId(newProductUri));
                Utils.showToastAndLog(EditorActivity.this, false, LOG_TAG, saveSuccessMsg);
            }
            finish();
        } else {
            Utils.showToastAndLog(EditorActivity.this, true, LOG_TAG, getString(R.string.error_can_not_save));
        }
    }

    /** Method to update the existing product details. */
    private void updateProduct() {
        ContentValues values = getValidatedData();
        if (mCanSave) {
            int rowsAffected = getContentResolver().update(mExistingProductUri, values, null, null);
            if (rowsAffected == 0) Utils.showToastAndLog(EditorActivity.this, true, LOG_TAG, getString(R.string.update_msg_error));
            else Utils.showToastAndLog(EditorActivity.this, false, LOG_TAG, getString(R.string.update_msg_success));
            finish();
        } else {
            Utils.showToastAndLog(EditorActivity.this, true, LOG_TAG, getString(R.string.error_can_not_save));
        }
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
        float productPriceFloat = Float.parseFloat(productPriceString);
        int productQuantityInt = Integer.parseInt(productQuantityString);

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
        if (!TextUtils.isEmpty(productName)) {
            values.put(TableEntry.COL_PRODUCT_NAME, productName);
        } else { mCanSave = false; }
        if (!TextUtils.isEmpty(productAuthor)) {
            values.put(TableEntry.COL_AUTHOR, productAuthor);
        } else { mCanSave = false; }
        if ((productPrice != 0) && (productPrice < Float.MAX_VALUE)) {
            values.put(TableEntry.COL_PRICE, productPrice);
        } else { mCanSave = false; }
        if ((productQuantity != 0) && (productQuantity < Integer.MAX_VALUE)) {
            values.put(TableEntry.COL_QUANTITY, productQuantity);
        } else { mCanSave = false; }
        if (!TextUtils.isEmpty(supplierName)) {
            values.put(TableEntry.COL_SUPPLIER_NAME, supplierName);
        } else { mCanSave = false; }
        if (!TextUtils.isEmpty(supplierPhone)) {
            values.put(TableEntry.COL_SUPPLIER_PHONE, supplierPhone);
        } else { mCanSave = false; }

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
     * Show a dialog that warns the user there are unsaved changes that will be lost if they leave the editor.
     * @param discardButtonClickListener This is the click listener for what to do when the user confirms
     *                                   they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, (dialogInterface, id) -> {
            // User clicked "Keep Editing", so dismiss the dialog and continue editing product.
            if (dialogInterface != null) dialogInterface.dismiss();
        });

        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
