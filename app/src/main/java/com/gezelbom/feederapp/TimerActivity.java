package com.gezelbom.feederapp;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    Chronometer timer;
    ImageButton stopButton;
    ImageButton playButton;
    ImageButton pauseButton;
    long elapsedTime = 0;
    long startedTime;
    long stoppedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        playButton = (ImageButton) findViewById(R.id.button_play);
        stopButton = (ImageButton) findViewById(R.id.button_stop);
        pauseButton = (ImageButton) findViewById(R.id.button_pause);
        timer = (Chronometer) findViewById(R.id.chronometer_timer);

        startedTime = SystemClock.elapsedRealtime();
        timer.setBase(startedTime);
        timer.start();
        playButton.setEnabled(false);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startedTime = SystemClock.elapsedRealtime();
                timer.setBase(SystemClock.elapsedRealtime() + stoppedTime);
                timer.start();
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.stop();
                stoppedTime = timer.getBase();
                elapsedTime += SystemClock.elapsedRealtime() - startedTime;
                pauseButton.setEnabled(false);
                playButton.setEnabled(true);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.stop();
                elapsedTime += SystemClock.elapsedRealtime() - startedTime;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String endDate = dateFormat.format(date);
                int elapsed = (int) (elapsedTime / 1000);

                // Put extra to the activity that is waiting for results and return
                Intent returnIntent = new Intent();
                returnIntent.putExtra(FeederDBAdapter.COL_END_DATE, endDate);
                returnIntent.putExtra(FeederDBAdapter.COL_FEED_LENGTH, elapsed);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        });
    }
}
