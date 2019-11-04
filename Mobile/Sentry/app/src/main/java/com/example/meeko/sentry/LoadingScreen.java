package com.example.meeko.sentry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        long duration = 5000;
        rotate.setDuration(duration);
        rotate.setRepeatCount(Animation.INFINITE);
        ImageView img = (ImageView)findViewById(R.id.SentryLoadingImg);
        img.startAnimation(rotate);
    }
}
