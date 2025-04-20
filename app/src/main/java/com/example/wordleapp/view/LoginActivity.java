package com.example.wordleapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText textInputEmail, textInputPassword;
    TextView signUp;
    Button loginButton;
    String username, email, password, retypePassword, apiKey;
    TextView textError;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signUp = findViewById(R.id.signUp);
        loginButton = findViewById(R.id.login);
        textInputEmail = findViewById(R.id.loginEmail);
        textInputPassword = findViewById(R.id.logInPassword);
        textError = findViewById(R.id.error);
        progressBar = findViewById(R.id.progressBar);
        sharedPreferences = getSharedPreferences("WordleApp", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getString("logged", "false").equals("true")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textError.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                email = String.valueOf(textInputEmail.getText());
                password = String.valueOf(textInputPassword.getText());

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://192.168.117.194/wordle-app/login.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressBar.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    String message = jsonObject.getString("message");
                                    if (status.equals("success")) {
                                        username = jsonObject.getString("username");
                                        email = jsonObject.getString("email");
                                        apiKey = jsonObject.getString("apiKey");
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("logged", "true");
                                        editor.putString("username", username);
                                        editor.putString("email", email);
                                        editor.putString("apiKey", apiKey);
                                        editor.apply();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        textError.setText(message);
                                        textError.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    textError.setText("Error parsing response");
                                    textError.setVisibility(View.VISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        textError.setText("Error connecting to server");
                        textError.setVisibility(View.VISIBLE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> paramV = new HashMap<>();
                        paramV.put("email", email);
                        paramV.put("password", password);
                        return paramV;
                    }
                };
                queue.add(stringRequest);
            }
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}