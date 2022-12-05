package com.example.social_distancing_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class WhitelistActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView myListView;
    private EditText myEditText;
    private ImageButton myAddButton;
    private TextView mText;

    private ArrayList<String> myWhiteListArray;

    private FileManager myFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);

        myEditText = (EditText) findViewById(R.id.editText);
        myListView = (ListView) findViewById(R.id.listView);
        mText = (TextView) findViewById(R.id.text);

        myAddButton = (ImageButton) findViewById(R.id.add_btn);
        myAddButton.setOnClickListener(this);

        mText.setText("Add a device so that you are not notified when you are getting closer " +
                "than what social distancing requires.");

        myFileManager = new FileManager(this);
        myFileManager.checkFileExistence();

        try {
            myWhiteListArray = myFileManager.loadWhiteListFromFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        updateList();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_btn) {
            if(myEditText.getText().toString().length() == 16) {
                myWhiteListArray.add(myEditText.getText().toString());
                myEditText.setText("");
                updateList();
                Toast.makeText(getBaseContext(), "Added to whitelist", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getBaseContext(), "Too Short", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateList() {
        myListView.setAdapter(new MyWhitelistAdapter(myWhiteListArray, this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        myFileManager.saveWhiteListToFile(myWhiteListArray);
    }
}