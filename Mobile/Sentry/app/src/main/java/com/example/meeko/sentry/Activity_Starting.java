package com.example.meeko.sentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Starting extends AppCompatActivity {
    private ViewPager viewPager;
    private SlideAdapter myadapter;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myadapter = new SlideAdapter(this);
        viewPager.setAdapter(myadapter);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        String policeLoggedIn = pref.getString("policeLoggedIn", "");
        if (user != null&&!policeLoggedIn.equals("true")) {
            Intent home = new Intent(Activity_Starting.this, HomeActivity.class);
            startActivity(home);
            finish();

        }
        else if (policeLoggedIn.equals("true")&& user!=null){
            Intent home = new Intent(Activity_Starting.this, PoliceHomeActivity.class);
            startActivity(home);
            finish();
        }

    }
}
