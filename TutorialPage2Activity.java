package com.example.social_distancing_assistant;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class TutorialPage2Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView mText;
    private ImageButton mNextButton;
    private Button mSkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page2);

        mText = (TextView) findViewById(R.id.tutorial_text);
        mText.setText("Social Distancing Assistant requires location permission in order to work.");

        mNextButton = (ImageButton) findViewById(R.id.exit_btn);
        mNextButton.setOnClickListener(this);

        mSkipButton = (Button) findViewById(R.id.skip_btn);
        mSkipButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit_btn) {
            Intent mTutorialIntent = new Intent(TutorialPage2Activity.this,
                    TutorialPage3Activity.class);
            TutorialPage2Activity.this.startActivity(mTutorialIntent);
        }
        if (v.getId() == R.id.skip_btn) {
            Intent mSkipIntent = new Intent(TutorialPage2Activity.this,
                    MainActivity.class);
            TutorialPage2Activity.this.startActivity(mSkipIntent);
        }
    }
}