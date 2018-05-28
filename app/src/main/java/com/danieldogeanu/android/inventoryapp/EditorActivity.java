package com.danieldogeanu.android.inventoryapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.danieldogeanu.android.inventoryapp.data.Data;

public class EditorActivity extends AppCompatActivity {

    private Data mData;

    private EditText mProductNameEditText;
    private EditText mProductAuthorEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    private static final int NO_ID = -1;
    private int mExistingProductID = NO_ID;

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

        if (getIntent().hasExtra("product_id")) {
            mExistingProductID = (int) getIntent().getSerializableExtra("product_id");
            openExistingProduct(mExistingProductID);
        }

        FloatingActionButton saveFab = findViewById(R.id.save_fab);
        saveFab.setOnClickListener(view -> {
            if (mExistingProductID > NO_ID) updateProduct(mExistingProductID);
            else saveProduct();
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
                if (mExistingProductID > NO_ID) updateProduct(mExistingProductID);
                else saveProduct();
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

        Product product = new Product(
                productNameString,
                productAuthorString,
                productPriceFloat,
                productQuantityInt,
                supplierNameString,
                supplierPhoneString
        );
        mData.insertData(EditorActivity.this, product);
    }

    private void updateProduct(int existingProductId) {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productAuthorString = mProductAuthorEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        float productPriceFloat = Float.parseFloat(productPriceString);
        int productQuantityInt = Integer.parseInt(productQuantityString);

        Product product = new Product(
                existingProductId,
                productNameString,
                productAuthorString,
                productPriceFloat,
                productQuantityInt,
                supplierNameString,
                supplierPhoneString
        );
        mData.updateData(EditorActivity.this, product);
    }

    private void openExistingProduct(int productID) {
        Product product = mData.getData(EditorActivity.this, productID);

        mProductNameEditText.setText(product.getProductName(), TextView.BufferType.EDITABLE);
        mProductAuthorEditText.setText(product.getProductAuthor(), TextView.BufferType.EDITABLE);
        mProductPriceEditText.setText(Float.toString(product.getProductPrice()), TextView.BufferType.EDITABLE);
        mProductQuantityEditText.setText(Integer.toString(product.getProductQuantity()), TextView.BufferType.EDITABLE);
        mSupplierNameEditText.setText(product.getSupplierName(), TextView.BufferType.EDITABLE);
        mSupplierPhoneEditText.setText(product.getSupplierPhone(), TextView.BufferType.EDITABLE);
    }

}
