package com.gezelbom.feederapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Alex
 *
 * Custom CursorAdapter similair to simpleCursorAdapter, but since SimpleCursorAdapter cant take
 * ViewGroups as input CursorAdapter has been extended.
 */
public class FeedCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;
    private final int layout;
    private String[] from;
    private int[] to;


    /**
     * Constructor that takes the same input as SimpleCursorAdapter does
     */
    public FeedCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.to = to;
        this.from = from;
    }

    /**
     * Custom bindView to perform some actions and bind the cursor values to the correct view
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //The order of the different values as set when creating the cursorAdapter
        final int TYPE = 0;
        int START_DATE = 1;
        int FEED_LENGTH = 2;

        String text;
        final int typeColumn = cursor.getColumnIndex(FeederDBAdapter.COL_FEED_TYPE);
        final int feedLengthColumn = cursor.getColumnIndex(FeederDBAdapter.COL_FEED_LENGTH);

        //For each of the values in this "row" perform the appropriate action
        for (int i = 0; i < to.length; i++) {
            final View v = view.findViewById(to[i]);

            if (i == TYPE) {
                //Set all the childViews of the viewGroup to Invisible
                for (int j = 0; j < ((ViewGroup) v).getChildCount(); j++) {
                    ((ViewGroup) v).getChildAt(j).setVisibility(View.INVISIBLE);
                }
                //Get the correct View according to the int value and set it to visible
                View view1 = v.findViewById(Feed.FeedType.values()[cursor.getInt(typeColumn)].viewID);
                view1.setVisibility(View.VISIBLE);
            } else if (i == FEED_LENGTH) {
                if (cursor.getInt(feedLengthColumn) < 60) {
                    // Display the feedLength in Seconds if below the 60 seconds
                    text = cursor.getString(feedLengthColumn) + " Seconds";
                    ((TextView) v).setText(text);
                } else {
                    //Display the feedLength as Minutes without following seconds
                    int value = cursor.getInt(feedLengthColumn) / 60;
                    text = value + " Minutes";
                    ((TextView) v).setText(text);
                }
            } else {
                //Set the textView Value
                text = cursor.getString(cursor.getColumnIndex(from[i]));
                ((TextView) v).setText(text);
            }
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(layout, parent, false);
    }
}
