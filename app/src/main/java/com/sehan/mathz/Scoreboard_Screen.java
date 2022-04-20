package com.sehan.mathz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Scoreboard_Screen extends AppCompatActivity {
    private static final String MARKS = "MARKS";

    private String Marks;

    private TextView mark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard__screen);

        Intent intent =getIntent();
        Marks = intent.getStringExtra(MARKS);

        mark = findViewById(R.id.mark);
        System.out.println(Marks);
        mark.setText(String.format("%s/10", Marks));
    }
    public void submitMark(View v){
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }
}