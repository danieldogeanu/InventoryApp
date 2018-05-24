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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Get List View
        ListView listView = findViewById(R.id.inventory_list);

        // Get Data
        Data data = Data.getInstance();
        ArrayList<Product> products = data.queryData(InventoryActivity.this);

        // Set the Adapter
        ProductAdapter adapter = new ProductAdapter(InventoryActivity.this, products);
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.inventory_fab);
        fab.setOnClickListener(view -> {
            Intent editorIntent = new Intent(InventoryActivity.this, EditorActivity.class);
            startActivity(editorIntent);
        });

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
                // Do Stuff
                return true;
            case R.id.action_delete_all_data:
                // Do Stuff
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
