package com.dna.plank.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dna.plank.R;

public class BeforeMainActivity extends AppCompatActivity {
    private  final int DISPLAY =1000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(BeforeMainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },DISPLAY);
    }
}