package com.yawar.memo.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yawar.memo.R;

import me.mutasem.slidetoanswer.SwipeToAnswerView;

public class ReceivedPhoneCall extends AppCompatActivity {

    ImageView callingUserImg;
    TextView callingUserName;

    SwipeToAnswerView answer, decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_phone_call);

        initViews();
        initActions();


    }

    private void initActions() {


        answer.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                //TODO : PERFORM WHEN ANSWER
                Toast.makeText(ReceivedPhoneCall.this, "Answer", Toast.LENGTH_LONG).show();
                decline.stopAnimation();
                answer.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
            }
        });
        decline.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                //TODO : PERFORM WHEN REFUSE/DECLINE
                Toast.makeText(ReceivedPhoneCall.this, "Decline", Toast.LENGTH_LONG).show();
                answer.stopAnimation();
                answer.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
            }
        });

    }

    private void initViews() {

        callingUserImg = findViewById(R.id.iv_calling_user_img);
        callingUserName = findViewById(R.id.tv_user_calling_name);
        answer = findViewById(R.id.answer);
        decline = findViewById(R.id.decline);
    }
}


