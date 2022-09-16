package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private TextView phoneText, resendOtpBtn;
    private EditText otpEditText;
    private LinearLayout verifyBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String num = getIntent().getStringExtra("mobNumber");

        phoneText = findViewById(R.id.phone_number_text);
        otpEditText = findViewById(R.id.otp_text_field);
        verifyBtn = findViewById(R.id.verify_otp_btn);
        resendOtpBtn = findViewById(R.id.resend_otp_btn);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String mCode = phoneAuthCredential.getSmsCode();
                if (mCode != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                signInWithPhoneAuthCredential(phoneAuthCredential, num);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.GONE);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        sendVerificationCodeToUser(num);

        verifyBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#50000000")));

        phoneText.setText(num);

        otpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    verifyBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00b37b")));
                } else if (s.toString().length() < 6) {
                    verifyBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#50000000")));
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpEditText.getText().toString().length() == 6) {
                    progressBar.setVisibility(View.VISIBLE);
                    verifyPhoneNumberWithCode(num);
                } else {
                    Snackbar.make(v, "Please enter a 6 digit code", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(num);
            }
        });

        verifyBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AnimatorSet reducer = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.reduce_size);
                        reducer.setTarget(verifyBtn);
                        reducer.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        AnimatorSet regain = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.regain_size);
                        regain.setTarget(verifyBtn);
                        regain.start();
                        break;
                }
                return false;
            }
        });
    }

    private void verifyPhoneNumberWithCode(String number) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpEditText.getText().toString());
        signInWithPhoneAuthCredential(credential, number);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential, String number) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        userIsLoggedIn(number);

                    } else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(VerifyOTPActivity.this, "OTP entered might not be correct", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userIsLoggedIn(String num) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Map<String, Object> userData = new HashMap<>();
            userData.put("Mobile Number", num);

            firestore.collection("USERS").document(mAuth.getUid())
                    .set(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                CollectionReference userDataReference = firestore.collection("USERS").document(mAuth.getUid()).collection("USER_DATA");

                                ////maps
                                Map<String, Object> shopListMap = new HashMap<>();
                                shopListMap.put("list_size", (long) 0);

                                Map<String, Object> cartMap = new HashMap<>();
                                cartMap.put("list_size", (long) 0);

                                Map<String, Object> addressesMap = new HashMap<>();
                                addressesMap.put("list_size", (long) 0);
                                ////maps

                                List<String> documentNames = new ArrayList<>();
                                documentNames.add("SHOPPING_LIST");
                                documentNames.add("CART");
                                documentNames.add("ADDRESSES");

                                List<Map<String, Object>> documentFields = new ArrayList<>();
                                documentFields.add(shopListMap);
                                documentFields.add(cartMap);
                                documentFields.add(addressesMap);

                                for (int x = 0; x < documentNames.size(); x++) {
                                    int finalX = x;
                                    userDataReference.document(documentNames.get(x))
                                            .set(documentFields.get(x))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (finalX == documentNames.size() - 1) {
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
                                                        }

                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(VerifyOTPActivity.this, error, Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                }

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(VerifyOTPActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }

    private void sendVerificationCodeToUser(String num) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                num,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void resendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(mResendToken)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}