package com.example.wordleapp.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wordleapp.BuildConfig;
import com.example.wordleapp.R;
import com.example.wordleapp.utils.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    AppCompatButton nextGameButton;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtWinLose = findViewById(R.id.txt_win_lose);
        nextGameButton = findViewById(R.id.nextGame);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Initialize buttons// Ensure the ID matches your layout file

        setupGridTextViews();
        setupKeyboard();
        fetchRandomWord();
        setupSubmitAndBackspace();
        final MediaPlayer popSound = MediaPlayer.create(this, R.raw.pop);


        nextGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popSound.start();
                resetGame();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Get the selected item's ID

        if (id == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.roles) {
            Intent intent1 = new Intent(MainActivity.this, Roles.class);
            startActivity(intent1);
            finish();
            return true;
        }
        if (id == R.id.logout) {
            handleLogout();
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }


    private void handleLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "http://192.168.81.194/wordle_app/logout.php";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                if (response.equals("success")) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("WordleApp", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("logged", "");
                                    editor.putString("username", "");
                                    editor.putString("email", "");
                                    editor.putString("apiKey", "");
                                    editor.apply();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> {
                                error.printStackTrace();
                                Toast.makeText(MainActivity.this, "Error logging out: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            SharedPreferences sharedPreferences = getSharedPreferences("WordleApp", MODE_PRIVATE);
                            Map<String, String> params = new HashMap<>();
                            params.put("email", sharedPreferences.getString("email", ""));
                            params.put("apiKey", sharedPreferences.getString("apiKey", ""));
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                })
                .setNegativeButton("No", null)
                .show();
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

        MediaPlayer popSound = MediaPlayer.create(this, R.raw.pop);
        submit.setOnClickListener(v -> {
            popSound.start();
            checkGuess();
        });
    }

    @SuppressLint("SetTextI18n")
    private void checkGuess() {
        if (currentGuess.length() < 5) {
            Toast.makeText(this, "Enter 5 letters", Toast.LENGTH_SHORT).show();
            MediaPlayer warningSound = MediaPlayer.create(this, R.raw.warning);
            warningSound.start();
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
            MediaPlayer winSound = MediaPlayer.create(this, R.raw.win);
            winSound.start();
            disableInput();
            nextGameButton.setVisibility(View.VISIBLE); // Show nextGameButton
        } else if (currentAttempt == maxAttempts - 1) {
            txtWinLose.setVisibility(View.VISIBLE);
            txtWinLose.setText("You Lose! Word: " + targetWord);
            MediaPlayer loseSound = MediaPlayer.create(this, R.raw.lose);
            loseSound.start();
            disableInput();
            nextGameButton.setVisibility(View.VISIBLE); // Show nextGameButton
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

    private void resetGame() {
        // Reset game state
        currentAttempt = 0;
        currentLetterIndex = 0;
        currentGuess.setLength(0);
        txtWinLose.setVisibility(View.GONE);
        nextGameButton.setVisibility(View.GONE);

        // Clear grid and keyboard
        for (List<TextView> row : guessTextViews) {
            for (TextView textView : row) {
                textView.setText("");
                textView.setBackgroundColor(Color.rgb(99, 99, 99));
                textView.setTextColor(Color.WHITE);
                textView.setOnClickListener(v -> handleCellClick(textView));
            }
        }
        for (Button btn : keyboardButtons) {
            btn.setBackgroundColor(Color.parseColor("#636363"));
            btn.setOnClickListener(view -> handleKeyPress(btn.getText().toString().charAt(0)));
        }
        // Fetch a new word
        fetchRandomWord();
    }

}

