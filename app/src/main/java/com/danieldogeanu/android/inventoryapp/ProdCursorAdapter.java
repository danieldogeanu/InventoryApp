package com.danieldogeanu.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
