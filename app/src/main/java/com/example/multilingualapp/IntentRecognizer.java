package com.example.multilingualapp;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;

public class IntentRecognizer {

    private final ActivityResultLauncher<Intent> startForResult;
    private final Intent intent;

    public IntentRecognizer(ComponentActivity activity, SpeechResultCallback callback) {
        ActivityResultRegistry registry = activity.getActivityResultRegistry();
        startForResult = registry.register("key", new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            callback.onSpeechResult(results);
                        }
                    }
                });

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
    }

    public void startListening(String language) {
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        startForResult.launch(intent);
    }

    public interface SpeechResultCallback {
        void onSpeechResult(ArrayList<String> results);
    }
}
