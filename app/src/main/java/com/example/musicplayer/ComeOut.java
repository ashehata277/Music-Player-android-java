package com.example.musicplayer;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class ComeOut extends AppCompatActivity {
    private SeekBar seekBar;
    private MediaPlayer sound;
    private TextView total,name,current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_come_out);
        seekBar = (SeekBar)findViewById(R.id.seekBar3);
        total=(TextView)findViewById(R.id.TotalTime2);
        current=(TextView)findViewById(R.id.CurrentTime2);
        name=(TextView)findViewById(R.id.MusicName2);
        sound= new MediaPlayer();
        Intent comeIN = getIntent();
        if (Intent.ACTION_SEND.equals(comeIN.getAction()) && comeIN.getType() != null) {
            if(comeIN.getType().startsWith("audio/"))
            {
                Uri audioURI =Uri.parse(String.valueOf(comeIN.getData()));
                try {
                    sound.setDataSource(ComeOut.this,audioURI);
                    Log.i("TAG","TAg");
                    sound.prepare();
                    sound.start();
                    StartMusic();
                    soundTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                {
                    sound.seekTo(progress);
                    soundTime();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void soundTime()
    {
        seekBar.setMax(sound.getDuration());
        int trim = (seekBar.getMax()/1000);
        int m = trim/60;
        int s =trim%60;
        int trim0 = (seekBar.getProgress()/1000);
        int m0 = trim0/60;
        int s0= trim0 %60;
        total.setText(m +":"+s);
        current.setText(m0+":"+s0);
    }
    public void StartMusic() {
        new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    while (sound.isPlaying()) {
                        if (sound.isPlaying()) {
                            int soundDuration = sound.getDuration();
                            int currentPosition = 0;
                            seekBar.setMax(soundDuration);
                            while (currentPosition < soundDuration) {
                                try {
                                    currentPosition = sound.getCurrentPosition();
                                    seekBar.setProgress(currentPosition);
                                } catch (Exception exp) {
                                    exp.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }.start();
    }
    public void Pause2(View view)
    {
        if(sound.isPlaying())
        {
            sound.pause();
        }
        else
        {
            sound.start();
            StartMusic();
        }
    }
}