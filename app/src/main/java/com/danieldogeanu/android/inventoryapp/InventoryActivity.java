package com.danieldogeanu.android.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.danieldogeanu.android.inventoryapp.data.Data;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    ListView mListView;
    Data mData;
    ArrayList<Product> mProducts;
    ProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Get List View
        mListView = findViewById(R.id.inventory_list);

        // Get Data Class Instance
        mData = Data.getInstance();

        // Get Data and Set the Adapter
        displayData();

        FloatingActionButton fab = findViewById(R.id.inventory_fab);
        fab.setOnClickListener(view -> {
            Intent editorIntent = new Intent(InventoryActivity.this, EditorActivity.class);
            startActivity(editorIntent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

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

    private void displayData() {
        // Get Data
        mProducts = mData.getData(InventoryActivity.this);
        // Set the Adapter
        mAdapter = new ProductAdapter(InventoryActivity.this, mProducts);
        mListView.setAdapter(mAdapter);
    }
}
