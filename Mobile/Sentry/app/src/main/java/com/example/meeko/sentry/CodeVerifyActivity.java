package com.example.meeko.sentry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class CodeVerifyActivity extends AppCompatActivity {

    TextInputEditText first, second, third, fourth, fifth, sixth;
    FirebaseAuth auth;
    String vericode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_verify);

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        fifth = findViewById(R.id.fifth);
        sixth = findViewById(R.id.sixth);

        Bundle bundle = getIntent().getExtras();

        vericode = bundle.getString("vericode");
    }

    public void verify(View v){
        String input_code = first.getText().toString() + second.getText().toString() + third.getText().toString() + fourth.getText().toString() + fifth.getText().toString() + sixth.getText().toString();

        if (vericode != null){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vericode, input_code);
            signInWithPhone(credential);
        }
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent home = new Intent(CodeVerifyActivity.this, HomeActivity.class);
                            startActivity(home);
                            Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_SHORT).show();
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

}
