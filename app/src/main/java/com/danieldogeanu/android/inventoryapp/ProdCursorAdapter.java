package com.danieldogeanu.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

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
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
