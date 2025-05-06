package com.example.wordleapp.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wordleapp.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

// ApiClient.java
public class ApiClient {
    private static final String API_KEY = BuildConfig.API_KEY;

    public static void getRandomWord(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String BASE_URL = "https://api.wordnik.com/v4/words.json/randomWord?minLength=5&maxLength=5&hasDictionaryDef=true&includePartOfSpeech=noun,verb,adjective&excludePartOfSpeech=proper-noun,proper-noun-plural,proper-noun-posessive,family-name&api_key=" + API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, BASE_URL, response -> {
            Log.d("API_RESPONSE", "Raw Response: " + response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                String word = jsonResponse.getString("word");
                if (word.length() == 5 && word.matches("[a-zA-Z]+")) {
                    listener.onResponse(word.toUpperCase());
                } else {
                    errorListener.onErrorResponse(new VolleyError("Invalid word or contains special characters"));
                }
            } catch (JSONException e) {
                Log.e("API_ERROR", "JSON Parsing Error: " + e.getMessage());
                errorListener.onErrorResponse(new VolleyError("Invalid JSON response"));
            }
        }, error -> {
            Log.e("API_ERROR", "Error: " + error.getMessage());
            errorListener.onErrorResponse(error);
        });
        queue.add(request);
    }
}
