package com.gezelbom.feederapp;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

/**
 * Activity that when started displays a timer and starts counting time. Returns an intent to the caller
 */
public class TimerActivity extends AppCompatActivity {

    Chronometer timer;
    ImageButton stopButton;
    ImageButton playButton;
    ImageButton lullabyButton;
    MediaPlayer mediaPlayer;
    long elapsedTime = 0;
    long startedTime;
    private boolean muted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        muted = true;

        playButton = (ImageButton) findViewById(R.id.button_play);
        stopButton = (ImageButton) findViewById(R.id.button_stop);
        lullabyButton = (ImageButton) findViewById(R.id.imageButton_lullaby);
        timer = (Chronometer) findViewById(R.id.chronometer_timer);

        //Start the timer
        startedTime = SystemClock.elapsedRealtime();
        timer.setBase(startedTime);
        timer.start();
        playButton.setEnabled(false);

        /*lullabyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (muted) {
                    lullabyButton.setImageResource(R.drawable.ic_action_music_white);
                    muted = false;
                    playLullaby();
                } else {
                    muted = true;
                    lullabyButton.setImageResource(R.drawable.ic_action_volume_mute_white);
                    stopLullaby();
                }
            }
        });*/

        //When stop button is pressed
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Stop the timer
                timer.stop();
//                stopLullaby();
                elapsedTime += SystemClock.elapsedRealtime() - startedTime;

                String endDate = MainActivity.getEpochTimeInInt();
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

    private void stopLullaby() {
        mediaPlayer.stop();
    }

    private void playLullaby() {
        mediaPlayer = MediaPlayer.create(this,R.raw.twinkle_twinkle);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
}
