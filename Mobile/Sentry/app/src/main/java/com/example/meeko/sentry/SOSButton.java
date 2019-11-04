package com.example.meeko.sentry;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import io.paperdb.Paper;

public class SOSButton extends AppCompatActivity implements LocationListener {
    private TextView noteView, noteView2, TextNew;
    private EditText lockNumber1, lockNumber2, lockNumber3, lockNumber4, lockPassword;
    private Button btnVerify;
    private ImageButton SOSbtn;
    private PatternLockView mPatternLockView;
    Handler h = new Handler();


    String savedNumLock = "numKey";
    String savedPatternLock = "patternKey";
    String savedPasswordLock = "passwordKey";
    public LatLng[][] LocationController = new LatLng[8][];
    final String[] LocationNames = new String[8];

    public String CURRENT_USER;
    public String finalNumLock = "";
    public String finalPatternLock = "";
    public String finalPasswordLock = "";
    public LatLng temp;

    FusedLocationProviderClient mFusedLocationProviderClient;

    FirebaseFirestore db;
    PolygonTest mpolt = new PolygonTest();

    Runnable getLoc = new Runnable() {
        @Override
        public void run() {
            Log.v("IMARUNNING", "HEHE:");
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final CollectionReference LocationsRef = db.collection("Incidents");
                LocationsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            String Username = pref.getString("userName", "");
                            String tempID = "";
                            getDeviceLocation();
                            boolean Status = true;
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.v("WORKSWELL",Username+" "+documentSnapshot.get("VictimName")+ " "+documentSnapshot.getBoolean("Ongoing").toString());
                                if (Username.equals(documentSnapshot.get("VictimName"))&&documentSnapshot.getBoolean("Ongoing")) {
                                    Log.v("WORKSWELL","NAYSU");
                                    tempID = documentSnapshot.getId();
                                    Status = documentSnapshot.getBoolean("Ongoing");

                                    DocumentReference update = db.collection("Incidents").document(tempID);
                                    update.update("Latitude", temp.latitude);
                                    update.update("Longitude", temp.longitude);
                                    if (Status) {
                                        Log.v("IMARUNNING", "HAHA:");
                                        h.postDelayed(getLoc, 5000);
                                    }
                                }


                            }
                        }
                    }
                });

            }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosbutton);
        Log.v("WORKSNAYSU","!@#");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);



        db = FirebaseFirestore.getInstance();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        CURRENT_USER = pref.getString("userName","");
        Paper.init(this);
        finalNumLock = Paper.book().read(savedNumLock);
        finalPatternLock = Paper.book().read(savedPatternLock);
        finalPasswordLock = Paper.book().read(savedPasswordLock);

        //LocationNames
        LocationNames[0] = "NOHS Area";
        LocationNames[1] = "Libertad Area";
        LocationNames[2] = "Barangay33";
        LocationNames[3] = "Barangay38";
        LocationNames[4] = "Roxas Area";
        LocationNames[5] = "UNOR Area";
        LocationNames[6] = "Barangay36";
        LocationNames[7] = "Barangay16";
        //LocationCoordinates
        LocationController[0] = GeofenceQuad(new LatLng(10.661855, 122.947214), new LatLng(10.659604, 122.946088), new LatLng(10.66145672, 122.94232517), new LatLng(10.663608, 122.943417));
        LocationController[1] = GeofenceQuad(new LatLng(10.661855, 122.947214), new LatLng(10.659604, 122.946088), new LatLng(10.658033, 122.949555), new LatLng(10.659215, 122.953263));
        LocationController[2] = GeofenceQuad(new LatLng(10.661855, 122.947214), new LatLng(10.659215, 122.953263), new LatLng(10.661333, 122.954585), new LatLng(10.664423, 122.948214));
        LocationController[3] = GeofenceQuad(new LatLng(10.666213, 122.944749), new LatLng(10.664423, 122.948214), new LatLng(10.661855, 122.947214), new LatLng(10.663608, 122.943417));
        LocationController[4] = GeofenceQuad(new LatLng(10.659604, 122.946088), new LatLng(10.66145672, 122.94232517), new LatLng(10.657209, 122.940076), new LatLng(10.655139, 122.944627));
        LocationController[5] = GeofenceQuad(new LatLng(10.658033, 122.949555), new LatLng(10.659604, 122.946088), new LatLng(10.655139, 122.944627), new LatLng(10.654169, 122.947428));
        LocationController[6] = GeofenceQuad(new LatLng(10.668013, 122.940928), new LatLng(10.666213, 122.944749), new LatLng(10.66145672, 122.94232517), new LatLng(10.663683, 122.937615));
        LocationController[7] = GeofenceQuad(new LatLng(10.668013, 122.940928), new LatLng(10.669014, 122.938691), new LatLng(10.669242, 122.92824), new LatLng(10.663683, 122.937615));

        if (finalNumLock != null && !finalNumLock.equals("")) {
            setContentView(R.layout.number_verify);
            TextNew = (TextView) findViewById(R.id.note);
            lockNumber1 = (EditText) findViewById(R.id.lockNumber1);
            lockNumber2 = (EditText) findViewById(R.id.lockNumber2);
            lockNumber3 = (EditText) findViewById(R.id.lockNumber3);
            lockNumber4 = (EditText) findViewById(R.id.lockNumber4);
            SOSbtn = (ImageButton) findViewById(R.id.SOS);
            SOSbtn.setVisibility(View.VISIBLE);

            TextNew.setText("Enter Password");

            btnVerify = (Button) findViewById(R.id.btnVerify);

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputNumLock = lockNumber1.getText().toString() + lockNumber2.getText().toString() + lockNumber3.getText().toString() + lockNumber4.getText().toString();
                    if (inputNumLock.equals(finalNumLock)) {
                        Toast.makeText(SOSButton.this, "Correct number code", Toast.LENGTH_SHORT).show();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startMain);
                        finish();

                    } else {
                        Toast.makeText(SOSButton.this, "Incorrect number code", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (finalPasswordLock != null && !finalPasswordLock.equals("")) {
            setContentView(R.layout.password_verify);
            SOSbtn = (ImageButton) findViewById(R.id.SOS2);
            SOSbtn.setVisibility(View.VISIBLE);
            TextNew = (TextView) findViewById(R.id.note2);
            TextNew.setText("Enter Password");

            lockPassword = (EditText) findViewById(R.id.lockPassword);

            btnVerify = (Button) findViewById(R.id.btnVerify);

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputPasswordLock = lockPassword.getText().toString();
                    if (inputPasswordLock.equals(finalPasswordLock)) {
                        Toast.makeText(SOSButton.this, "Correct password", Toast.LENGTH_SHORT).show();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startMain);
                        finish();

                    } else {
                        Toast.makeText(SOSButton.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (finalPatternLock != null && !finalPatternLock.equals("")) {
            setContentView(R.layout.pattern_verify);
            SOSbtn = (ImageButton) findViewById(R.id.SOSverifybtn);
            SOSbtn.setVisibility(View.VISIBLE);
            setContentView(R.layout.pattern_verify);
            TextNew = (TextView) findViewById(R.id.note3);
            TextNew.setText("Enter Pattern");

            mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);

            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    String inputPatternLock = PatternLockUtils.patternToString(mPatternLockView, pattern);
                    if (inputPatternLock.equals(finalPatternLock)) {
                        Toast.makeText(SOSButton.this, "Correct pattern", Toast.LENGTH_SHORT).show();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startMain);
                        finish();

                    } else {
                        Toast.makeText(SOSButton.this, "Incorrect pattern", Toast.LENGTH_SHORT).show();
                        mPatternLockView.clearPattern();
                    }

                }

                @Override
                public void onCleared() {

                }
            });
        } else {
            finish();
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        CollectionReference LocationsRef3 = db.collection("Incidents");
        LocationsRef3.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.v("WORKINGTAG","MEHHH");
                ImageView sos1 = (ImageView)findViewById(R.id.SOS);
                ImageView sos2 = (ImageView)findViewById(R.id.SOS2);
                ImageView sos3 = (ImageView)findViewById(R.id.SOSverifybtn);
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String userName = documentSnapshot.getString("VictimName");
                    boolean status = documentSnapshot.getBoolean("Ongoing");
                    if (userName.equals(CURRENT_USER) && status) {
                        if(finalNumLock != null && !finalNumLock.equals(""))
                            sos1.setEnabled(false);
                        if(finalPasswordLock != null && !finalPasswordLock.equals(""))
                            sos2.setEnabled(false);
                        if(finalPatternLock != null && !finalPatternLock.equals(""))
                            sos3.setEnabled(false);
                        Toast.makeText(SOSButton.this, "You are still active!\nDisabled Alert Button", Toast.LENGTH_SHORT).show();
                        Log.v("WORKINGTAG",""+status);
                        return;
                    } else if (userName.equals(CURRENT_USER)) {
                        if(finalNumLock != null && !finalNumLock.equals(""))
                            sos1.setEnabled(true);
                        if(finalPasswordLock != null && !finalPasswordLock.equals(""))
                            sos2.setEnabled(true);
                        if(finalPatternLock != null && !finalPatternLock.equals(""))
                            sos3.setEnabled(true);
                    } else {
                        if(finalNumLock != null && !finalNumLock.equals(""))
                            sos1.setEnabled(true);
                        if(finalPasswordLock != null && !finalPasswordLock.equals(""))
                            sos2.setEnabled(true);
                        if(finalPatternLock != null && !finalPatternLock.equals(""))
                            sos3.setEnabled(true);
                    }
                }
            }
        });
    }

    public void onSOSBtnClick(View v) {
        getDeviceLocation();
            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setNeutralButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                            String Username = pref.getString("userName","");
                                            SharedPreferences pref2 = getApplicationContext().getSharedPreferences("ContactNumbers",MODE_PRIVATE);
                                            if(pref2.getString("Contact1","")!="")
                                                sendSMS(pref2.getString("Contact1",""), "Help!! Alarm has been triggered by: " + Username + " \nhttp://maps.google.com/?q="+temp.latitude+","+temp.longitude);
                                            if(pref2.getString("Contact2","")!="")
                                                sendSMS(pref2.getString("Contact2",""), "Help!! Alarm has been triggered by: " + Username + " \nhttp://maps.google.com/?q="+temp.latitude+","+temp.longitude);
                                        }
                                    },2000);

                                    //INSERT CODE FOR SOS
                                    if (!temp.equals(null)) {
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                int x=0;
                                                if (isNetworkConnected()) {
                                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                                    Map<String, Object> newIncident = new HashMap<>();
                                                    String Username = pref.getString("userName", "");
                                                    boolean status = true;
                                                    long time = System.currentTimeMillis();
                                                    String ts = getDate(time);
                                                    newIncident.put("VictimName", Username);
                                                    newIncident.put("Latitude", temp.latitude);
                                                    newIncident.put("Longitude", temp.longitude);
                                                    newIncident.put("OLatitude", temp.latitude);
                                                    newIncident.put("OLongitude", temp.longitude);
                                                    newIncident.put("Witnesses","");
                                                    newIncident.put("IncidentID", Username.charAt(0) + harmonyRound(temp.latitude));
                                                    newIncident.put("Timestamp", ts);
                                                    newIncident.put("Case", "N/A");
                                                    newIncident.put("Category","N/A");

                                                    for (int i = 0; i < LocationController.length; i++) {
                                                        if (mpolt.PointIsInRegion(temp.latitude, temp.longitude, LocationController[i])) {
                                                            newIncident.put("IncidentOrigin", LocationNames[i]);

                                                        } else {
                                                            newIncident.put("IncidentOrigin", "OutsideBacolodArea");
                                                        }
                                                    }
                                                    newIncident.put("Ongoing", status);
                                                    db.collection("Incidents").add(newIncident).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            h.post(getLoc);
                                                        }
                                                    });

                                                    x++;
                                                    MyProcessForExit(this);


                                                }
                                                Log.v("TAGTAGTAG",""+x);
                                                if(!isNetworkConnected()) {
                                                    h.postDelayed(this, 5000);
                                                }
                                            }
                                        }, 5000);

                                        Intent intent = new Intent(SOSButton.this, ICEtriggeredScreen.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(SOSButton.this, "Cannot locate your location!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    ).create();
            alertDialog.setTitle("SOS has been triggered!");
            alertDialog.setMessage("00:10");
            alertDialog.show();   //

            new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    alertDialog.setMessage("You have " + (millisUntilFinished / 1000)+"s to confirm!");
                }

                @Override
                public void onFinish() {
                    alertDialog.dismiss();
                }
            }.start();



        }




    @Override
    public void onLocationChanged(Location location) {
        temp = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(this, ""+location.getLatitude(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }




    @Override
    public void onProviderDisabled(String provider) {

    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("MM-dd-yyyy hh:mm:ss", cal).toString();
        return date;
    }
    public String harmonyRound(double value){
        DecimalFormat threeDForm = new DecimalFormat("######");
        String stringValue = threeDForm.format(value * 100000);
        stringValue.replaceAll(".", "");
        return stringValue;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    public LatLng[] GeofenceQuad(LatLng coor1,LatLng coor2,LatLng coor3,LatLng coor4) {
        LatLng[] lat = new LatLng[4];
        lat[0]=coor1;
        lat[1]=coor2;
        lat[2]=coor3;
        lat[3]=coor4;
        return lat;

    }
    private void getDeviceLocation(){

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            temp = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        }
                    }
                });

        }catch (SecurityException e){
        }
    }
    private void  MyProcessForExit(Runnable run)
    {
        h.removeCallbacksAndMessages(run);
// close activity or whatever
        finish();
    }
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }






}



