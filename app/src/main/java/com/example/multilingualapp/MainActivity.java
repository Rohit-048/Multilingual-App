package com.example.multilingualapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private TextView responseTextView;
    private TextToSpeech textToSpeech;
    private Spinner languageSpinner;

    private final Map<String, String> languageMap = new HashMap<String, String>() {{
        put("Hindi", "hi-IN");
        put("Bengali", "bn-IN");
        put("Telugu", "te-IN");
        put("Marathi", "mr-IN");
        put("Tamil", "ta-IN");
        put("Urdu", "ur-IN");
        put("Gujarati", "gu-IN");
        put("Malayalam", "ml-IN");
        put("Kannada", "kn-IN");
        put("Punjabi", "pa-IN");
        put("English", "en-IN");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseTextView = findViewById(R.id.responseTextView);
        languageSpinner = findViewById(R.id.languageSpinner);
        Button listenButton = findViewById(R.id.listenButton);

        // Initialize language spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(languageMap.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TextToSpeech", "Language not supported");
                } else {
                    speak("Hi, I am your assistant. How can I help you?");
                }
            } else {
                Log.e("TextToSpeech", "Initialization failed");
            }
        });

        // Set onClickListener for the Listen button
        listenButton.setOnClickListener(v -> startListening());
    }

    private void startListening() {
        String selectedLanguage = languageMap.get(languageSpinner.getSelectedItem().toString());
        Toast.makeText(this, "Listening in " + languageSpinner.getSelectedItem().toString() + "...", Toast.LENGTH_SHORT).show();

        // Create an intent to recognize speech
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage); // Set selected language

        // Display the speech input dialog
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                responseTextView.setText(results.get(0));
                speak(results.get(0));
            }
        } else {
            Toast.makeText(this, "Error occurred during speech recognition", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}