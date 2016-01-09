package com.gezelbom.feederapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends ListActivity {

    private static final String TAG = "MainActivity";
    Button leftButton;
    Button rightButton;
    ImageView bottleButton;
    TextView textViewLastFeedStart;
    Context context;

    SimpleCursorAdapter cursorAdapter;
    FeederDBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        dbAdapter = new FeederDBAdapter(this);
        dbAdapter.open();

        updateLastFeed();
        updateFeedsList();

        textViewLastFeedStart = (TextView) findViewById(R.id.textView_last_feed_value);
        leftButton = (Button) findViewById(R.id.button_left);
        rightButton = (Button) findViewById(R.id.button_right);
        bottleButton = (ImageView) findViewById(R.id.button_bottle);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeed(FeederDBAdapter.FEED_TYPE_LEFT);
                Intent intent = new Intent(context, TimerActivity.class);
                startActivityForResult(intent, 1001);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeed(FeederDBAdapter.FEED_TYPE_RIGHT);
            }
        });

        bottleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeed(FeederDBAdapter.FEED_TYPE_BOTTLE);
            }
        });

    }

    private void updateFeedsList() {

        // Use the dbAdapter and fetch all rows method
        Cursor cursor = dbAdapter.fetchAllRows();
        if (cursor.getCount() == 0)
            return;
        // Create a fromColumns StringArray to define what columns to use
        String[] fromColumns = {FeederDBAdapter.COL_FEED_TYPE,
                FeederDBAdapter.COL_START_DATE,
                FeederDBAdapter.COL_FEED_LENGTH};
        // Create a toColumns int Array to define what views to use
        int[] toColumns = {R.id.TextView_feed_type,
                R.id.textView_feed_start_date,
                R.id.textView_feed_length};

        // Setup the adapter
        // 1(Context)Context,
        // 2,(int)the Layout for a singe row
        // 3(Cursor),The cursor containing the data
        // 4(StringArray) define the columns to us.
        // 5(intArray) the view of each column in the layout
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.feed_row, cursor,
                fromColumns, toColumns, 0);
        super.setListAdapter(cursorAdapter);

    }

    private void updateLastFeed() {
        Feed feed = dbAdapter.getLastFeed();
        if (feed != null)
            textViewLastFeedStart.setText(feed.startDate);
    }


    private void startFeed(int feedType) {
        dbAdapter.startFeed(feedType, getEpochTimeInInt());
    }


    private String getEpochTimeInInt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1001) {
            String endDate = data.getStringExtra(FeederDBAdapter.COL_END_DATE);
            int feedLength = data.getIntExtra(FeederDBAdapter.COL_FEED_LENGTH, 0);
            dbAdapter.stopFeed(endDate, feedLength);
//            cursorAdapter.notifyDataSetChanged();
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    cursorAdapter.notifyDataSetChanged();
                }

            };


        }
    }
}