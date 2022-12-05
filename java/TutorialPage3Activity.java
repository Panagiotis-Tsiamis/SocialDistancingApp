package com.example.social_distancing_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class TutorialPage3Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView mText;
    private ImageButton mNextButton;
    private Button mSkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page3);

        mText = (TextView) findViewById(R.id.tutorial_text);
        mText.setText("After installation just add the app's button to quick settings panel and " +
                "you are ready to go.\n\nIn order to turn it on both bluetooth and location must" +
                " be enabled!");

        mNextButton = (ImageButton) findViewById(R.id.exit_btn);
        mNextButton.setOnClickListener(this);

        mSkipButton = (Button) findViewById(R.id.skip_btn);
        mSkipButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit_btn) {
            Intent mTutorialIntent = new Intent(TutorialPage3Activity.this,
                    TutorialPage4Activity.class);
            TutorialPage3Activity.this.startActivity(mTutorialIntent);
        }
        if (v.getId() == R.id.skip_btn) {
            Intent mSkipIntent = new Intent(TutorialPage3Activity.this,
                    MainActivity.class);
            TutorialPage3Activity.this.startActivity(mSkipIntent);
        }
    }
}