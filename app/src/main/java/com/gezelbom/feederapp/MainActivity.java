package com.gezelbom.feederapp;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends ListActivity {

    private static final String TAG = "MainActivity";
    Button leftButton;
    Button rightButton;
    Button graphButton;
    ImageView bottleButton;
    TextView textViewLastFeedStart;
    Context context;

    FeedCursorAdapter cursorAdapter;
    FeederDBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //Declare the views
        textViewLastFeedStart = (TextView) findViewById(R.id.textView_last_feed_value);
        leftButton = (Button) findViewById(R.id.button_left);
        rightButton = (Button) findViewById(R.id.button_right);
        bottleButton = (ImageView) findViewById(R.id.button_bottle);
        graphButton = (Button) findViewById(R.id.button_graph);

        //Create a dbAdapter and open the connection to the DB
        dbAdapter = new FeederDBAdapter(this);
        dbAdapter.open();
        //dbAdapter.dropAndCreate();


        //OnclickListeners for the Three Main buttons. The trigger the StartFeed
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeed(Feed.FEED_TYPE_LEFT);
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeed(Feed.FEED_TYPE_RIGHT);
            }
        });
        bottleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeed(Feed.FEED_TYPE_BOTTLE);
            }
        });

        //OnclickListener for the graph button
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the cursor with the daily feed lengths
                Cursor cursor = dbAdapter.getFeedsPerDay();
                ArrayList<Long> dates = new ArrayList<>(cursor.getCount());
                ArrayList<Integer> lengths = new ArrayList<>(cursor.getCount());

                //The DateFormat which the cursor uses
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                //Get values for the graph from the cursor
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    try {
                        //parse the value as a date to get it as a long in millis
                        dates.add(sdf.parse(cursor.getString(0)).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Divide feed length by 60 for minutes
                    lengths.add(cursor.getInt(1) / 60);
                }

                //Pass the data to the Intent and start Activity GraphActivity
                Intent intent = new Intent(context, GraphActivity.class);
                intent.putExtra("dates", dates);
                intent.putIntegerArrayListExtra("lengths", lengths);
                startActivity(intent);
            }
        });

        //Update the Views
        updateLastFeed();
        updateFeedsList();
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
        int[] toColumns = {R.id.layout_feed_type,
                R.id.textView_feed_start_date,
                R.id.textView_feed_length};

        // Setup the adapter
        // 1(Context)Context,
        // 2,(int)the Layout for a singe row
        // 3(Cursor),The cursor containing the data
        // 4(StringArray) define the columns to us.
        // 5(intArray) the view of each column in the layout
        cursorAdapter = new FeedCursorAdapter(this, R.layout.feed_row, cursor,
                fromColumns, toColumns, 0);
        super.setListAdapter(cursorAdapter);

    }

    /**
     * Update the lastFeed textView
     */
    private void updateLastFeed() {
        Feed feed = dbAdapter.getLastFeed();
        if (feed != null)
            textViewLastFeedStart.setText(feed.startDate);
    }


    /**
     * Calls the db to store the feedType and startDate as state in the DBAdapter
     *
     * @param feedType the type of the current feed to store
     */
    private void startFeed(int feedType) {
        dbAdapter.startFeed(feedType, getEpochTimeInInt());
        Intent intent = new Intent(context, TimerActivity.class);
        startActivityForResult(intent, 1001);
    }


    /**
     * Method that creates a String of the current dateTime as a String formated as SQLiteDB requires
     *
     * @return DateTime as String in the format yyyy-MM-dd HH:mm:ss
     */
    public static String getEpochTimeInInt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Stop button pressed in timer activity
        if (resultCode == RESULT_OK && requestCode == 1001) {

            //Get the values to store from the intent
            String endDate = data.getStringExtra(FeederDBAdapter.COL_END_DATE);
            int feedLength = data.getIntExtra(FeederDBAdapter.COL_FEED_LENGTH, 0);

            //Stop the feed and store the value in the database
            dbAdapter.stopFeed(endDate, feedLength);

            //Update the list and the textView
            updateFeedsList();
            updateLastFeed();
            //Delay 10800000 = 3 hours
            scheduleNotification(10800000);


        }
    }

    /**
     * Schedule a notification when delay has passed
     */
    private void scheduleNotification(long delay) {
        //Intent and pendingIntent to start MainActivity on notification click
        Intent returnIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, returnIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Configure the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("FeederApp");
        builder.setContentText("3 hours since baby's last meal");
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);
        builder.setSmallIcon(R.drawable.ic_not_icon);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        //Intent and Pending intent to publish the notification
        //and add the notification as an extra to the intent
        Intent intent = new Intent(this, NotificationPublish.class);
        intent.putExtra(NotificationPublish.NOTIFICATION_ID, 1002);
        intent.putExtra(NotificationPublish.NOTIFICATION, builder.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Create an AlarmManager and schedule the Notification to the future currently 3hours
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtMillis = SystemClock.elapsedRealtime() + delay;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);

    }
}