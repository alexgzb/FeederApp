package com.gezelbom.feederapp;

/**
 * Created by Alex
 */
public class Feed {
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
