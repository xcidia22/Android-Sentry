package com.example.meeko.sentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ICEActivity extends AppCompatActivity {
    SmsManager smsManager;
    FirebaseAuth auth;
    FirebaseUser user;
    String name;
    private final static String MY_PREFS_NAME = "ContactNumbers";
    String policeLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ice);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        policeLoggedIn = pref.getString("policeLoggedIn", "");
        String userName = pref.getString("userName", "");

        smsManager = SmsManager.getDefault();
        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        EditText edt1 = (EditText) findViewById(R.id.editText);
        EditText edt2= (EditText)findViewById(R.id.editText2);
        edt1.setHint(prefs.getString("Contact1","No Contact"));
        edt2.setHint(prefs.getString("Contact2","No Contact"));


    }


    public void onSave(View view){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        EditText edt1 = (EditText) findViewById(R.id.editText);
        EditText edt2= (EditText)findViewById(R.id.editText2);
        String Contact1,Contact2,Contact3;
        if(edt1.getText().toString().length()==10) {
            Contact1 = "+63"+edt1.getText().toString();
            prefs.putString("Contact1", Contact1);
        }
        if(edt2.getText().toString().length()==10) {
            Contact2 = "+63"+edt2.getText().toString();
            prefs.putString("Contact2", Contact2);
        }
        if(pref.getString("Contact1","")!=""||pref.getString("Contact2","")!="") {
            Toast.makeText(this, "Contacts successfully registered!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Save unsuccessful!", Toast.LENGTH_SHORT).show();
        }
        prefs.commit();
    }

    public void clearbtn1(View view){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("Contact1");
        editor.apply();
        EditText edt1=(EditText)findViewById(R.id.editText);
        edt1.setHint("No Contact");
    }
    public void clearbtn2(View view){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("Contact2");
        editor.apply();
        EditText edt2=(EditText)findViewById(R.id.editText2);
        edt2.setHint("No Contact");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(policeLoggedIn.equals("true")){
            startActivity(new Intent(this,PoliceHomeActivity.class));
        }else{
            startActivity(new Intent(this,HomeActivity.class));
        }
    }
}