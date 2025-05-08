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

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    TextInputEditText textInputUsername, textInputEmail, TextInputPassword, TextInputRetypePassword;
    String username, email, password, retypePassword;
    TextView textError, loginText;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        textInputUsername = findViewById(R.id.username);
        textInputEmail = findViewById(R.id.signInAddress);
        TextInputPassword = findViewById(R.id.signInPassword);
        TextInputRetypePassword = findViewById(R.id.retypePassword);
        loginText = findViewById(R.id.loginNow);
        textError = findViewById(R.id.error);
        progressBar = findViewById(R.id.progressBar);
        Button registerButton = findViewById(R.id.btn_submit);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textError.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                username = String.valueOf(textInputUsername.getText());
                email = String.valueOf(textInputEmail.getText());
                password = String.valueOf(TextInputPassword.getText());
                retypePassword = String.valueOf(TextInputRetypePassword.getText());

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url ="http://192.168.81.194/wordle_app/register.php";

                if (!password.equals(retypePassword)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(retypePassword)) {
                    progressBar.setVisibility(View.GONE);
                    textError.setText("Please input all the fields");
                    textError.setVisibility(View.VISIBLE);
                    return;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    textError.setText(response);
                                    textError.setVisibility(View.VISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        textError.setText(error.getLocalizedMessage());
                        textError.setVisibility(View.VISIBLE);
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String, String> paramV = new HashMap<>();
                        paramV.put("username", username);
                        paramV.put("email", email);
                        paramV.put("password", password);
                        return paramV;
                    }
                };
                queue.add(stringRequest);
            }
        });
    }



    private String getCurrentDateTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }
}