package com.sehan.mathz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Question_Screen extends AppCompatActivity {

    private static final String FORMAT = "%02d:%02d";
    private static final String LEVEL = "LEVEL";


    private TextView timer;
    private TextView noOfQuestion;
    private TextView firstNo;
    private TextView secondNo;
    private String Level;
    private int millisInFuture;
    private int bound;
    private int question ;
    private Button nextBtn;
    private CountDownTimer countDownTimer;
    private Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question__screen);

        Intent intent =getIntent();
        Level = intent.getStringExtra(LEVEL);
        question=1;

        switch(Level){
            case "H":
                millisInFuture = 11000;
                bound = 1001;
                break;
            case "M":
                millisInFuture = 16000;
                bound = 101;
                break;
            case "E":
                millisInFuture = 21000;
                bound = 11;
                break;
        }

        timer = findViewById(R.id.timer);
        noOfQuestion = findViewById(R.id.noOfQuestion);
        nextBtn = findViewById(R.id.nextBtn);
        firstNo = findViewById(R.id.firstNo);
        secondNo =  findViewById(R.id.secondNo);
        noOfQuestion.setText(question+"/10");
        r = new Random();
        firstNo.setText(String.valueOf(r.nextInt(bound)));
        secondNo.setText(String.valueOf(r.nextInt(bound)));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.onFinish();
                //goNextQuestion();
            }
        });

        countDownTimer = new CountDownTimer(millisInFuture,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                goNextQuestion();
                Toast.makeText(getApplicationContext(),"Finish",Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void goNextQuestion() {
        if (question<10) {
            question++;
            noOfQuestion.setText(question + "/10");
            firstNo.setText(String.valueOf(r.nextInt(bound)));
            secondNo.setText(String.valueOf(r.nextInt(bound)));
            countDownTimer.start();
        }
    }
}