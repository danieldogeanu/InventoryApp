package com.danieldogeanu.android.inventoryapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.danieldogeanu.android.inventoryapp.data.Data;

public class EditorActivity extends AppCompatActivity {

    private Data mData;

    private EditText mProductNameEditText;
    private EditText mProductAuthorEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mData = Data.getInstance();

        mProductNameEditText = findViewById(R.id.product_name);
        mProductAuthorEditText = findViewById(R.id.product_author);
        mProductPriceEditText = findViewById(R.id.product_price);
        mProductQuantityEditText = findViewById(R.id.product_quantity);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        FloatingActionButton saveFab = findViewById(R.id.save_fab);
        saveFab.setOnClickListener(view -> {
            saveProduct();
            finish();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                // Do Stuff
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productAuthorString = mProductAuthorEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        float productPriceFloat = Float.parseFloat(productPriceString);
        int productQuantityInt = Integer.parseInt(productQuantityString);

        Product product = new Product(productNameString, productAuthorString, productPriceFloat, productQuantityInt, supplierNameString, supplierPhoneString);
        mData.insertData(EditorActivity.this, product);
    }

}
