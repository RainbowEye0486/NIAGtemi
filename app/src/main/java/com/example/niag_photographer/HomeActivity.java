package com.example.niag_photographer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class HomeActivity  extends ActivityController{
    private static final String TAG = "Home_page";
    private Button photo_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        photo_btn = (Button)findViewById(R.id.photo_btn);
        photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation bounce = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.bounce_animation);
                MediaPlayer click = MediaPlayer.create(HomeActivity.this, R.raw.click);
                click.start();
                photo_btn.startAnimation(bounce);
                Intent intent = new Intent(HomeActivity.this, PictureActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                Log.d(TAG, "onClick: photo button");
            }
        });
    }
}