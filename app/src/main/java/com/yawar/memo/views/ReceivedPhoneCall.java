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

import id.ss564.lib.slidingbutton.SlidingButton;

public class ReceivedPhoneCall extends AppCompatActivity {

    RelativeLayout slide_view, refuse_slide_view;
    SlidingButton slidingButton, refuse_slidingButton;
    ImageView double_arrow, double_arrow2, callingUserImg;
    TextView callingUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_phone_call);

        initViews();
        initActions();


    }

    private void initActions() {

        slidingButton.setOnSlidingListener(v -> {
            double_arrow.setVisibility(View.VISIBLE);
            double_arrow2.setVisibility(View.GONE);
        });

        refuse_slidingButton.setOnSlidingListener(v -> {
            double_arrow.setVisibility(View.GONE);
            double_arrow2.setVisibility(View.VISIBLE);
        });


        slidingButton.setOnStateChangeListener(active -> {
            Log.i("TAG", "onCreate: v  " + active);
            if (active) {
                slidingButton.setVisibility(View.GONE);
                slide_view.setVisibility(View.GONE);
                refuse_slide_view.setVisibility(View.GONE);
                refuse_slidingButton.setVisibility(View.GONE);
                //TODO : PERFORM WHEN ANSWER
                Toast.makeText(ReceivedPhoneCall.this, "Answer", Toast.LENGTH_LONG).show();
            }
        });

        refuse_slidingButton.setOnStateChangeListener(active -> {
            Log.i("TAG", "onCreate: v  " + active);
            if (active) {
                slidingButton.setVisibility(View.GONE);
                slide_view.setVisibility(View.GONE);
                refuse_slide_view.setVisibility(View.GONE);
                refuse_slidingButton.setVisibility(View.GONE);
                //TODO : PERFORM WHEN REFUSE/DECLINE
                Toast.makeText(ReceivedPhoneCall.this, "Decline", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViews() {
        slidingButton = findViewById(R.id.slidingButton);
        refuse_slidingButton = findViewById(R.id.refuse_slidingButton);
        slide_view = findViewById(R.id.slide_view);
        refuse_slide_view = findViewById(R.id.refuse_slide_view);
        double_arrow = findViewById(R.id.double_arrow);
        double_arrow2 = findViewById(R.id.double_arrow2);
        callingUserImg = findViewById(R.id.iv_calling_user_img);
        callingUserName = findViewById(R.id.tv_user_calling_name);
    }
}


