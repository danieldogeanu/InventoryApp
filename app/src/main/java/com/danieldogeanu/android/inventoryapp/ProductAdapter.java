package com.danieldogeanu.android.inventoryapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(@NonNull Context context, @NonNull ArrayList<Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.productNameTextView = convertView.findViewById(R.id.item_product_name);
            viewHolder.productAuthorTextView = convertView.findViewById(R.id.item_product_author);
            viewHolder.productPriceTextView = convertView.findViewById(R.id.item_product_price);
            viewHolder.productQuantityTextView = convertView.findViewById(R.id.item_product_quantity);
            viewHolder.supplierNameTextView = convertView.findViewById(R.id.item_supplier_name);
            viewHolder.supplierPhoneTextView = convertView.findViewById(R.id.item_supplier_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product currentProduct = getItem(position);

        viewHolder.productNameTextView.setText(currentProduct.getProductName());
        viewHolder.productAuthorTextView.setText(currentProduct.getProductAuthor());
        viewHolder.productPriceTextView.setText(Utils.formatPrice(currentProduct.getProductPrice()));
        viewHolder.productQuantityTextView.setText(Utils.formatQuantity(currentProduct.getProductQuantity()));
        viewHolder.supplierNameTextView.setText(currentProduct.getSupplierName());
        viewHolder.supplierPhoneTextView.setText(currentProduct.getSupplierPhone());

        return convertView;
    }

    private static class ViewHolder {
        private TextView productNameTextView,
                productAuthorTextView,
                productPriceTextView,
                productQuantityTextView,
                supplierNameTextView,
                supplierPhoneTextView;
    }

}
