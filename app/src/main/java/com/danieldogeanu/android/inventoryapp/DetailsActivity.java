package com.danieldogeanu.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.danieldogeanu.android.inventoryapp.data.Contract.TableEntry;

/**
 * Activity class that allows the user to visualize the details of an existing product.
 */
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    private static final int EXISTING_PRODUCT_LOADER = 0;

    // Values that we need to keep track of.
    private Uri mCurrentProductUri;
    private int mCurrentQuantity;
    private boolean mQuantityChanged = false;

    // Define all the necessary views.
    private TextView mProductNameTextView, mProductAuthorTextView, mProductPriceTextView,
            mProductQuantityTextView, mSupplierNameTextView, mSupplierPhoneTextView;
    private Button mCallSupplierBtn;
    private ImageButton mIncrementBtn, mDecrementBtn;
    private FloatingActionButton mEditFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Find the views.
        findAllViews();

        // Get the intent that was used to launch this activity and extract the product Uri.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // Initialize the loader to read the product data from the database and display the current values.
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, DetailsActivity.this);

        // Setup the FAB to open the Editor Activity.
        mEditFab.setOnClickListener(view -> launchEditorActivity());

        // Set Click Listeners for the Increment and Decrement buttons.
        mIncrementBtn.setOnClickListener(view -> incrementQuantity());
        mDecrementBtn.setOnClickListener(view -> decrementQuantity());
    }

    /**
     * Override onPause to save the quantity value to database when the user leaves the activity.
     * We don't need a button for this, we can do it automatically.
     */
    @Override
    protected void onPause() {
        super.onPause();
        updateQuantity();
    }

    /**
     * Override the onCreateOptionsMenu method in order to
     * create the options menu for the Details Activity.
     * @param menu The menu to create.
     * @return Returns true if the menu was created successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
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
            case R.id.action_edit:
                launchEditorActivity();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
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
                DetailsActivity.this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null
        );
    }

    /**
     * Overrides the onLoadFinished method in order to read product data from
     * the database, and update the views on the screen with the existing values.
     */
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

            // Update the global values for Quantity.
            mCurrentQuantity = productQuantity;

            // Update the views on the screen with the values from the database.
            mProductNameTextView.setText(productName);
            mProductAuthorTextView.setText(productAuthor);
            mProductPriceTextView.setText(Utils.formatPrice(productPrice));
            mProductQuantityTextView.setText(Utils.formatQuantity(productQuantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneTextView.setText(supplierPhone);

            // Set Click Listener for the Call Supplier Button.
            // We do this here to make sure that we have the phone number.
            if (!supplierPhone.isEmpty()) mCallSupplierBtn.setOnClickListener(view -> launchCall(supplierPhone));
        }
    }

    /**
     * Overrides the onLoaderReset method in order to handle the case in which the Loader
     * is invalidated. If that happens, we need to clear out the data from the views
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameTextView.setText("");
        mProductAuthorTextView.setText("");
        mProductPriceTextView.setText("");
        mProductQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneTextView.setText("");
    }

    /** Method to find all necessary views that we need to populate with product data. */
    private void findAllViews() {
        mProductNameTextView = findViewById(R.id.details_product_name);
        mProductAuthorTextView = findViewById(R.id.details_product_author);
        mProductPriceTextView = findViewById(R.id.details_product_price);
        mProductQuantityTextView = findViewById(R.id.details_product_quantity);
        mSupplierNameTextView = findViewById(R.id.details_supplier_name);
        mSupplierPhoneTextView = findViewById(R.id.details_supplier_phone);
        mCallSupplierBtn = findViewById(R.id.details_call_btn);
        mIncrementBtn = findViewById(R.id.details_incr_btn);
        mDecrementBtn = findViewById(R.id.details_decr_btn);
        mEditFab = findViewById(R.id.edit_fab);
    }

    /** Method to launch the Editor Activity with the current product Uri. */
    private void launchEditorActivity() {
        if (mCurrentProductUri != null) {
            Intent editorIntent = new Intent(DetailsActivity.this, EditorActivity.class);
            editorIntent.setData(mCurrentProductUri);
            startActivity(editorIntent);
        }
        finish();
    }

    /** Method to launch Phone Dialer app, to call the specified Supplier Phone number. */
    private void launchCall(String phone) {
        if (!phone.isEmpty()) {
            Uri phoneNumber = Uri.parse("tel:" + phone);
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(phoneNumber);
            if (dialIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(dialIntent);
            }
        }
    }

    /** Method to increment the current product quantity by one. */
    private void incrementQuantity() {
        if (mCurrentQuantity != Integer.MAX_VALUE) {
            mCurrentQuantity++;
            mProductQuantityTextView.setText(Utils.formatQuantity(mCurrentQuantity));
            mQuantityChanged = true;
        }
    }

    /** Method to decrement the current product quantity by one. */
    private void decrementQuantity() {
        if (mCurrentQuantity > 0) {
            mCurrentQuantity--;
            mProductQuantityTextView.setText(Utils.formatQuantity(mCurrentQuantity));
            mQuantityChanged = true;
        }
    }

    /** Method to update the product quantity and save the new value into the database. */
    private void updateQuantity() {
        if (mQuantityChanged) {
            ContentValues values = new ContentValues();
            values.put(TableEntry.COL_QUANTITY, mCurrentQuantity);
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) Utils.showToastAndLog(DetailsActivity.this, true, LOG_TAG, getString(R.string.update_msg_error));
            else Utils.showToastAndLog(DetailsActivity.this, false, LOG_TAG, getString(R.string.update_msg_success));
        }
    }

    /** Method to delete the product from the database. */
    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) Utils.showToastAndLog(DetailsActivity.this, true, LOG_TAG, getString(R.string.delete_msg_error));
            else Utils.showToastAndLog(DetailsActivity.this, false, LOG_TAG, getString(R.string.delete_msg_success));
        }
        finish();
    }

    /** Method to show a confirmation dialog for the delete action. */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, (dialogInterface, id) -> {
            deleteProduct();
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, id) -> {
            if (dialogInterface != null) dialogInterface.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
