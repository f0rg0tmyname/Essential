package com.android.retail.hawk;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import static com.android.retail.hawk.DatabaseQueries.currentUser;

public class SendOTPActivity extends AppCompatActivity {

    private LinearLayout continueBtn;
    private TextView tAndCBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_o_t_p);

        if (currentUser != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        final TextInputEditText phoneNumber = (TextInputEditText) findViewById(R.id.phone_number);

        continueBtn = findViewById(R.id.send_otp_continue_btn);
        tAndCBtn = findViewById(R.id.t_and_c_btn);

        continueBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#50000000")));

        phoneNumber.setText("+91");
        Selection.setSelection(phoneNumber.getText(), phoneNumber.getText().length());

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91")) {
                    phoneNumber.setText("+91");
                    Selection.setSelection(phoneNumber.getText(), phoneNumber.getText().length());
                }

                if (s.toString().length() == 13) {
                    continueBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff4600")));
                } else if (s.toString().length() < 13) {
                    continueBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = phoneNumber.getText().toString();
                if (num.length() == 13) {

                    Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                    intent.putExtra("mobNumber", num);
                    startActivity(intent);
                }else {
                    Snackbar.make(v, "Please enter a valid mobile number", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        continueBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        AnimatorSet reducer = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.reduce_size);
                        reducer.setTarget(continueBtn);
                        reducer.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        AnimatorSet regain = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.regain_size);
                        regain.setTarget(continueBtn);
                        regain.start();
                        break;
                }
                return false;
            }
        });

        tAndCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse("https://business.paytm.com/payment-gateway"));
                startActivity(httpIntent);
            }
        });

    }
}