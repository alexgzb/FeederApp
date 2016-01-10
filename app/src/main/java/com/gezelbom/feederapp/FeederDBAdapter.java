package com.gezelbom.feederapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alex
 */
public class FeederDBAdapter {

    private DatabaseHelper helper;
    private SQLiteDatabase database;
    private final Context context;

    private static final String DATABASE_NAME = "feederAppDB";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "feeds";
    private static final String TAG = "FeederDBAdapter";
    public static final String COL_UID = "_id";
    public static final String COL_FEED_TYPE = "type";
    public static final String COL_START_DATE = "startDate";
    public static final String COL_END_DATE = "endDate";
    public static final String COL_FEED_LENGTH = "feedLength";

    private String startDate;
    private int feedType;

    // SQL Statements
    private static final String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE + "("
            + COL_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FEED_TYPE + " INTEGER NOT NULL, "
            + COL_START_DATE + " TEXT NOT NULL, "
            + COL_END_DATE + " TEXT NOT NULL, "
            + COL_FEED_LENGTH + " INTEGER NOT NULL);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
            + DATABASE_TABLE + ";";

    /**
     * Constructor for the DBAdapter
     *
     * @param context
     *            Takes the context from which it has been initialised
     */
    public FeederDBAdapter(Context context) {
        this.context = context;
    }

    /**
     * Method that where database object is created and ready to be used.
     *
     * @return an instance of this class
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        helper = new DatabaseHelper(context);
        database = helper.getWritableDatabase();
    }

    public void dropAndCreate() throws SQLException {
        helper.dropAndCreate(database);
    }

    public Feed getLastFeed() {
        String[] columns = new String[] { COL_UID, COL_FEED_TYPE, COL_START_DATE, COL_END_DATE, COL_FEED_LENGTH};
        Cursor cursor = database.query(DATABASE_TABLE, columns, null, null, null, null, COL_UID + " DESC");
        cursor.moveToFirst();
        if (cursor.getCount() == 0)
            return null;
        int feedType = cursor.getInt(1);
        String startDate = cursor.getString(2);
        String endDate = cursor.getString(3);
        int feedLength = cursor.getInt(4);
        return new Feed(feedType,startDate,endDate, feedLength);
    }

    /**
     * Close the DBHelper object if it exists
     */
    public void close() {
        if (helper != null) {
            helper.close();
        }
    }

    /**
     *Create a new row using execSQL, gives possibility to use functions during sqls
     * @param feedType
     * @param startDate
     * @param endDate
     * @param feedLength
     */
    private void createRow(int feedType, String startDate, String endDate, int feedLength) {
        //Must use the execSQL to be able to use the datetime() function.
        String insertValue = "INSERT INTO " + DATABASE_TABLE + "("
                + COL_FEED_TYPE + ", "
                + COL_START_DATE + ", "
                + COL_END_DATE + ", "
                + COL_FEED_LENGTH
                + ") VALUES("
                + feedType + ", "
                + startDate + ", "
                + endDate + ", "
                + feedLength + ");";
        database.execSQL(insertValue);
        Log.d(TAG, "Inserting to db");
    }

    /**
     * Create a new row using Insert convenience method from the database
     *
     * @param feedType
     * @param startDate
     * @param endDate
     * @param feedLength
     * @return
     */
    private long insertRow(int feedType, String startDate, String endDate, int feedLength) {
        ContentValues values = new ContentValues();
        values.put(COL_FEED_TYPE, feedType);
        values.put(COL_START_DATE, startDate);
        values.put(COL_END_DATE, endDate);
        values.put(COL_FEED_LENGTH, feedLength);
        return database.insert(DATABASE_TABLE, null, values);
    }

    public void startFeed (int feedType, String startDate) {
        this.startDate = startDate;
        this.feedType = feedType;
    }

    public boolean stopFeed (String endDate, int feedLength) {
        long row = insertRow(this.feedType, this.startDate, endDate, feedLength);
        return (row != -1);
    }

    /**
     * Method that deletes all rows from the table
     */
    public void deleteAllRows() {
        Log.d(TAG, "Deleting all rows in table");
        int rows = database.delete(DATABASE_TABLE, null, null);
        Log.d(TAG, "Deleted " + rows + " rows");
    }


    /**
     * Returns an int row count
     * @return
     */
    public int getCount() {
        return database.query(DATABASE_TABLE,
                new String[] { COL_UID}, null, null,
                null, null, null).getCount();
    }

    /**
     * Returns an Cursor object containing all rows from PRIMETABLES.
     * @return
     */
    public Cursor fetchAllRows() {

        Cursor cursor = database.query(DATABASE_TABLE,
                new String[] {COL_UID, COL_FEED_TYPE, COL_START_DATE, COL_END_DATE, COL_FEED_LENGTH}, null, null, null, null, COL_UID + " DESC");

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            Log.d(TAG, "Creating Database");
        }

        public void dropAndCreate(SQLiteDatabase db) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion);
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }
}
