package com.example.social_distancing_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TutorialPage5Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView mText;
    private ImageButton mExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page5);

        mText = (TextView) findViewById(R.id.tutorial_text);
        mText.setText("Delete a device from the list that you don't want to be whitelisted anymore." +
                " Or type a device's Android ID and press the add button to add it to the list.");

        mExitButton = (ImageButton) findViewById(R.id.exit_btn);
        mExitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit_btn) {
            Intent mSkipIntent = new Intent(TutorialPage5Activity.this,
                    MainActivity.class);
            TutorialPage5Activity.this.startActivity(mSkipIntent);
        }
    }
}