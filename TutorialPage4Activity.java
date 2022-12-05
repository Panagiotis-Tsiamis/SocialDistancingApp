package com.example.social_distancing_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class TutorialPage4Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView mText;
    private ImageButton mNextButton;
    private Button mSkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page4);

        mText = (TextView) findViewById(R.id.tutorial_text);
        mText.setText("Scan for nearby devices and add them to your whitelist so that you are " +
                "not notified when you are getting closer than what social distancing requires.");

        mNextButton = (ImageButton) findViewById(R.id.exit_btn);
        mNextButton.setOnClickListener(this);

        mSkipButton = (Button) findViewById(R.id.skip_btn);
        mSkipButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit_btn) {
            Intent mTutorialIntent = new Intent(TutorialPage4Activity.this,
                    TutorialPage5Activity.class);
            TutorialPage4Activity.this.startActivity(mTutorialIntent);
        }
        if (v.getId() == R.id.skip_btn) {
            Intent mSkipIntent = new Intent(TutorialPage4Activity.this,
                    MainActivity.class);
            TutorialPage4Activity.this.startActivity(mSkipIntent);
        }
    }
}