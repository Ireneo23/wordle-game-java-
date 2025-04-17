package com.example.wordleapp.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wordleapp.BuildConfig;

// ApiClient.java
public class ApiClient {
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String BASE_URL = "https://api.wordnik.com/v4/words.json/randomWords?limit=1&hasDictionaryDef=true&minLength=5&maxLength=5&api_key=" + API_KEY;

    public static void getRandomWord(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, BASE_URL, listener, errorListener);
        queue.add(request);
    }
}
