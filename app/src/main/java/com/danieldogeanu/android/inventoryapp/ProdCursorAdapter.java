package com.danieldogeanu.android.inventoryapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danieldogeanu.android.inventoryapp.data.Contract.TableEntry;

/**
 * Adapter class for the ListView that uses a Cursor of product data as its data source.
 * This adapter knows how to create list items for each row of product data in the Cursor.
 */
public class ProdCursorAdapter extends CursorAdapter {

    /**
     * Constructor for a new ProdCursorAdapter.
     * @param context The context from which this class is instantiated.
     * @param cursor The cursor from which to get the data.
     */
    public ProdCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     * @param context The app context.
     * @param cursor The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent The parent to which the new view is attached to.
     * @return The newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate the item layout.
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        // Create a new ViewHolder to hold all child views.
        ViewHolder viewHolder = new ViewHolder(view);
        // Set the ViewHolder as a tag to the newly inflated layout.
        view.setTag(viewHolder);
        // Return the inflated layout.
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get the ViewHolder from the tag set in the newView method.
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Find the columns of product attributes that we're interested in.
        int indexProdID = cursor.getColumnIndex(TableEntry._ID);
        int indexProdName = cursor.getColumnIndex(TableEntry.COL_PRODUCT_NAME);
        int indexProdAuthor = cursor.getColumnIndex(TableEntry.COL_AUTHOR);
        int indexProdPrice = cursor.getColumnIndex(TableEntry.COL_PRICE);
        int indexProdQuantity = cursor.getColumnIndex(TableEntry.COL_QUANTITY);
        int indexSupplName = cursor.getColumnIndex(TableEntry.COL_SUPPLIER_NAME);

        // Read the attributes from the Cursor for the current product.
        long productID = cursor.getLong(indexProdID);
        String productName = cursor.getString(indexProdName);
        String productAuthor = cursor.getString(indexProdAuthor);
        float productPrice = cursor.getFloat(indexProdPrice);
        int productQuantity = cursor.getInt(indexProdQuantity);
        String productSupplier = cursor.getString(indexSupplName);

        // If the product author and supplier are null or empty, set default values to display.
        // We do this because those fields are not required and the user can leave them empty.
        if (TextUtils.isEmpty(productAuthor)) productAuthor = context.getString(R.string.unknown_author);
        if (TextUtils.isEmpty(productSupplier)) productSupplier = context.getString(R.string.unknown_supplier);

        // Set Intent to open the current Product in the DetailsActivity.
        viewHolder.itemCard.setOnClickListener(item -> {
            // Form the content URI that represents this specific product that was clicked on.
            Uri currentProductUri = ContentUris.withAppendedId(TableEntry.CONTENT_URI, productID);
            // Create new Intent to go to the DetailsActivity with the current URI on the data field.
            Intent detailsIntent = new Intent(context, DetailsActivity.class);
            detailsIntent.setData(currentProductUri);
            context.startActivity(detailsIntent);
        });

        // Update the TextViews with the attributes for the current product.
        viewHolder.productNameTextView.setText(productName);
        viewHolder.productAuthorTextView.setText(productAuthor);
        viewHolder.productPriceTextView.setText(Utils.formatPrice(productPrice));
        viewHolder.productQuantityTextView.setText(Utils.formatQuantity(productQuantity));
        viewHolder.supplierNameTextView.setText(productSupplier);
    }

    /** Class that caches all the child views necessary to build each item. */
    private static class ViewHolder {
        private RelativeLayout itemCard;
        private TextView productNameTextView,
                productAuthorTextView,
                productPriceTextView,
                productQuantityTextView,
                supplierNameTextView;

        /**
         * Public constructor so we can find all the views necessary.
         * @param view The parent view from which to find the children views.
         */
        private ViewHolder(View view) {
            itemCard = view.findViewById(R.id.item_card);
            productNameTextView = view.findViewById(R.id.item_product_name);
            productAuthorTextView = view.findViewById(R.id.item_product_author);
            productPriceTextView = view.findViewById(R.id.item_product_price);
            productQuantityTextView = view.findViewById(R.id.item_product_quantity);
            supplierNameTextView = view.findViewById(R.id.item_supplier_name);
        }
    }
}
