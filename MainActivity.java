package com.arifcemal.searchassistant;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        if (isConnected()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Say something...");
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        "Your device does not support speech input.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please check your internet connection!",
                    Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> result;
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Intent viewIntent;
                    String text = result.get(0);

                    String[] words = text.split(" ");
                    String query = "";

                    if ((words[0].toLowerCase().contains("youtube") && words.length >= 1) || words[0].toLowerCase().contains("video") || words[0].toLowerCase().contains("image") || words[0].toLowerCase().contains("news")) {
                        for (int i = 1; i < words.length; i++) {
                            query += words[i] + " ";
                        }
                    }

                    if (words[0].toLowerCase().contains("youtube") || words[0].toLowerCase().contains("video")) {
                        viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("https://www.youtube.com/results?search_query=" + query));
                    } else if (words[0].toLowerCase().contains("image")) {
                        viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("https://www.google.com/search?tbm=isch&q=" + query));
                    } else if (words[0].toLowerCase().contains("news")) {
                        viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("https://www.google.com/search?tbm=nws&q=" + query));
                    } else {
                        viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("https://www.google.com/search?q=" + text));
                    }

                    if (words[0].toLowerCase().contains("youtube") && query.isEmpty()) {
                        viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("https://www.youtube.com/"));
                    }

                    startActivity(viewIntent);
                }
                break;
            }

        }

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}
