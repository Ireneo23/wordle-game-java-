package com.example.wordleapp.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wordleapp.BuildConfig;
import com.example.wordleapp.R;
import com.example.wordleapp.utils.ApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import java.util.List;



public class MainActivity extends AppCompatActivity {

    private String targetWord = "";
    private final int maxAttempts = 6;
    private int currentAttempt = 0;
    private int currentLetterIndex = 0;
    private final StringBuilder currentGuess = new StringBuilder();
    private final List<List<TextView>> guessTextViews = new ArrayList<>();
    private final List<Button> keyboardButtons = new ArrayList<>();
    private TextView txtWinLose;
    private RequestQueue requestQueue;
    private final String API_KEY = BuildConfig.API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button logoutButton = findViewById(R.id.logout);
        txtWinLose = findViewById(R.id.txt_win_lose);
        setupGridTextViews();
        setupKeyboard();
        fetchRandomWord();
        setupSubmitAndBackspace();


        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void fetchRandomWord() {
        ApiClient.getRandomWord(this, response -> {
            Log.d("API", "Response: " + response);
            // Clean the response
            targetWord = response.replace("[", "").replace("]", "").replace("\"", "").toUpperCase();

            // Check if the word length is 5
            if (targetWord.length() != 5) {
                Log.e("API", "Invalid word length: " + targetWord);
                Toast.makeText(this, "Invalid word fetched from API", Toast.LENGTH_SHORT).show();
                fetchRandomWord(); // Retry fetching a valid word
            }
        }, error -> {
            Log.e("API", "Error: " + error.toString()); // Log the error
            Toast.makeText(this, "Failed to fetch word", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupGridTextViews() {
        for (int i = 1; i <= maxAttempts; i++) {
            List<TextView> row = new ArrayList<>();
            for (int j = 1; j <= 5; j++) {
                int id = getResources().getIdentifier("tview_" + i + j, "id", getPackageName());
                TextView textView = findViewById(id);
                textView.setOnClickListener(v -> handleCellClick(textView));
                row.add(textView);
            }
            guessTextViews.add(row);
        }
    }

    private void setupKeyboard() {
        String keys = "QWERTYUIOPASDFGHJKLZXCVBNM";
        for (char c : keys.toCharArray()) {
            int id = getResources().getIdentifier("btn_" + Character.toLowerCase(c), "id", getPackageName());
            Button btn = findViewById(id);
            btn.setOnClickListener(view -> handleKeyPress(c));
            keyboardButtons.add(btn);
        }
    }

    private void handleCellClick(TextView textView) {
        if (currentLetterIndex > 0 && currentAttempt < maxAttempts) {
            currentLetterIndex--;
            currentGuess.deleteCharAt(currentLetterIndex);
            textView.setText("");
        }
    }

    private void handleKeyPress(char letter) {
        if (currentLetterIndex < 5 && currentAttempt < maxAttempts) {
            TextView tv = guessTextViews.get(currentAttempt).get(currentLetterIndex);
            tv.setText(String.valueOf(letter));
            currentGuess.append(letter);
            currentLetterIndex++;
        }
    }

    private void setupSubmitAndBackspace() {
        Button submit = findViewById(R.id.submitBtn);
        Button backspace = findViewById(R.id.btn_backspace);

        submit.setOnClickListener(v -> checkGuess());

        backspace.setOnClickListener(v -> {
            if (currentLetterIndex > 0 && currentAttempt < maxAttempts) {
                currentLetterIndex--;
                currentGuess.deleteCharAt(currentLetterIndex);
                TextView tv = guessTextViews.get(currentAttempt).get(currentLetterIndex);
                tv.setText("");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void checkGuess() {
        if (currentGuess.length() < 5) {
            Toast.makeText(this, "Enter 5 letters", Toast.LENGTH_SHORT).show();
            return;
        }

        String guess = currentGuess.toString().toUpperCase();
        char[] guessChars = guess.toCharArray();
        char[] targetChars = targetWord.toCharArray();
        boolean[] checked = new boolean[5];

        // First pass: exact matches
        for (int i = 0; i < 5; i++) {
            TextView tv = guessTextViews.get(currentAttempt).get(i);
            if (guessChars[i] == targetChars[i]) {
                tv.setBackgroundColor(Color.parseColor("#66BB6A")); // Green
                tv.setTextColor(Color.WHITE);
                updateKeyColor(guessChars[i], "#66BB6A"); // Update keyboard button color to green
                checked[i] = true;
            }
        }

        // Second pass: check letters in wrong place
        for (int i = 0; i < 5; i++) {
            if (guessChars[i] == targetChars[i]) continue;

            TextView tv = guessTextViews.get(currentAttempt).get(i);
            boolean found = false;

            for (int j = 0; j < 5; j++) {
                if (!checked[j] && guessChars[i] == targetChars[j]) {
                    found = true;
                    checked[j] = true;
                    break;
                }
            }

            if (found) {
                tv.setBackgroundColor(Color.parseColor("#FFEB3B")); // Yellow
                tv.setTextColor(Color.BLACK);
                updateKeyColor(guessChars[i], "#FFEB3B"); // Update keyboard button color to yellow
            } else {
                tv.setBackgroundColor(Color.parseColor("#333333")); // Dark Gray
                tv.setTextColor(Color.WHITE);
                updateKeyColor(guessChars[i], "#333333"); // Update keyboard button color to black
            }
        }

        // Check win/loss
        if (guess.equals(targetWord)) {
            txtWinLose.setVisibility(View.VISIBLE);
            txtWinLose.setText("You Win!");
            disableInput();
        } else if (currentAttempt == maxAttempts - 1) {
            txtWinLose.setVisibility(View.VISIBLE);
            txtWinLose.setText("You Lose! Word: " + targetWord);
            disableInput();
        }

        // Prepare for next round
        currentGuess.setLength(0);
        currentLetterIndex = 0;
        currentAttempt++;
    }

    private void disableInput() {
        for (List<TextView> row : guessTextViews) {
            for (TextView textView : row) {
                textView.setOnClickListener(null);
            }
        }
        for (Button btn : keyboardButtons) {
            btn.setOnClickListener(null);
        }
    }

    private void updateKeyColor(char letter, String color) {
        for (Button btn : keyboardButtons) {
            if (btn.getText().toString().equalsIgnoreCase(String.valueOf(letter))) {
                btn.setBackgroundColor(Color.parseColor(color));
            }
        }
    }

}

