package com.wite.positionerwear;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG="TAG";
    private MediaPlayer mMediaPlayer;
    private ImageView image;
    private AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        image = (ImageView) findViewById(R.id.iamge_animation);
        image.setBackgroundResource(R.drawable.recordplayer);
        anim = (AnimationDrawable) image.getBackground();
        anim.start();






        String filename= getIntent().getStringExtra("file");
          File mFile=new File(filename);
          Uri mUri=Uri.parse(mFile.getAbsolutePath());

        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this,mUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  mMediaPlayer.setDataSource();

        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(PlayerActivity.this, "播放完毕", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "播放完毕" );
                anim.stop();
                finish();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                Toast.makeText(PlayerActivity.this, "播放错误", Toast.LENGTH_SHORT).show();
                anim.stop();
                Log.d(TAG, "播放出现错误 请联系牛牛牛 " );
                return false;
            }
        });



    }
    }



