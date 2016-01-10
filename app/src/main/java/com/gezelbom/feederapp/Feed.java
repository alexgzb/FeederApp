package com.gezelbom.feederapp;

/**
 * Created by Alex
 */
public class Feed {

    public static final int FEED_TYPE_LEFT = 0;
    public static final int FEED_TYPE_RIGHT = 1;
    public static final int FEED_TYPE_BOTTLE = 2;

    enum FeedType {
        LEFT(FEED_TYPE_LEFT, R.id.type_feed_left),
        RIGHT(FEED_TYPE_RIGHT, R.id.type_feed_right),
        BOTTLE(FEED_TYPE_BOTTLE, R.id.type_feed_bottle);

        int type;
        int viewID;

        FeedType(int feedType, int id) {
            type = feedType;
            viewID = id;
        }
    }

    public final int feedType;
    public final String startDate;
    public final String endDate;
    public final int feedLength;


    public Feed(int feedType, String startDate, String endDate, int feedLength) {
        this.feedType = feedType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.feedLength = feedLength;
    }
}
