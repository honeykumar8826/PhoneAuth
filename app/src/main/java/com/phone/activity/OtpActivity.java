package com.phone.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.phone.R;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    public static final long TIME_SECOND = 60;
    public static final String TAG = "OtpActivity";
    private EditText etMobile, etVerificationCode;
    private Button sendOtp;
    private String mobileNum, verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        //        initialize the id
        inItId();
        context = OtpActivity.this;
        // firebase auth intialization
        mAuth = FirebaseAuth.getInstance();
        // FirebaseApp.initializeApp(OtpActivity.this);
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                get the value from the user
                getUserInput();
                if (!mobileNum.isEmpty()) {
//                      send the post reqest on the server
                    sendSms();
                } else {
                    Toast.makeText(OtpActivity.this, "Fill all field", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        initialize the phoneAuthProvider
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    etVerificationCode.setText(code);
                    verifyCode(code);
                }
                Log.i(TAG, "onVerificationCompleted: " + code);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(context, "failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                Log.i(TAG, "onCodeSent: " + verificationId);
            }
        };
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithMobile(credential);
    }

    private void sendSms() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mobileNum, TIME_SECOND, TimeUnit.SECONDS, OtpActivity.this, mCallBack);
        Log.i("as", "sendSms: ");
    }


    private void getUserInput() {
        mobileNum = etMobile.getText().toString();
    }

    private void inItId() {
        etMobile = findViewById(R.id.et_mobile);
        sendOtp = findViewById(R.id.btn_send_otp);
        etVerificationCode = findViewById(R.id.verified_code);
    }

    public void signInWithMobile(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(OtpActivity.this, "Code Verified successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Code Not Verified Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
