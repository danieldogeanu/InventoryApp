package com.danieldogeanu.android.inventoryapp;

import android.app.LoaderManager;
import android.content.Context;
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

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;
    private int mCurrentQuantity;

    private TextView mProductName, mProductAuthor, mProductPrice,
            mProductQuantity, mSupplierName, mSupplierPhone;
    private ImageButton mIncrementBtn, mDecrementBtn;
    private Button mCallBtn;
    private FloatingActionButton mEditFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Find all relevant views that we need to populate with product data.
        mProductName = findViewById(R.id.details_product_name);
        mProductAuthor = findViewById(R.id.details_product_author);
        mProductPrice = findViewById(R.id.details_product_price);
        mProductQuantity = findViewById(R.id.details_product_quantity);
        mSupplierName = findViewById(R.id.details_supplier_name);
        mSupplierPhone = findViewById(R.id.details_supplier_phone);
        mIncrementBtn = findViewById(R.id.details_incr_btn);
        mDecrementBtn = findViewById(R.id.details_decr_btn);
        mCallBtn = findViewById(R.id.details_call_btn);
        mEditFab = findViewById(R.id.edit_fab);

        // Get the intent that was used to launch this activity and extract the product Uri.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // Initialize the loader to read the product data from the database and display the current values.
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, DetailsActivity.this);

        // Setup the FAB to open the Editor Activity.
        mEditFab.setOnClickListener(view -> {
            launchEditorActivity(DetailsActivity.this);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                launchEditorActivity(DetailsActivity.this);
                finish();
                return true;
            case R.id.action_delete:
                // Do something.
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
            mProductName.setText(productName);
            mProductAuthor.setText(productAuthor);
            mProductPrice.setText(Utils.formatPrice(productPrice));
            mProductQuantity.setText(Utils.formatQuantity(productQuantity));
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(supplierPhone);

            // Set Click Listener for the Call Supplier Button.
            mCallBtn.setOnClickListener(view -> launchCall(supplierPhone));
        }
    }

    /**
     * Overrides the onLoaderReset method in order to handle the case in which the Loader
     * is invalidated. If that happens, we need to clear out the data from the views
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mProductAuthor.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mSupplierName.setText("");
        mSupplierPhone.setText("");
    }

    /**
     * Method to launch the Editor Activity with the current product Uri.
     * @param context The context from which this method is called.
     */
    private void launchEditorActivity(Context context) {
        if (mCurrentProductUri != null) {
            Intent editorIntent = new Intent(context, EditorActivity.class);
            editorIntent.setData(mCurrentProductUri);
            context.startActivity(editorIntent);
        }
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

}
