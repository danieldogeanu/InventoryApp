package com.danieldogeanu.android.inventoryapp;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.danieldogeanu.android.inventoryapp.data.Contract.TableEntry;

import java.util.ArrayList;

/**
 * Displays a list of products that were entered and stored in the app.
 * This is the main entry point for the app.
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = InventoryActivity.class.getSimpleName();
    private static final int PRODUCT_LOADER = 0;

    // Cursor adapter used to populate the ListView with product data.
    private ProdCursorAdapter mCursorAdapter;

    /**
     * Overrides the onCreate method to assemble and display the Inventory Activity.
     * @param savedInstanceState The saved instance state Bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Attach Intent to open the Editor Activity.
        FloatingActionButton fab = findViewById(R.id.inventory_fab);
        fab.setOnClickListener(view -> {
            Intent editorIntent = new Intent(InventoryActivity.this, EditorActivity.class);
            startActivity(editorIntent);
        });

        // Find the ListView which will be populated with product data.
        ListView invListView = findViewById(R.id.inventory_list);

        // Find and set empty state view on the ListView.
        View emptyStateView = findViewById(R.id.empty_state_view);
        invListView.setEmptyView(emptyStateView);

        // Setup an Adapter to create a list of items with each row of product data in the Cursor.
        // There is no product data yet (until the Loader finishes), so we pass in null for the Cursor.
        mCursorAdapter = new ProdCursorAdapter(InventoryActivity.this, null);
        invListView.setAdapter(mCursorAdapter);

        // Initialize the Loader.
        getLoaderManager().initLoader(PRODUCT_LOADER, null, InventoryActivity.this);
    }

    /**
     * Override the onCreateOptionsMenu method in order to
     * create the options menu for Inventory Activity.
     * @param menu The menu to create.
     * @return Returns true if the menu was created successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    /**
     * Override the onOptionsItemSelected method in order to
     * detect which menu item was clicked and to execute the selected action.
     * @param item The item that was clicked in the menu.
     * @return Executes the action and returns the clicked item from the menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all_data:
                deleteAllData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Overrides the onCreateLoader method in order to create the projection
     * that specifies the columns from the table that we care about.
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
                TableEntry.COL_SUPPLIER_NAME
        };

        return new CursorLoader(
                InventoryActivity.this,
                TableEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    /**
     * Overrides the onLoadFinished method in order to update the
     * ProdCursorAdapter with this new Cursor containing updated product data.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    /**
     * Overrides the onLoaderReset method in order to
     * call this callback when the data needs to be deleted.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    /** Insert dummy (demo) product data into the database. */
    private void insertDummyData() {
        // Get dummy data string arrays.
        String[] dummyProductTitles = getResources().getStringArray(R.array.dummy_prod_titles);
        String[] dummyProductAuthors = getResources().getStringArray(R.array.dummy_prod_authors);
        String[] dummyProductPrices = getResources().getStringArray(R.array.dummy_prod_prices);
        String[] dummyProductQuantities = getResources().getStringArray(R.array.dummy_prod_quantity);
        String[] dummySupplierNames = getResources().getStringArray(R.array.dummy_supl_names);
        String[] dummySupplierPhones = getResources().getStringArray(R.array.dummy_supl_phones);

        // Create array to hold Uris for products added into the database.
        ArrayList<Uri> prodUris = new ArrayList<>();

        // Insert dummy data into the database.
        for (int i = 0; i < dummyProductTitles.length; i++) {
            // Extract dummy data and add it to ContentValues object.
            ContentValues values = new ContentValues();
            values.put(TableEntry.COL_PRODUCT_NAME, dummyProductTitles[i]);
            values.put(TableEntry.COL_AUTHOR, dummyProductAuthors[i]);
            values.put(TableEntry.COL_PRICE, Float.parseFloat(dummyProductPrices[i]));
            values.put(TableEntry.COL_QUANTITY, Integer.parseInt(dummyProductQuantities[i]));
            values.put(TableEntry.COL_SUPPLIER_NAME, dummySupplierNames[i]);
            values.put(TableEntry.COL_SUPPLIER_PHONE, dummySupplierPhones[i]);

            // Insert values into the database.
            Uri newProdUri = getContentResolver().insert(TableEntry.CONTENT_URI, values);
            prodUris.add(newProdUri);
        }

        // If the number of products added matches the number of Uris, display Toast and Log message.
        if (prodUris.size() == dummyProductTitles.length) {
            String dummyMsg = getString(R.string.insert_msg_dummy);
            Utils.showToast(getApplicationContext(), dummyMsg);
            Log.i(LOG_TAG, dummyMsg);
        }
    }

    /** Delete all products data from the database. */
    private void deleteAllData() {
        // Delete all products from the database.
        int rowsDeleted = getContentResolver().delete(TableEntry.CONTENT_URI, null, null);

        // Show Toast and Log the message.
        String deletionMsg = getString(R.string.delete_msg_all);
        if (rowsDeleted > 0) {
            Utils.showToast(getApplicationContext(), deletionMsg);
            Log.i(LOG_TAG, deletionMsg);
        }
    }
}
