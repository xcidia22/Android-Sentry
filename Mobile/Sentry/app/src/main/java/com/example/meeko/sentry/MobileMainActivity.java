package com.example.meeko.sentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class MobileMainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore database;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    EditText mobilePre, mobileNum, mobileName, mobileEmail, mobileVerCode;

    String numberFinal;
    String verificationCode;
    String firestoreMobile;

    Boolean boolLogin;

    String policeLoggedIn = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_login);



        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();


        boolLogin = false;

        mobilePre = findViewById(R.id.txtMobilePrefix);
        mobileNum = findViewById(R.id.txtMobileNum);
        mobileName = findViewById(R.id.txtMobileName);
        mobileEmail = findViewById(R.id.txtMobileEmail);
        mobileVerCode = findViewById(R.id.txtMobileVerCode);

        mobilePre.setFocusable(false);

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential){
                Toast.makeText(getApplicationContext(),"Verification Completed", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onVerificationFailed(FirebaseException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(getApplicationContext(), "Code sent", Toast.LENGTH_SHORT).show();
            }

        };
    }

    public void onClickSendVerification(View v){
        numberFinal = mobilePre.getText().toString() + mobileNum.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                numberFinal, 60, TimeUnit.SECONDS,this, mCallback
        );
    }

    public void onClickVerify(View v){
        String input_code = mobileVerCode.getText().toString();
        if(verificationCode != null){
            verifyPhoneNumber(verificationCode, input_code);
        }
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();

                            editor.putString("userName", mobileName.getText().toString());
                            editor.putString("userEmail", mobileEmail.getText().toString());
                            editor.putString("userUrl", "https://t3.ftcdn.net/jpg/00/64/67/52/240_F_64675209_7ve2XQANuzuHjMZXP3aIYIpsDKEbF5dD.jpg");
                            editor.putString("userPhone",numberFinal);
                            editor.commit();

                            database.collection("Officers")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                policeLoggedIn="false";
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    firestoreMobile = document.get("mobile").toString();
                                                    if (firestoreMobile.equals(numberFinal)){
                                                        policeLoggedIn = "true";
                                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = pref.edit();

                                                        editor.putString("policeLoggedIn", policeLoggedIn);
                                                        editor.commit();

                                                        Intent home = new Intent(MobileMainActivity.this, PoliceHomeActivity.class);
                                                        home.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        Toast.makeText(getApplicationContext(),"Welcome Officer!", Toast.LENGTH_LONG).show();
                                                        startActivity(home);

                                                    }

                                                }

                                                if (policeLoggedIn.equals("false")){
                                                    Intent home = new Intent(MobileMainActivity.this, HomeActivity.class);
                                                    home.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    finish();
                                                    startActivity(home);
                                                    Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {
                                                Toast.makeText(getApplicationContext(),"Error getting documents!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });




                        }
                        else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(),"User already Exist", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Wrong code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void verifyPhoneNumber(String verificationCode, String input_code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, input_code);
        signInWithPhone(credential);
        Intent intent = new Intent(this, LoadingScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);


    }
}
