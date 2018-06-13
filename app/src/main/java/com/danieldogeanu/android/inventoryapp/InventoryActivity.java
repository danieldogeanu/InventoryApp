package com.danieldogeanu.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.danieldogeanu.android.inventoryapp.data.Contract.TableEntry;
import com.danieldogeanu.android.inventoryapp.data.Data;

import java.util.ArrayList;

/**
 * Displays a list of products that were entered and stored in the app.
 * This is the main entry point for the app.
 */
public class InventoryActivity extends AppCompatActivity {

    // Data Holder
    private Data mData;

    /**
     * Overrides the onCreate method to assemble and display the Inventory Activity.
     * @param savedInstanceState The saved instance state Bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Get Data Class Instance
        mData = Data.getInstance();

        // Get Data and Set the Adapter
        displayData();

        // Attach Intent to open the Editor Activity.
        FloatingActionButton fab = findViewById(R.id.inventory_fab);
        fab.setOnClickListener(view -> {
            Intent editorIntent = new Intent(InventoryActivity.this, EditorActivity.class);
            startActivity(editorIntent);
        });

    }

    /** Override the onStart method to refresh the displayed data. */
    @Override
    protected void onStart() {
        super.onStart();
        displayData();
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
                mData.insertDummyData(InventoryActivity.this);
                displayData();
                return true;
            case R.id.action_delete_all_data:
                mData.deleteAllData(InventoryActivity.this);
                displayData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to display data from the database.
     * Gets all products from the database and adds them to the ProductAdapter.
     */
    private void displayData() {
        // Get Data
        ArrayList<Product> products = mData.getData(InventoryActivity.this);

        // Get the ListView and set the Adapter
        ListView listView = findViewById(R.id.inventory_list);
        View emptyView = findViewById(R.id.empty_state_view);
        ProductAdapter adapter = new ProductAdapter(InventoryActivity.this, products);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
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
            getContentResolver().insert(TableEntry.CONTENT_URI, values);
        }
    }
}
