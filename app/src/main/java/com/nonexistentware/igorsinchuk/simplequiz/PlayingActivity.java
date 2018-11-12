package com.nonexistentware.igorsinchuk.simplequiz;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nonexistentware.igorsinchuk.simplequiz.Common.Common;
import com.squareup.picasso.Picasso;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener {

    final static long INTERVAL = 1000;
    final static long TIMEOUT = 7000;
    int progressValue = 0;

    CountDownTimer mCountDown;

    int fIndex = 0;
    int score = 0;
    int thisQuestion = 0;
    int totalQuestion;
    int correctAnswer;

    FirebaseDatabase database;
    DatabaseReference question;

    ProgressBar progressBar;
    ImageView questionImg;
    Button btnA, btnB, btnC, btnD;
    TextView txtScore, txtQuestion, questionTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        database = FirebaseDatabase.getInstance();
        question = database.getReference("Question");

        txtScore = (TextView) findViewById(R.id.txtScore);
        txtQuestion = (TextView) findViewById(R.id.txtTotalQuestion);
        questionTxt = (TextView) findViewById(R.id.questionTxt);
        questionImg = (ImageView) findViewById(R.id.questionImg);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnA = (Button) findViewById(R.id.answerBtnA);
        btnB = (Button) findViewById(R.id.answerBtnB);
        btnC = (Button) findViewById(R.id.answerBtnC);
        btnD = (Button) findViewById(R.id.answerBtnD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View view ) {
        mCountDown.cancel();
        if (fIndex < totalQuestion) {
            Button clickedButton = (Button) view;
            if (clickedButton.getText().equals(Common.questionList.get(fIndex).getCorrectAnswer())) {
                score +=10;
                correctAnswer++;
                showQuestion(++fIndex);
            } else {
                Intent intent = new Intent(this, Done.class);
                Bundle dataSend = new Bundle();
                dataSend.putInt("SCORE", score);
                dataSend.putInt("TOTAL", totalQuestion);
                dataSend.putInt("CORRECT", correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }

            txtScore.setText(String.format("%d", score));
        }
    }

    private void showQuestion(int index) {
        if (index < totalQuestion) {
            thisQuestion++;
            txtQuestion.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);

            if (Common.questionList.get(index).getIsImageQuestion().equals("true")) {
                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(questionImg);
                questionImg.setVisibility(View.VISIBLE);
                questionTxt.setVisibility(View.INVISIBLE);
            } else {
                questionTxt.setText(Common.questionList.get(index).getQuestion());

                questionImg.setVisibility(View.VISIBLE);
                questionTxt.setVisibility(View.INVISIBLE);
            }

            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

            mCountDown.start(); //start timer
        } else {
            Intent intent = new Intent(this, Done.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();

        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestion(++fIndex);
            }
        };

        showQuestion(fIndex);
    }
}
