package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView musicList;
    private SeekBar seekBar;
    private TextView total,name,current;
    private ImageView pause;
    private int PREMISSION_DATA_TAG = 200;
    private MediaPlayer sound;
    private int currentaudio;
    private int seeking ;
    private ArrayList<AudioModal> allMusics;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkPremissions(Manifest.permission.READ_EXTERNAL_STORAGE, PREMISSION_DATA_TAG);
        checkPremissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, PREMISSION_DATA_TAG);
        allMusics = ALL(this);
        Adapter musicAdapter = new adapter(allMusics);
        musicList.setAdapter((ListAdapter) musicAdapter);
       /* if(savedInstanceState!=null)
        {
            try {
                Log.i("seeking", String.valueOf(savedInstanceState.getInt("Seeking")));
                currentaudio=savedInstanceState.getInt("LastState");
                sound.setDataSource(allMusics.get(savedInstanceState.getInt("LastState")).getPath());
                sound.prepare();
                sound.seekTo(savedInstanceState.getInt("Seeking"));
                sound.start();
                total.setText(savedInstanceState.getString("Total"));
                current.setText(savedInstanceState.getString("CurrentTime"));
                name.setText(savedInstanceState.getString("name"));
                StartMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/
        Log.i("TAG", "TAG");
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentaudio=position;
                view.setSelected(true);
                sound.stop();
                sound.reset();
                sound.release();
                sound = new MediaPlayer();
                seekBar.setProgress(0);
                try {
                    sound.setDataSource(allMusics.get(position).getPath());
                    sound.prepare();
                    sound.start();
                    name.setText(allMusics.get(position).getName());
                    soundTime();
                    current.setText("0:0");
                    StartMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(currentaudio<allMusics.size()-1) {
                    sound.stop();
                    sound.reset();
                    sound.release();
                    sound = new MediaPlayer();
                    seekBar.setProgress(0);
                    try {
                        sound.setDataSource(allMusics.get(currentaudio + 1).getPath());
                        sound.prepare();
                        sound.start();
                        name.setText(allMusics.get(currentaudio + 1).getName());
                        soundTime();
                        current.setText("0:0");
                        StartMusic();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currentaudio++;
            }
        });
    }
 /*   @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("Save","instance");
        outState.putInt("LastState",currentaudio);
        outState.putString("Total",total.getText().toString());
        outState.putString("name",name.getText().toString());
        outState.putString("CurrentTime",current.getText().toString());
        outState.putInt("Seeking",seeking);
        sound.reset();
        sound.stop();
        sound.release();
    }*/
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Save","Pause");
       // seeking=sound.getCurrentPosition();
    }
    public void init() {
        musicList = (ListView) findViewById(R.id.MusicList);
        total=(TextView)findViewById(R.id.TotalTime2);
        current=(TextView)findViewById(R.id.CurrentTime);
        name=(TextView)findViewById(R.id.MusicName);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        pause =(ImageView)findViewById(R.id.imageView);
        sound=new MediaPlayer();
        pause.setImageResource(R.drawable.ic_baseline_not_started_24);

    }
    public void checkPremissions(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PREMISSION_DATA_TAG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }

    }
    public void StartMusic()
    {
        new Thread() {
            @Override
            public void run() {
                synchronized (this)
                {
                while(sound.isPlaying())

                {
                    if (sound.isPlaying())
                    {
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
    public void Pause(View view)
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
    public void next(View view)
    {
        if(currentaudio<allMusics.size()-1 && currentaudio>=0) {
            sound.stop();
            sound.reset();
            sound.release();
            sound = new MediaPlayer();
            seekBar.setProgress(0);
            try {
                sound.setDataSource(allMusics.get(currentaudio + 1).getPath());
                sound.prepare();
                sound.start();
                name.setText(allMusics.get(currentaudio + 1).getName());
                soundTime();
                StartMusic();
                currentaudio++;
                current.setText("0:0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void back(View view)
    {
        if(currentaudio<allMusics.size()-1 && currentaudio>=0) {
            sound.stop();
            sound.reset();
            sound.release();
            sound = new MediaPlayer();
            seekBar.setProgress(0);
            try {
                sound.setDataSource(allMusics.get(currentaudio - 1).getPath());
                sound.prepare();
                sound.start();
                name.setText(allMusics.get(currentaudio - 1).getName());
                soundTime();
                StartMusic();
                currentaudio--;
                current.setText("0:0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    public ArrayList<AudioModal> ALL(final Context mContext) {
        ArrayList<AudioModal> audios = new ArrayList<AudioModal>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    audios.add(new AudioModal(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)), cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))));
                } while (cursor.moveToNext());
            }
        }
        return audios;
    }
    public class adapter extends BaseAdapter {
        private ArrayList<AudioModal> content = new ArrayList<AudioModal>();

        public adapter(ArrayList<AudioModal> content) {
            this.content = content;
        }
        @Override
        public int getCount() {
            return content.size();
        }
        @Override
        public Object getItem(int position) {
            return content.get(position).getName();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater lay = getLayoutInflater();
            View view = lay.inflate(R.layout.listitem, parent, false);
            TextView artistName = (TextView) view.findViewById(R.id.ArtistName);
            TextView musicName = (TextView) view.findViewById(R.id.NameMusic);
            ImageView musicImage = (ImageView) view.findViewById(R.id.musicimage);
            artistName.setText(content.get(position).getArtist());
            musicName.setText(content.get(position).getName());
            return view;
        }
    }
}
