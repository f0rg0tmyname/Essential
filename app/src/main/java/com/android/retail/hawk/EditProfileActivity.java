package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText nameTextField, numTextField;
    private LinearLayout continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameTextField = findViewById(R.id.edit_profile_name);
        numTextField = findViewById(R.id.edit_profile_mob_num);
        continueBtn = findViewById(R.id.edit_profile_continue_btn);

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

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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