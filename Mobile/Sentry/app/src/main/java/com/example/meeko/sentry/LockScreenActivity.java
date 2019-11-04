package com.example.meeko.sentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompleteEvent;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;

import org.w3c.dom.Text;

import java.util.List;

import io.paperdb.Paper;
import io.reactivex.functions.Consumer;

public class LockScreenActivity extends AppCompatActivity {

    private TextView noteView, noteView2;
    private EditText lockNumber1, lockNumber2, lockNumber3, lockNumber4, lockPassword;
    private Button btnSaveLock, btnNoLock, btnNumberLock, btnPatternLock, btnPasswordLock, btnVerify;
    private PatternLockView mPatternLockView;


    String savedNumLock = "numKey";
    String savedPatternLock = "patternKey";
    String savedPasswordLock = "passwordKey";

    public String inputNumLock = "";
    public String inputPatternLock = "";
    public String inputPasswordLock = "";

    public String finalNumLock = "";
    public String finalPatternLock = "";
    public String finalPasswordLock = "";
    public String lockTypeString = "";

    int lockType;

    String policeLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        policeLoggedIn = pref.getString("policeLoggedIn", "");
        Paper.init(this);
        finalNumLock = Paper.book().read(savedNumLock);
        finalPatternLock = Paper.book().read(savedPatternLock);
        finalPasswordLock = Paper.book().read(savedPasswordLock);


        if (finalNumLock != null && !finalNumLock.equals("")) {
            setContentView(R.layout.number_verify);

            lockNumber1 = (EditText) findViewById(R.id.lockNumber1);
            lockNumber2 = (EditText) findViewById(R.id.lockNumber2);
            lockNumber3 = (EditText) findViewById(R.id.lockNumber3);
            lockNumber4 = (EditText) findViewById(R.id.lockNumber4);

            btnVerify = (Button) findViewById(R.id.btnVerify);

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputNumLock = lockNumber1.getText().toString() + lockNumber2.getText().toString() + lockNumber3.getText().toString() + lockNumber4.getText().toString();
                    if (inputNumLock.equals(finalNumLock)) {
                        Toast.makeText(LockScreenActivity.this, "Correct number code", Toast.LENGTH_SHORT).show();
                        getScreenLayout();

                    } else {
                        Toast.makeText(LockScreenActivity.this, "Incorrect number code", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (finalPasswordLock != null && !finalPasswordLock.equals("")) {
            setContentView(R.layout.password_verify);

            lockPassword = (EditText) findViewById(R.id.lockPassword);

            btnVerify = (Button) findViewById(R.id.btnVerify);

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputPasswordLock = lockPassword.getText().toString();
                    if (inputPasswordLock.equals(finalPasswordLock)) {
                        Toast.makeText(LockScreenActivity.this, "Correct password", Toast.LENGTH_SHORT).show();
                        getScreenLayout();

                    } else {
                        Toast.makeText(LockScreenActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (finalPatternLock != null && !finalPatternLock.equals("")) {
            setContentView(R.layout.pattern_verify);
            ImageButton SOSbtn = (ImageButton) findViewById(R.id.SOSverifybtn);
            SOSbtn.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(LockScreenActivity.this, "Correct pattern", Toast.LENGTH_SHORT).show();
                        getScreenLayout();

                    } else {
                        Toast.makeText(LockScreenActivity.this, "Incorrect pattern", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCleared() {

                }
            });
        } else {
            getScreenLayout();

        }

//        if (finalNumLock != null && !finalNumLock.equals("")){
//            setContentView(R.layout.number_verify);
//
//            lockNumber1 = (EditText)findViewById(R.id.lockNumber1);
//            lockNumber2 = (EditText)findViewById(R.id.lockNumber2);
//            lockNumber3 = (EditText)findViewById(R.id.lockNumber3);
//            lockNumber4 = (EditText)findViewById(R.id.lockNumber4);
//
//            btnVerify = (Button) findViewById(R.id.btnVerify);
//
//            btnVerify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String inputNumLock = lockNumber1.getText().toString() + lockNumber2.getText().toString() + lockNumber3.getText().toString() + lockNumber4.getText().toString();
//                        if (inputNumLock.equals(finalNumLock)){
//                            Toast.makeText(LockScreenActivity.this, "Correct number code", Toast.LENGTH_SHORT).show();
//                            setContentView(R.layout.lockscreen);
//                        }
//                        else {
//                            Toast.makeText(LockScreenActivity.this, "Incorrect number code", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//            });
//        }
//
//        else if (finalPattern != null && !finalPattern.equals("")) {
//            setContentView(R.layout.pattern_verify);
//
//            mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
//            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
//                @Override
//                public void onStarted() {
//
//                }
//
//                @Override
//                public void onProgress(List<PatternLockView.Dot> progressPattern) {
//
//                }
//
//                @Override
//                public void onComplete(List<PatternLockView.Dot> pattern) {
//                    String inputPattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
//                    if (inputPattern.equals(finalPattern)){
//                        Toast.makeText(LockScreenActivity.this, "Correct pattern", Toast.LENGTH_SHORT).show();
//                        setContentView(R.layout.lockscreen);
//
//                    }
//                    else {
//                        Toast.makeText(LockScreenActivity.this, "Incorrect pattern", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCleared() {
//
//                }
//            });
//
//        }
//


//        if (lockID.equals("") || lockID.equals("0")){
//            setContentView(R.layout.lockscreen);
//            Toast.makeText(LockScreenActivity.this, "ARI KO DI", Toast.LENGTH_SHORT).show();
//            noteView = (TextView)findViewById(R.id.noteView);
//            noteView2 = (TextView)findViewById(R.id.noteView2);
//            lockNumber1 = (EditText)findViewById(R.id.lockNumber1);
//            lockNumber2 = (EditText)findViewById(R.id.lockNumber2);
//            lockNumber3 = (EditText)findViewById(R.id.lockNumber3);
//            lockNumber4 = (EditText)findViewById(R.id.lockNumber4);
//            lockPassword = (EditText)findViewById(R.id.lockPassword);
//
//            btnSaveLock = (Button) findViewById(R.id.btnSaveLock);
//            btnNoLock = (Button) findViewById(R.id.btnNoLock);
//            btnNumberLock = (Button) findViewById(R.id.btnNumberLock);
//            btnPatternLock = (Button) findViewById(R.id.btnPatternLock);
//            btnPasswordLock = (Button) findViewById(R.id.btnPasswordLock);
//
//            mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
//        }
//
//        else if (lockID.equals("1")){
//            if (savedNumLock.equals("")){
//                setContentView(R.layout.lockscreen);
//
//                lockNumber1 = (EditText)findViewById(R.id.lockNumber1);
//                lockNumber2 = (EditText)findViewById(R.id.lockNumber2);
//                lockNumber3 = (EditText)findViewById(R.id.lockNumber3);
//                lockNumber4 = (EditText)findViewById(R.id.lockNumber4);
//
//                btnSaveLock = (Button) findViewById(R.id.btnSaveLock);
//
//                inputNumLock = lockNumber1.getText().toString() + lockNumber2.getText().toString() + lockNumber3.getText().toString() + lockNumber4.getText().toString();
//                editor.putString("inputNumLock", inputNumLock);
//                editor.commit();
//            }
//            else {
//                setContentView(R.layout.number_verify);
//
//                EditText lockNumber1 = (EditText)findViewById(R.id.lockNumber1);
//                EditText lockNumber2 = (EditText)findViewById(R.id.lockNumber2);
//                EditText lockNumber3 = (EditText)findViewById(R.id.lockNumber3);
//                EditText lockNumber4 = (EditText)findViewById(R.id.lockNumber4);
//
//                Button btnVerify = (Button) findViewById(R.id.btnVerify);
//
//                savedNumLock = pref.getString("savedNumLock", "");
//                inputNumLock = lockNumber1.getText().toString() + lockNumber2.getText().toString() + lockNumber3.getText().toString() + lockNumber4.getText().toString();
//
//                btnVerify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (savedNumLock.equals(inputNumLock)){
//                            setContentView(R.layout.lockscreen);
//                            Toast.makeText(LockScreenActivity.this, "Correct number code", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(LockScreenActivity.this, "Incorrect number code", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }
//        else if (lockID.equals("2")){
//            if (savedPatternLock.equals("")) {
//                setContentView(R.layout.lockscreen);
//
//                mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
//                mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
//                    @Override
//                    public void onStarted() {
//
//                    }
//
//                    @Override
//                    public void onProgress(List<PatternLockView.Dot> progressPattern) {
//
//                    }
//
//                    @Override
//                    public void onComplete(List<PatternLockView.Dot> pattern) {
//                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = pref.edit();
//
//                        inputPatternLock = PatternLockUtils.patternToString(mPatternLockView, pattern);
//                        editor.putString("inputPatternLock", inputPatternLock);
//                        editor.commit();
//                    }
//
//                    @Override
//                    public void onCleared() {
//
//                    }
//                });
//            }
//            else {
//                setContentView(R.layout.pattern_verify);
//
//                mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
//
//                mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
//                    @Override
//                    public void onStarted() {
//
//                    }
//
//                    @Override
//                    public void onProgress(List<PatternLockView.Dot> progressPattern) {
//
//                    }
//
//                    @Override
//                    public void onComplete(List<PatternLockView.Dot> pattern) {
//                        inputPatternLock = PatternLockUtils.patternToString(mPatternLockView, pattern);
//                        if (savedPatternLock.equals(inputPatternLock)){
//                            setContentView(R.layout.lockscreen);
//                            Toast.makeText(LockScreenActivity.this, "Correct pattern", Toast.LENGTH_SHORT).show();
//
//                        }
//                        else {
//                            Toast.makeText(LockScreenActivity.this, "Incorrect pattern", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onCleared() {
//
//                    }
//                });
//            }
//        }
//        else if (lockID.equals("3")){
//            if (savedPasswordLock.equals("")){
//                setContentView(R.layout.lockscreen);
//
//                lockPassword = (EditText)findViewById(R.id.lockPassword);
//
//                btnSaveLock = (Button) findViewById(R.id.btnSaveLock);
//
//                inputPasswordLock = lockPassword.getText().toString();
//                editor.putString("inputPasswordLock", inputPasswordLock);
//                editor.commit();
//            }
//            else {
//                setContentView(R.layout.password_verify);
//
//                EditText LockPassword = (EditText)findViewById(R.id.lockPassword);
//                Button btnVerify = (Button) findViewById(R.id.btnVerify);
//
//                savedPasswordLock = pref.getString("savedNumLock", "");
//                inputPasswordLock = LockPassword.getText().toString();
//
//                btnVerify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (savedPasswordLock.equals(inputPasswordLock)){
//                            setContentView(R.layout.lockscreen);
//                            Toast.makeText(LockScreenActivity.this, "Correct number code", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(LockScreenActivity.this, "Incorrect number code", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            }
//        }
    }

    public void getLockType(int a) {
        if (a == 0) {
            inputNumLock = "";
            inputPatternLock = "";
            inputPasswordLock = "";
        } else if (a == 1) {
            inputNumLock = lockNumber1.getText().toString() + lockNumber2.getText().toString() + lockNumber3.getText().toString() + lockNumber4.getText().toString();

        } else if (a == 2) {
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {
                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    inputPatternLock = PatternLockUtils.patternToString(mPatternLockView, pattern);
                    TextView textView = (TextView)findViewById(R.id.textView);
                }
                @Override
                public void onCleared() {

                }
            });
        } else if (a == 3) {
            inputPasswordLock = lockPassword.getText().toString();
        }
    }
    public void getScreenLayout(){
        setContentView(R.layout.lockscreen);

        lockType = 0;
        lockTypeString = Integer.toString(lockType);

        noteView = (TextView) findViewById(R.id.noteView);
        noteView2 = (TextView) findViewById(R.id.noteView2);
        lockNumber1 = (EditText) findViewById(R.id.lockNumber1);
        lockNumber2 = (EditText) findViewById(R.id.lockNumber2);
        lockNumber3 = (EditText) findViewById(R.id.lockNumber3);
        lockNumber4 = (EditText) findViewById(R.id.lockNumber4);
        lockPassword = (EditText) findViewById(R.id.lockPassword);

        btnSaveLock = (Button) findViewById(R.id.btnSaveLock);
        btnNoLock = (Button) findViewById(R.id.btnNoLock);
        btnNumberLock = (Button) findViewById(R.id.btnNumberLock);
        btnPatternLock = (Button) findViewById(R.id.btnPatternLock);
        btnPasswordLock = (Button) findViewById(R.id.btnPasswordLock);

        mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);

        btnNoLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockType = 0;
                noteView.setVisibility(View.VISIBLE);
                noteView2.setVisibility(View.VISIBLE);

                lockNumber1.setVisibility(View.INVISIBLE);
                lockNumber2.setVisibility(View.INVISIBLE);
                lockNumber3.setVisibility(View.INVISIBLE);
                lockNumber4.setVisibility(View.INVISIBLE);
                mPatternLockView.setVisibility(View.INVISIBLE);
                lockPassword.setVisibility(View.INVISIBLE);

            }
        });

        btnNumberLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockType = 1;
                lockNumber1.setVisibility(View.VISIBLE);
                lockNumber2.setVisibility(View.VISIBLE);
                lockNumber3.setVisibility(View.VISIBLE);
                lockNumber4.setVisibility(View.VISIBLE);

                noteView.setVisibility(View.INVISIBLE);
                noteView2.setVisibility(View.INVISIBLE);
                mPatternLockView.setVisibility(View.INVISIBLE);
                lockPassword.setVisibility(View.INVISIBLE);
            }
        });

        btnPatternLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockType = 2;
                mPatternLockView.setVisibility(View.VISIBLE);

                noteView.setVisibility(View.INVISIBLE);
                noteView2.setVisibility(View.INVISIBLE);
                lockNumber1.setVisibility(View.INVISIBLE);
                lockNumber2.setVisibility(View.INVISIBLE);
                lockNumber3.setVisibility(View.INVISIBLE);
                lockNumber4.setVisibility(View.INVISIBLE);
                lockPassword.setVisibility(View.INVISIBLE);
            }
        });

        btnPasswordLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockType = 3;
                lockPassword.setVisibility(View.VISIBLE);

                noteView.setVisibility(View.INVISIBLE);
                noteView2.setVisibility(View.INVISIBLE);
                lockNumber1.setVisibility(View.INVISIBLE);
                lockNumber2.setVisibility(View.INVISIBLE);
                lockNumber3.setVisibility(View.INVISIBLE);
                lockNumber4.setVisibility(View.INVISIBLE);
                mPatternLockView.setVisibility(View.INVISIBLE);
            }
        });


        btnSaveLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockType == 0) {
                    getLockType(lockType);
                    Paper.book().write(savedNumLock, "");
                    Paper.book().write(savedPatternLock, "");
                    Paper.book().write(savedPasswordLock, "");
                    Toast.makeText(LockScreenActivity.this, "No locking mechanism", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (lockType == 1) {
                    getLockType(lockType);
                    Paper.book().write(savedNumLock, inputNumLock);
                    Paper.book().write(savedPatternLock, "");
                    Paper.book().write(savedPasswordLock, "");

                    Toast.makeText(LockScreenActivity.this, "Number code saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (lockType == 2) {
                    getLockType(lockType);
                    if(inputPatternLock.isEmpty()){
                        Toast.makeText(LockScreenActivity.this, "Confirm Pattern", Toast.LENGTH_SHORT).show();
                        mPatternLockView.clearPattern();
                    }else{
                        Paper.book().write(savedNumLock, "");
                        Paper.book().write(savedPatternLock, inputPatternLock);
                        Paper.book().write(savedPasswordLock, "");
                        Toast.makeText(LockScreenActivity.this, "Pattern saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (lockType == 3) {
                    getLockType(lockType);
                    Paper.book().write(savedNumLock, "");
                    Paper.book().write(savedPatternLock, "");
                    Paper.book().write(savedPasswordLock, inputPasswordLock);
                    Toast.makeText(LockScreenActivity.this, "Password saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(policeLoggedIn.equals("true")){
            startActivity(new Intent(this,PoliceHomeActivity.class));
        }
        else{
            startActivity(new Intent(this,HomeActivity.class));
        }
    }
}
