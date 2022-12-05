package com.example.social_distancing_assistant;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileManager {

    private Context myContext;

    public FileManager (Context context) {
        this.myContext = context;
    }

    public void checkFileExistence() {

        try {
            FileOutputStream myFos = myContext.openFileOutput("WhiteList.txt",
                    Context.MODE_APPEND);
            OutputStreamWriter myOsw = new OutputStreamWriter(myFos);
            myOsw.write("");
            myOsw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList loadWhiteListFromFile () throws FileNotFoundException {
        ArrayList<String> myArray = new ArrayList<>();

        FileInputStream myFis = myContext.openFileInput("WhiteList.txt");
        InputStreamReader myInputStreamReader =
                new InputStreamReader(myFis, StandardCharsets.UTF_8);
        StringBuilder myStringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(myInputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                myStringBuilder.append(line);
                myArray.add(myStringBuilder.toString().substring(0, 16));
                myStringBuilder = new StringBuilder();
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myArray;
    }

    public void saveWhiteListToFile (ArrayList myArray) {
        try {
            FileOutputStream myFos = myContext.openFileOutput("WhiteList.txt",
                    Context.MODE_PRIVATE);
            OutputStreamWriter myOsw = new OutputStreamWriter(myFos);
            for (int i = 0; i < myArray.size(); i++) {
                myOsw.write(myArray.get(i) + "\n");
            }
            myOsw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
