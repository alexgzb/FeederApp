package com.gezelbom.feederapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Graph Activity that plots a XYPlot of the daily feed lengths uses AndroidPlot
 */
public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();

        XYPlot plot = (XYPlot) findViewById(R.id.plot);

        ArrayList<Long> dates = (ArrayList<Long>) intent.getSerializableExtra("dates");
        ArrayList<Integer> lengths = intent.getIntegerArrayListExtra("lengths");

        // Create the series from dates/lengths
        XYSeries series = new SimpleXYSeries(dates, lengths, "");

        // create formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesFormat.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels);

        // add the series' to the xyplot:
        plot.addSeries(series, seriesFormat);

        //No Decimal for the Y-axis
        plot.setRangeValueFormat(new DecimalFormat("0"));

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);

        //SetDomain Step level since it is a date
        plot.setDomainStep(XYStepMode.SUBDIVIDE, dates.size());

        //Format for the DomainValue since it is a Date
        plot.setDomainValueFormat(new Format() {
            private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            @Override
            public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
                // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                return sdf.format(object, buffer, field);
            }

            @Override
            public Object parseObject(String string, ParsePosition position) {
                return null;
            }
        });

    }

}
