package com.dna.plank.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import com.dna.plank.R;

public class Before_Exercise extends AppCompatActivity {
    TextView compre_re;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before__exercise);
        compre_re=(TextView) findViewById(R.id.compte_re);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Before_Exercise.this, CameraActivity.class);
                startActivity(intent);
                finish();
            }
        },10000);

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                compre_re.setText("L'excercice va commencer dans : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
              //  mTextField.setText("done!");
            }
        }.start();

    }
}