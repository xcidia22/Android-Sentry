package com.example.meeko.sentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.Share;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HelpCentreActivity extends AppCompatActivity {
    RatingBar mRatingBar;
    TextView mRatingScale;
    EditText mFeedback;
    Button mSendFeedback;
    String policeLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_centre);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        policeLoggedIn = pref.getString("policeLoggedIn", "");



        mRatingBar = findViewById(R.id.ratingBar);
        mRatingScale=findViewById(R.id.tvRatingScale);
        mFeedback=findViewById(R.id.etFeedback);
        mSendFeedback= findViewById(R.id.btnSubmit);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Very bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome. I love it");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });
        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedback.getText().toString().isEmpty()) {
                    Toast.makeText(HelpCentreActivity.this, "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                }
                else if(!isNetworkConnected()){
                    Toast.makeText(HelpCentreActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    String name = pref.getString("userName", "");

                    Map<String, Object> newReview = new HashMap<>();
                    String Username = name;
                    newReview.put("Rating",mRatingBar.getNumStars());
                    newReview.put("Review",mFeedback.getText());
                    newReview.put("email",pref.getString("userEmail",""));
                    db.collection("Reviews").add(newReview);



                    mFeedback.setText("");
                    mRatingBar.setRating(0);
                    Toast.makeText(HelpCentreActivity.this, "Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(policeLoggedIn.equals("true")){
            startActivity(new Intent(this,PoliceHomeActivity.class));
        }
        startActivity(new Intent(this,HomeActivity.class));
    }
}
