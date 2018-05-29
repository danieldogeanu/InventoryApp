package com.danieldogeanu.android.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for Product objects.
 * Used to build the list of products to display in the InventoryActivity.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    /**
     * ProductAdapter constructor.
     * @param context The context in which this class is instantiated.
     * @param products The list of products to build.
     */
    public ProductAdapter(@NonNull Context context, @NonNull ArrayList<Product> products) {
        super(context, 0, products);
    }

    /**
     * Overrides the getView method to display a custom layout for each item in the list.
     * @param position The position in the list.
     * @param convertView The view for the item in the list.
     * @param parent The parent view of this item.
     * @return Returns the fully assembled item to display in the list.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate and find all the necessary views.
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemCard = convertView.findViewById(R.id.item_card);
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

        // Get the current Product.
        Product currentProduct = getItem(position);

        // Set Intent to open the current Product in the EditorActivity.
        viewHolder.itemCard.setOnClickListener(view -> {
            Intent editorIntent = new Intent(getContext(), EditorActivity.class);
            editorIntent.putExtra("product_id", currentProduct.getProductID());
            getContext().startActivity(editorIntent);
        });

        // Set the Product data to the item.
        viewHolder.productNameTextView.setText(currentProduct.getProductName());
        viewHolder.productAuthorTextView.setText(currentProduct.getProductAuthor());
        viewHolder.productPriceTextView.setText(Utils.formatPrice(currentProduct.getProductPrice()));
        viewHolder.productQuantityTextView.setText(Utils.formatQuantity(currentProduct.getProductQuantity()));
        viewHolder.supplierNameTextView.setText(currentProduct.getSupplierName());
        viewHolder.supplierPhoneTextView.setText(currentProduct.getSupplierPhone());

        // Return the fully assembled item.
        return convertView;
    }

    /** The ViewHolder that stores all the views necessary to build each item. */
    private static class ViewHolder {
        private RelativeLayout itemCard;
        private TextView productNameTextView,
                productAuthorTextView,
                productPriceTextView,
                productQuantityTextView,
                supplierNameTextView,
                supplierPhoneTextView;
    }

}
