package com.danieldogeanu.android.inventoryapp;

import android.app.LoaderManager;
import android.content.Context;
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
                // Do something.
                return true;
            case R.id.action_delete:
                // Do something.
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

}
