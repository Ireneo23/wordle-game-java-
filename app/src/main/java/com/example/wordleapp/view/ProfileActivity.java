package com.example.wordleapp.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wordleapp.R;

import java.security.cert.CertPathValidatorException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Button backButton;
    TextView usernameTextView, emailTextView, logoutButton, deleteAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        sharedPreferences = getSharedPreferences("WordleApp", MODE_PRIVATE);
        logoutButton = findViewById(R.id.logout);
        usernameTextView = findViewById(R.id.profile_username);
        emailTextView = findViewById(R.id.profileEmail);
        deleteAccount = findViewById(R.id.delete);

        backButton = findViewById(R.id.backButton);
        MediaPlayer popSound = MediaPlayer.create(this, R.raw.pop);

        if (sharedPreferences.getString("logged", "false").equals("false")){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");

        usernameTextView.setText("Username: " + username);
        emailTextView.setText("Email: " + email);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                popSound.start();
                startActivity(intent);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popSound.start();
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                String url = "http://192.168.81.194/wordle_app/logout.php";

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if (response.equals("success")) {
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
                                                    Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> paramV = new HashMap<>();
                                        paramV.put("email", sharedPreferences.getString("email", ""));
                                        paramV.put("apiKey", sharedPreferences.getString("apiKey", ""));
                                        return paramV;
                                    }
                                };
                                queue.add(stringRequest);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popSound.start();
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                String url = "http://192.168.81.194/wordle_app/delete.php";

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if (response.trim().equals("success")) {
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.clear();
                                                    editor.apply();
                                                    Toast.makeText(ProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                                                    // Navigate to LoginActivity
                                                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish(); // Close current activity
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        Toast.makeText(ProfileActivity.this, "Error deleting account: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> paramV = new HashMap<>();
                                        paramV.put("email", sharedPreferences.getString("email", ""));
                                        paramV.put("apiKey", sharedPreferences.getString("apiKey", ""));
                                        return paramV;
                                    }
                                };
                                queue.add(stringRequest);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }
}
